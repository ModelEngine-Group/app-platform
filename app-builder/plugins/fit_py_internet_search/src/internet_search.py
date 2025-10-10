# -- encoding: utf-8 --
# Copyright (c) 2024 Huawei Technologies Co., Ltd. All Rights Reserved.
# This file is a part of the ModelEngine Project.
# Licensed under the MIT License. See License.txt in the project root for license information.
# ======================================================================================================================
import json
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


def _truncate(text: str, max_chars: int) -> str:
    if len(text) <= max_chars:
        return text
    return text[: max_chars - 1].rstrip() + "…"


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
    items: List[SearchItem] = []
    errors = []  # 记录失败的搜索工具

    # Exa
    if "exa" in selected and api_keys.get("exa"):
        try:
            exa_client = Exa(api_key=api_keys["exa"])
            res = exa_client.search_and_contents(
                query,
                text={"max_characters": 2000},
                livecrawl="always",
                num_results=max_results_per_provider,
            )
            for i, r in enumerate(getattr(res, "results", [])[:max_results_per_provider]):
                text = _truncate(getattr(r, "text", "") or getattr(r, "content", "") or "", max_snippet_chars)
                items.append(
                    SearchItem(
                        id=getattr(r, "id", "") or f"exa_{i}",
                        text=text,
                        score=12.0,  # 使用float确保序列化
                        metadata={
                            "fileName": getattr(r, "title", "") or "",
                            "url": getattr(r, "url", "") or "",
                            "source": "exa",
                            "published_date": getattr(r, "published_date", None),
                            "summary": text,
                        }
                    )
                )
        except Exception as e:
            sys_plugin_logger.warning(f'Failed to search in Exa tool: {str(e)}')
            errors.append("exa")

    # Tavily
    if "tavily" in selected and api_keys.get("tavily"):
        try:
            tavily_client = TavilyClient(api_key=api_keys["tavily"])
            res = tavily_client.search(
                query=query,
                max_results=max_results_per_provider,
                include_images=False,
            )
            for i, r in enumerate(res.get("results", [])[:max_results_per_provider]):
                text = _truncate(r.get("content", "") or "", max_snippet_chars)
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
                            "summary": text,
                        }
                    )
                )
        except Exception as e:
            sys_plugin_logger.warning(f'Failed to search in Tavily tool: {str(e)}')
            errors.append("tavily")

    # Linkup
    if "linkup" in selected and api_keys.get("linkup"):
        try:
            linkup_client = LinkupClient(api_key=api_keys["linkup"])
            resp = linkup_client.search(
                query=query,
                depth="standard",
                output_type="searchResults",
                include_images=False,
            )
            for i, r in enumerate(getattr(resp, "results", [])[:max_results_per_provider]):
                text = _truncate(getattr(r, "content", "") or getattr(r, "text", "") or "", max_snippet_chars)
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
                            "summary": text,
                        }
                    )
                )
        except Exception as e:
            sys_plugin_logger.warning(f'Failed to search in Linkup tool: {str(e)}')
            errors.append("linkup")
    
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
