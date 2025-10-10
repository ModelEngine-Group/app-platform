"""
Standalone internet search function that can query multiple providers (Exa, Tavily, Linkup)
without coupling to the existing project code. No environment variables are read here; all
API keys must be provided via function parameters.

Returned structure is a dictionary with one key:
- items: list of {fileName, url, text, source, published_date, summary}

Example:

    from nexent.core.tools.standalone_web_search import internet_search

    result = internet_search(
        query="OpenAI o4 mini update",
        api_keys={
            "exa": "EXA_API_KEY",
            "tavily": "TAVILY_API_KEY",
            "linkup": "LINKUP_API_KEY",
        },
        providers=["exa", "tavily", "linkup"],
        max_results_per_provider=5,
    )

    for item in result["items"]:
        print(item["fileName"], item["url"], item["summary"])  # Display URLs and individual summaries
"""
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


def _generate_individual_summary(text: str, max_chars: int = 200) -> str:
    """为单个搜索结果生成独立摘要

    策略：
    - 如果内容较短，直接返回
    - 如果内容较长，提取前几个句子作为摘要
    - 确保摘要不超过最大字符限制
    """
    if not text:
        return ""

    # 如果内容已经很短，直接返回
    if len(text) <= max_chars:
        return text

    # 按句子分割（简单按句号分割）
    sentences = text.split('. ')

    # 收集句子直到达到字符限制
    summary_parts = []
    current_length = 0

    for sentence in sentences:
        sentence = sentence.strip()
        if not sentence:
            continue

        # 确保句子以句号结束
        if not sentence.endswith('.'):
            sentence += '.'

        sentence_length = len(sentence) + 1  # +1 for space

        # 如果添加这个句子会超过限制，且已经有内容，就停止
        if current_length + sentence_length > max_chars and summary_parts:
            break

        summary_parts.append(sentence)
        current_length += sentence_length

    summary = '. '.join(summary_parts)

    # 确保不超过最大字符限制
    if len(summary) > max_chars:
        summary = summary[:max_chars].rstrip() + "…"

    return summary


def _internet_search(
        query: str,
        api_keys: Dict[str, str],
        providers: Optional[Sequence[str]] = None,
        max_results_per_provider: int = 5,
        max_snippet_chars: int = 500,
        max_summary_chars: int = 200,
) -> List[SearchItem]:
    """Run internet search via selected providers and return unified items with individual summaries."""
    selected = list(providers) if providers is not None else []
    if not selected:
        for name in ("exa", "tavily", "linkup"):
            if api_keys.get(name):
                selected.append(name)
    items: List[SearchItem] = []

    # Exa
    if "exa" in selected and api_keys.get("exa"):
        try:
            exa_client = Exa(api_key=api_keys["exa"])
            res = exa_client.search_and_texts(
                query,
                text={"max_characters": 2000},
                livecrawl="always",
                extras={"links": 0, "image_links": 0},
                num_results=max_results_per_provider,
            )
            for i, r in enumerate(getattr(res, "results", [])[:max_results_per_provider]):
                text = _truncate(getattr(r, "content", "") or "", max_snippet_chars)
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
                            "summary": _generate_individual_summary(text, max_summary_chars),
                        }
                    )
                )
        except Exception:
            pass

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
                            "summary": _generate_individual_summary(text, max_summary_chars),
                        }
                    )
                )
        except Exception:
            pass

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
                            "summary": _generate_individual_summary(text, max_summary_chars),
                        }
                    )
                )
        except Exception:
            pass

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
