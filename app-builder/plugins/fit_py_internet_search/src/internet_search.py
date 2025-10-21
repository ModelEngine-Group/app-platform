# -- encoding: utf-8 --
# Copyright (c) 2024 Huawei Technologies Co., Ltd. All Rights Reserved.
# This file is a part of the ModelEngine Project.
# Licensed under the MIT License. See License.txt in the project root for license information.
# ======================================================================================================================
import json
import re
from concurrent.futures import ThreadPoolExecutor, as_completed
from dataclasses import dataclass
from typing import Dict, List, Optional, Sequence
from linkup import LinkupClient
from tavily import TavilyClient
from exa_py import Exa

from fitframework.api.decorators import fitable, value
from fitframework.api.logging import sys_plugin_logger
from fitframework.core.exception.fit_exception import FitException, InternalErrorCode


@dataclass
class SearchItem:
    id: str
    text: str
    score: float
    metadata: Dict[str, object]

    def to_dict(self) -> dict:
        """转换为字典，确保所有字段都可序列化"""
        return {
            "id": self.id,
            "text": self.text,
            "score": self.score,
            "metadata": {
                k: (v.isoformat() if hasattr(v, 'isoformat') else v)  # 处理日期等特殊类型
                for k, v in self.metadata.items()
            }
        }

    def to_json(self) -> str:
        """转换为JSON字符串"""
        return json.dumps(self.to_dict(), ensure_ascii=False)

    @classmethod
    def from_dict(cls, data: dict) -> 'SearchItem':
        """从字典创建SearchItem"""
        return cls(
            id=data["id"],
            text=data["text"],
            score=data["score"],
            metadata=data["metadata"]
        )


@value('internet-search.api-key.exa')
def _get_exa_api_key() -> str:
    pass


@value('internet-search.api-key.tavily')
def _get_tavily_api_key() -> str:
    pass


@value('internet-search.api-key.linkup')
def _get_linkup_api_key() -> str:
    pass


@value('internet-search.max_results_per_provider')
def _get_max_results_per_provider() -> int:
    pass


@value('internet-search.summary-length')
def _get_max_summary_length() -> int:
    pass


def _truncate(text: str, max_chars: int) -> str:
    if len(text) <= max_chars:
        return text
    return text[: max_chars - 1].rstrip() + "…"


def _extract_summary(text: str, max_sentences: int = 4) -> str:
    """
    从文本中提取前几句话作为摘要

    Args:
        text: 原始文本
        max_sentences: 最多保留的句子数，默认为4句

    Returns:
        摘要文本
    """
    if not text:
        return ""

    # 使用正则表达式匹配句子结束符号
    sentences = re.split(r'([。！？\.!?]+["\'»\)]?\s*)', text)

    # 重新组合句子（将分隔符和句子内容合并）
    combined_sentences = []
    for i in range(0, len(sentences) - 1, 2):
        sentence = sentences[i]
        separator = sentences[i + 1] if i + 1 < len(sentences) else ""
        combined = (sentence + separator).strip()
        if combined:
            combined_sentences.append(combined)

    # 如果最后一个元素没有分隔符
    if len(sentences) % 2 == 1 and sentences[-1].strip():
        combined_sentences.append(sentences[-1].strip())

    # 取前 max_sentences 句
    if len(combined_sentences) <= max_sentences:
        summary = " ".join(combined_sentences)
    else:
        summary = " ".join(combined_sentences[:max_sentences])

    # 确保摘要不会过长（最多150字符）
    if len(summary) > _get_max_summary_length():
        summary = summary[:(_get_max_summary_length() - 3)].rstrip() + "..."

    return summary


def _search_exa(query: str, api_key: str, max_results: int, max_snippet_chars: int) -> List[SearchItem]:
    """在 Exa 中搜索"""
    items: List[SearchItem] = []
    try:
        exa_client = Exa(api_key=api_key)
        res = exa_client.search_and_contents(
            query,
            text={"max_characters": 2000},
            livecrawl="always",
            num_results=max_results,
        )
        for i, r in enumerate(getattr(res, "results", [])[:max_results]):
            text = _truncate(getattr(r, "text", "") or getattr(r, "content", "") or "", max_snippet_chars)
            summary = _extract_summary(text)  # 提取3-4句话作为摘要
            items.append(
                SearchItem(
                    id=getattr(r, "id", "") or f"exa_{i}",
                    text=text,
                    score=12.0,
                    metadata={
                        "fileName": getattr(r, "title", "") or "",
                        "url": getattr(r, "url", "") or "",
                        "source": "exa",
                        "published_date": getattr(r, "published_date", None),
                        "summary": summary,
                    }
                )
            )
    except Exception as e:
        sys_plugin_logger.warning(f'Failed to search in Exa tool: {str(e)}')
    return items


