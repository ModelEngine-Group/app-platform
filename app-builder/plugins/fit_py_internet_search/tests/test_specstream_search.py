import importlib.util
import sys
import types
from pathlib import Path
from unittest.mock import MagicMock, patch


def _load_internet_search():
    # Provide minimal stubs for optional third-party SDKs and fitframework during unit tests
    for mod_name in ("linkup", "tavily", "exa_py"):
        if mod_name not in sys.modules:
            m = types.ModuleType(mod_name)
            m.LinkupClient = MagicMock()
            m.TavilyClient = MagicMock()
            m.Exa = MagicMock()
            sys.modules[mod_name] = m

    if "fitframework" not in sys.modules:
        ff = types.ModuleType("fitframework")
        ff_api = types.ModuleType("fitframework.api")
        ff_dec = types.ModuleType("fitframework.api.decorators")
        ff_dec.fitable = lambda *a, **k: (lambda fn: fn)
        ff_dec.value = lambda *a, **k: (lambda fn: fn)
        ff_log = types.ModuleType("fitframework.api.logging")
        ff_log.sys_plugin_logger = MagicMock()
        ff_core = types.ModuleType("fitframework.core")
        ff_exc_mod = types.ModuleType("fitframework.core.exception")
        ff_fit_exc = types.ModuleType("fitframework.core.exception.fit_exception")

        class _FitException(Exception):
            pass

        class _InternalErrorCode:
            CLIENT_ERROR = "CLIENT_ERROR"

        ff_fit_exc.FitException = _FitException
        ff_fit_exc.InternalErrorCode = _InternalErrorCode
        for name, mod in [
            ("fitframework", ff),
            ("fitframework.api", ff_api),
            ("fitframework.api.decorators", ff_dec),
            ("fitframework.api.logging", ff_log),
            ("fitframework.core", ff_core),
            ("fitframework.core.exception", ff_exc_mod),
            ("fitframework.core.exception.fit_exception", ff_fit_exc),
        ]:
            sys.modules[name] = mod

    mod_path = Path(__file__).resolve().parent.parent / "src" / "internet_search.py"
    spec = importlib.util.spec_from_file_location("internet_search", mod_path)
    module = importlib.util.module_from_spec(spec)
    assert spec and spec.loader
    spec.loader.exec_module(module)
    return module


def test_search_specstream_maps_sources():
    mod = _load_internet_search()
    mod._get_max_summary_length = lambda: 150
    mock_resp = MagicMock()
    mock_resp.raise_for_status.return_value = None
    mock_resp.json.return_value = {
        "sources": [
            {
                "title": "SpecStream Docs",
                "url": "https://specstream.ai/docs",
                "snippet": "Short snippet.",
                "excerpt": "First sentence. Second sentence.",
                "published": "2026-11-20",
            }
        ]
    }
    with patch.object(mod.requests, "post", return_value=mock_resp) as mock_post:
        items = mod._internet_search(
            query="web search api",
            api_keys={"specstream": "test-key"},
            providers=["specstream"],
            max_results_per_provider=3,
        )

    mock_post.assert_called_once()
    assert mock_post.call_args.kwargs["headers"]["Authorization"] == "Bearer test-key"
    assert mock_post.call_args.kwargs["json"] == {"q": "web search api", "num": 3, "passages": True}
    assert len(items) == 1
    assert items[0].metadata["source"] == "specstream"
    assert items[0].metadata["url"] == "https://specstream.ai/docs"
    assert items[0].metadata["fileName"] == "SpecStream Docs"
    assert "First sentence." in items[0].text