def _search_tavily(query: str, api_key: str, max_results: int, max_snippet_chars: int) -> List[SearchItem]:
    """在 Tavily 中搜索"""
    items: List[SearchItem] = []
    try:
        tavily_client = TavilyClient(api_key=api_key)
        res = tavily_client.search(
            query=query,
            max_results=max_results,
            include_images=False,
        )
        for i, r in enumerate(res.get("results", [])[:max_results]):
            text = _truncate(r.get("content", "") or "", max_snippet_chars)
            summary = _extract_summary(text)  # 提取3-4句话作为摘要
            items.append(
                SearchItem(
                    id=r.get("id", "") or f"tavily_{i}",
                    text=text,
                    score=12.0,
                    metadata={
                        "fileName": r.get("title", "") or "",
                        "url": r.get("url", "") or "",
                        "source": "tavily",
                        "published_date": r.get("published_date"),
                        "summary": summary,
                    }
                )
            )
    except Exception as e:
        sys_plugin_logger.warning(f'Failed to search in Tavily tool: {str(e)}')
    return items


def _search_linkup(query: str, api_key: str, max_results: int, max_snippet_chars: int) -> List[SearchItem]:
    """在 Linkup 中搜索"""
    items: List[SearchItem] = []
    try:
        linkup_client = LinkupClient(api_key=api_key)
        resp = linkup_client.search(
            query=query,
            depth="standard",
            output_type="searchResults",
            include_images=False,
        )
        for i, r in enumerate(getattr(resp, "results", [])[:max_results]):
            text = _truncate(getattr(r, "content", "") or getattr(r, "text", "") or "", max_snippet_chars)
            summary = _extract_summary(text)  # 提取3-4句话作为摘要
            items.append(
                SearchItem(
                    id=getattr(r, "id", "") or f"linkup_{i}",
                    text=text,
                    score=12.0,
                    metadata={
                        "fileName": getattr(r, "name", None) or getattr(r, "title", "") or "",
                        "url": getattr(r, "url", "") or "",
                        "source": "linkup",
                        "published_date": None,
                        "summary": summary,
                    }
                )
            )
    except Exception as e:
        sys_plugin_logger.warning(f'Failed to search in Linkup tool: {str(e)}')
    return items


def _internet_search(
        query: str,
        api_keys: Dict[str, str],
        providers: Optional[Sequence[str]] = None,
        max_results_per_provider: int = 5,
        max_snippet_chars: int = 500,
) -> List[SearchItem]:
    """Run internet search via selected providers and return unified items with individual summaries."""
    selected = list(providers) if providers is not None else []
    if not selected:
        for name in ("exa", "tavily", "linkup"):
            if api_keys.get(name):
                selected.append(name)

    # 准备并行搜索任务
    search_tasks = []
    if "exa" in selected and api_keys.get("exa"):
        search_tasks.append(("exa", _search_exa, api_keys["exa"]))
    if "tavily" in selected and api_keys.get("tavily"):
        search_tasks.append(("tavily", _search_tavily, api_keys["tavily"]))
    if "linkup" in selected and api_keys.get("linkup"):
        search_tasks.append(("linkup", _search_linkup, api_keys["linkup"]))

    # 使用线程池并行执行搜索
    items: List[SearchItem] = []
    errors = []

    with ThreadPoolExecutor(max_workers=len(search_tasks)) as executor:
        # 提交所有搜索任务
        future_to_provider = {
            executor.submit(task_func, query, api_key, max_results_per_provider, max_snippet_chars): provider_name
            for provider_name, task_func, api_key in search_tasks
        }

        # 收集结果
        for future in as_completed(future_to_provider):
            provider_name = future_to_provider[future]
            try:
                results = future.result()
                if results:
                    items.extend(results)
                else:
                    errors.append(provider_name)
            except Exception as e:
                sys_plugin_logger.error(f'Unexpected error in {provider_name} search: {str(e)}')
                errors.append(provider_name)

    # 如果所有搜索都失败了，才抛出异常
    if not items and errors:
        raise FitException(
            InternalErrorCode.CLIENT_ERROR,
            f'All search tools failed: {", ".join(errors)}'
        )

    # 去重逻辑
    seen = set()
    deduped: List[SearchItem] = []
    for it in items:
        key = (it.metadata.get("url") or it.metadata.get("fileName") or it.id).strip()
        if not key or key in seen:
            continue
        seen.add(key)
        deduped.append(it)

    return deduped


# 序列化工具函数
def serialize_search_items(items: List[SearchItem]) -> List[dict]:
    """将SearchItem列表序列化为字典列表"""
    return [item.to_dict() for item in items]


def deserialize_search_items(data: List[dict]) -> List[SearchItem]:
    """从字典列表反序列化为SearchItem列表"""
    return [SearchItem.from_dict(item) for item in data]


def search_items_to_json(items: List[SearchItem]) -> str:
    """将SearchItem列表转换为JSON字符串"""
    return json.dumps(serialize_search_items(items), ensure_ascii=False, indent=2)


@fitable("Search.Online.tool", "Python_REPL")
def search_online(query: str) -> List[SearchItem]:
    try:
        return _internet_search(
            query=query,
            api_keys={
                "exa": _get_exa_api_key(),
                "tavily": _get_tavily_api_key(),
                "linkup": _get_linkup_api_key(),
            },
            providers=["exa", "tavily", "linkup"],
            max_results_per_provider=_get_max_results_per_provider(),
        )
    except Exception:
        raise FitException(InternalErrorCode.CLIENT_ERROR, 'Failed to search for node information on the network')


def search_items_from_json(json_str: str) -> List[SearchItem]:
    """从JSON字符串解析为SearchItem列表"""
    data = json.loads(json_str)
    return deserialize_search_items(data)
