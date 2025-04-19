(() => {
    "use strict";
    var e,
    t,
    r,
    n = {
        1333: (e, t, r) => {
            var n = r(7294),
            o = r(745),
            a = (0, n.createContext)({
                data: {},
                terminateClick: function (e) {},
                resumingClick: function (e) {},
                restartClick: function (e) {}
            }),
            i = r(6170),
            l = r(7079),
            c = r(0),
            u = r(9381),
            f = r(6114),
            s = r(7124),
            m = r(1577),
            d = r(1133),
            p = r(7381),
            y = r(3449),
            b = r(5019),
            g = r(7119),
            v = r(4545),
            w = r(9101),
            h = r(5035),
            A = r(3379),
            O = r.n(A),
            j = r(1399);
            function E(e) {
                return E = "function" == typeof Symbol && "symbol" == typeof Symbol.iterator ? function (e) {
                    return typeof e
                }
                 : function (e) {
                    return e && "function" == typeof Symbol && e.constructor === Symbol && e !== Symbol.prototype ? "symbol" : typeof e
                },
                E(e)
            }
            function C(e, t) {
                var r = Object.keys(e);
                if (Object.getOwnPropertySymbols) {
                    var n = Object.getOwnPropertySymbols(e);
                    t && (n = n.filter((function (t) {
                                    return Object.getOwnPropertyDescriptor(e, t).enumerable
                                }))),
                    r.push.apply(r, n)
                }
                return r
            }
            function x(e) {
                for (var t = 1; t < arguments.length; t++) {
                    var r = null != arguments[t] ? arguments[t] : {};
                    t % 2 ? C(Object(r), !0).forEach((function (t) {
                            S(e, t, r[t])
                        })) : Object.getOwnPropertyDescriptors ? Object.defineProperties(e, Object.getOwnPropertyDescriptors(r)) : C(Object(r)).forEach((function (t) {
                            Object.defineProperty(e, t, Object.getOwnPropertyDescriptor(r, t))
                        }))
                }
                return e
            }
            function S(e, t, r) {
                return (t = function (e) {
                    var t = function (e) {
                        if ("object" != E(e) || !e)
                            return e;
                        var t = e[Symbol.toPrimitive];
                        if (void 0 !== t) {
                            var r = t.call(e, "string");
                            if ("object" != E(r))
                                return r;
                            throw new TypeError("@@toPrimitive must return a primitive value.")
                        }
                        return String(e)
                    }
                    (e);
                    return "symbol" == E(t) ? t : t + ""
                }
                    (t))in e ? Object.defineProperty(e, t, {
                    value: r,
                    enumerable: !0,
                    configurable: !0,
                    writable: !0
                }) : e[t] = r,
                e
            }
            function I(e, t) {
                return function (e) {
                    if (Array.isArray(e))
                        return e
                }
                (e) || function (e, t) {
                    var r = null == e ? null : "undefined" != typeof Symbol && e[Symbol.iterator] || e["@@iterator"];
                    if (null != r) {
                        var n,
                        o,
                        a,
                        i,
                        l = [],
                        c = !0,
                        u = !1;
                        try {
                            if (a = (r = r.call(e)).next, 0 === t) {
                                if (Object(r) !== r)
                                    return;
                                c = !1
                            } else
                                for (; !(c = (n = a.call(r)).done) && (l.push(n.value), l.length !== t); c = !0);
                        } catch (e) {
                            u = !0,
                            o = e
                        } finally {
                            try {
                                if (!c && null != r.return && (i = r.return(), Object(i) !== i))
                                    return
                            } finally {
                                if (u)
                                    throw o
                            }
                        }
                        return l
                    }
                }
                (e, t) || function (e, t) {
                    if (e) {
                        if ("string" == typeof e)
                            return M(e, t);
                        var r = {}
                        .toString.call(e).slice(8, -1);
                        return "Object" === r && e.constructor && (r = e.constructor.name),
                        "Map" === r || "Set" === r ? Array.from(e) : "Arguments" === r || /^(?:Ui|I)nt(?:8|16|32)(?:Clamped)?Array$/.test(r) ? M(e, t) : void 0
                    }
                }
                (e, t) || function () {
                    throw new TypeError("Invalid attempt to destructure non-iterable instance.\nIn order to be iterable, non-array objects must have a [Symbol.iterator]() method.")
                }
                ()
            }
            function M(e, t) {
                (null == t || t > e.length) && (t = e.length);
                for (var r = 0, n = Array(t); r < t; r++)
                    n[r] = e[r];
                return n
            }
            O()(j.Z, {
                insert: "head",
                singleton: !1
            }),
            j.Z.locals;
            var k = i.Z.Title,
            N = i.Z.Text,
            D = l.Z.confirm;
            const T = function () {
                var e = (0, n.useContext)(a),
                t = e.data,
                r = e.resumingClick,
                o = (e.terminateClick, I((0, n.useState)([]), 2)),
                i = o[0],
                A = o[1],
                O = I((0, n.useState)(!1), 2),
                j = O[0],
                E = O[1],
                C = I((0, n.useState)({
                            modelName: "",
                            baseUrl: "",
                            apiKey: ""
                        }), 2),
                S = C[0],
                M = C[1],
                T = function (e) {
                    var t;
                    return {
                        modelName: e.modelName || "",
                        modelId: e.modelId || "",
                        baseUrl: e.baseUrl || "",
                        userId: e.userId || "",
                        isDefault: null !== (t = e.isDefault) && void 0 !== t ? t : 0,
                        apiKey: e.apiKey || ""
                    }
                };
                (0, n.useEffect)((function () {
                        if (Array.isArray(null == t ? void 0 : t.models)) {
                            var e = t.models.map((function (e, t) {
                                        return x(x({}, e), {}, {
                                            serial: t + 1
                                        })
                                    }));
                            A(e)
                        }
                    }), [t]);
                var P = [{
                        title: "ID",
                        dataIndex: "serial",
                        key: "serial",
                        align: "center",
                        width: 60
                    }, {
                        title: "模型名称",
                        dataIndex: "modelName",
                        key: "modelName",
                        align: "center",
                        render: function (e) {
                            return n.createElement(u.Z, {
                                title: e
                            }, n.createElement("div", {
                                    style: {
                                        maxWidth: "300px",
                                        whiteSpace: "nowrap",
                                        overflow: "hidden",
                                        textOverflow: "ellipsis",
                                        margin: "0 auto"
                                    }
                                }, e))
                        }
                    }, {
                        title: "Base URL",
                        dataIndex: "baseUrl",
                        key: "baseUrl",
                        align: "center",
                        render: function (e) {
                            return n.createElement(u.Z, {
                                title: e
                            }, n.createElement("div", {
                                    style: {
                                        maxWidth: "300px",
                                        whiteSpace: "nowrap",
                                        overflow: "hidden",
                                        textOverflow: "ellipsis",
                                        margin: "0 auto"
                                    }
                                }, e))
                        }
                    }, {
                        title: "是否默认",
                        dataIndex: "isDefault",
                        key: "isDefault",
                        align: "center",
                        render: function (e) {
                            return 1 === e ? "是" : "否"
                        }
                    }, {
                        title: "操作",
                        key: "action",
                        align: "center",
                        width: 80,
                        render: function (e, t) {
                            var o = [1 !== t.isDefault && {
                                    key: "switch",
                                    label: "设为默认模型",
                                    onClick: function () {
                                        return function (e) {
                                            D({
                                                title: "切换默认模型",
                                                icon: n.createElement(g.Z, null),
                                                content: "是否将 “".concat(e.modelName, "” 设置为默认模型？"),
                                                okText: "确认",
                                                cancelText: "取消",
                                                onOk: function () {
                                                    var t = T(x(x({}, e), {}, {
                                                                apiKey: ""
                                                            }));
                                                    r({
                                                        params: {
                                                            action: "switch",
                                                            info: t
                                                        }
                                                    })
                                                }
                                            })
                                        }
                                        (t)
                                    }
                                }, {
                                    key: "delete",
                                    label: "删除模型",
                                    onClick: function () {
                                        return function (e) {
                                            D({
                                                title: "删除确认",
                                                icon: n.createElement(g.Z, null),
                                                content: n.createElement(n.Fragment, null, n.createElement("p", null, "你确定要删除这个模型吗？"), n.createElement("p", null, n.createElement("strong", null, "模型名称："), e.modelName)),
                                                okText: "确认删除",
                                                cancelText: "取消",
                                                okType: "danger",
                                                onOk: function () {
                                                    var t = T(x(x({}, e), {}, {
                                                                apiKey: ""
                                                            }));
                                                    r({
                                                        params: {
                                                            action: "delete",
                                                            info: t
                                                        }
                                                    })
                                                }
                                            })
                                        }
                                        (t)
                                    }
                                }
                            ].filter(Boolean);
                            return n.createElement(f.Z, {
                                overlay: n.createElement(s.Z, {
                                    items: o
                                }),
                                trigger: ["click"]
                            }, n.createElement(m.Z, {
                                    icon: n.createElement(v.Z, null)
                                }))
                        }
                    }
                ];
                return n.createElement("div", {
                    className: "form-wrap",
                    style: {
                        padding: "24px",
                        background: "#f5f5f5"
                    }
                }, n.createElement(d.Z, {
                        bordered: !1,
                        style: {
                            maxWidth: 1200,
                            margin: "0 auto"
                        }
                    }, n.createElement(k, {
                            level: 3,
                            style: {
                                textAlign: "center"
                            }
                        }, "模型管理"), n.createElement(N, {
                            type: "secondary",
                            style: {
                                display: "block",
                                textAlign: "center",
                                marginBottom: 24
                            }
                        }, "以下是系统中已注册的模型信息，可进行添加或退出。"), n.createElement(p.Z, {
                            columns: P,
                            dataSource: i,
                            rowKey: "serial",
                            pagination: !1,
                            bordered: !0,
                            size: "middle",
                            locale: {
                                emptyText: "暂无模型数据，请点击“添加模型”"
                            }
                        }), n.createElement("div", {
                            style: {
                                marginTop: 32,
                                textAlign: "center"
                            }
                        }, n.createElement(y.Z, {
                                size: "large"
                            }, n.createElement(m.Z, {
                                    type: "primary",
                                    icon: n.createElement(w.Z, null),
                                    onClick: function () {
                                        return E(!0)
                                    }
                                }, "添加模型"), n.createElement(m.Z, {
                                    icon: n.createElement(h.Z, null),
                                    onClick: function () {
                                        var e = T({});
                                        r({
                                            params: {
                                                action: "quit",
                                                info: e
                                            }
                                        })
                                    }
                                }, "退出")))), n.createElement(l.Z, {
                        title: "添加新模型",
                        open: j,
                        onOk: function () {
                            var e,
                            n = S.modelName,
                            o = S.baseUrl,
                            a = S.apiKey;
                            if (n.trim() && o.trim() && a.trim()) {
                                var i = (null == t || null === (e = t.models) || void 0 === e || null === (e = e[0]) || void 0 === e ? void 0 : e.userId) || "",
                                l = T({
                                    userId: i,
                                    modelName: n,
                                    baseUrl: o,
                                    apiKey: a
                                });
                                r({
                                    params: {
                                        action: "add",
                                        info: l
                                    }
                                }),
                                E(!1),
                                M({
                                    modelName: "",
                                    baseUrl: "",
                                    apiKey: ""
                                })
                            } else
                                c.ZP.warning("请填写完整模型信息")
                        },
                        onCancel: function () {
                            return E(!1)
                        },
                        okText: "确认",
                        cancelText: "取消"
                    }, n.createElement("div", {
                            style: {
                                marginBottom: 12
                            }
                        }, n.createElement("label", null, "模型名称："), n.createElement(b.Z, {
                                maxLength: 100,
                                showCount: !0,
                                value: S.modelName,
                                onChange: function (e) {
                                    return M(x(x({}, S), {}, {
                                            modelName: e.target.value
                                        }))
                                },
                                placeholder: "请输入模型名称"
                            })), n.createElement("div", {
                            style: {
                                marginBottom: 12
                            }
                        }, n.createElement("label", null, "API Key："), n.createElement(b.Z, {
                                value: S.apiKey,
                                onChange: function (e) {
                                    return M(x(x({}, S), {}, {
                                            apiKey: e.target.value
                                        }))
                                },
                                placeholder: "请输入 API Key"
                            })), n.createElement("div", null, n.createElement("label", null, "Base URL："), n.createElement(b.Z, {
                                maxLength: 200,
                                showCount: !0,
                                value: S.baseUrl,
                                onChange: function (e) {
                                    return M(x(x({}, S), {}, {
                                            baseUrl: e.target.value
                                        }))
                                },
                                placeholder: "请输入 Base URL"
                            }))))
            };
            function P(e) {
                return P = "function" == typeof Symbol && "symbol" == typeof Symbol.iterator ? function (e) {
                    return typeof e
                }
                 : function (e) {
                    return e && "function" == typeof Symbol && e.constructor === Symbol && e !== Symbol.prototype ? "symbol" : typeof e
                },
                P(e)
            }
            function L(e, t) {
                var r = Object.keys(e);
                if (Object.getOwnPropertySymbols) {
                    var n = Object.getOwnPropertySymbols(e);
                    t && (n = n.filter((function (t) {
                                    return Object.getOwnPropertyDescriptor(e, t).enumerable
                                }))),
                    r.push.apply(r, n)
                }
                return r
            }
            function Z(e) {
                for (var t = 1; t < arguments.length; t++) {
                    var r = null != arguments[t] ? arguments[t] : {};
                    t % 2 ? L(Object(r), !0).forEach((function (t) {
                            B(e, t, r[t])
                        })) : Object.getOwnPropertyDescriptors ? Object.defineProperties(e, Object.getOwnPropertyDescriptors(r)) : L(Object(r)).forEach((function (t) {
                            Object.defineProperty(e, t, Object.getOwnPropertyDescriptor(r, t))
                        }))
                }
                return e
            }
            function B(e, t, r) {
                return (t = function (e) {
                    var t = function (e) {
                        if ("object" != P(e) || !e)
                            return e;
                        var t = e[Symbol.toPrimitive];
                        if (void 0 !== t) {
                            var r = t.call(e, "string");
                            if ("object" != P(r))
                                return r;
                            throw new TypeError("@@toPrimitive must return a primitive value.")
                        }
                        return String(e)
                    }
                    (e);
                    return "symbol" == P(t) ? t : t + ""
                }
                    (t))in e ? Object.defineProperty(e, t, {
                    value: r,
                    enumerable: !0,
                    configurable: !0,
                    writable: !0
                }) : e[t] = r,
                e
            }
            function U(e, t) {
                (null == t || t > e.length) && (t = e.length);
                for (var r = 0, n = Array(t); r < t; r++)
                    n[r] = e[r];
                return n
            }
            function z() {
                var e,
                t,
                r,
                o = (t = (0, n.useState)({}), r = 2, function (e) {
                    if (Array.isArray(e))
                        return e
                }
                    (t) || function (e, t) {
                    var r = null == e ? null : "undefined" != typeof Symbol && e[Symbol.iterator] || e["@@iterator"];
                    if (null != r) {
                        var n,
                        o,
                        a,
                        i,
                        l = [],
                        c = !0,
                        u = !1;
                        try {
                            if (a = (r = r.call(e)).next, 0 === t) {
                                if (Object(r) !== r)
                                    return;
                                c = !1
                            } else
                                for (; !(c = (n = a.call(r)).done) && (l.push(n.value), l.length !== t); c = !0);
                        } catch (e) {
                            u = !0,
                            o = e
                        } finally {
                            try {
                                if (!c && null != r.return && (i = r.return(), Object(i) !== i))
                                    return
                            } finally {
                                if (u)
                                    throw o
                            }
                        }
                        return l
                    }
                }
                    (t, r) || function (e, t) {
                    if (e) {
                        if ("string" == typeof e)
                            return U(e, t);
                        var r = {}
                        .toString.call(e).slice(8, -1);
                        return "Object" === r && e.constructor && (r = e.constructor.name),
                        "Map" === r || "Set" === r ? Array.from(e) : "Arguments" === r || /^(?:Ui|I)nt(?:8|16|32)(?:Clamped)?Array$/.test(r) ? U(e, t) : void 0
                    }
                }
                    (t, r) || function () {
                    throw new TypeError("Invalid attempt to destructure non-iterable instance.\nIn order to be iterable, non-array objects must have a [Symbol.iterator]() method.")
                }
                    ()),
                i = o[0],
                l = o[1],
                c = (e = window.location.href.match(/uniqueId=([a-zA-Z0-9-]+)/)) && e.length > 1 ? e[1] : null;
                return (0, n.useEffect)((function () {
                        window.addEventListener("message", (function (e) {
                                window.self !== top && l(e.data)
                            })),
                        window.addEventListener("message", (function (e) {
                            }));
                        var e = new ResizeObserver((function (e) {
                                    e.forEach((function (e) {
                                            var t = e.contentRect.height > 900 ? 900 : e.contentRect.height;
                                            window.parent.postMessage({
                                                type: "app-engine-form-resize",
                                                height: t,
                                                uniqueId: c
                                            }, "*")
                                        }))
                                }));
                        return e.observe(document.querySelector("#custom-smart-form")),
                        function () {
                            e.unobserve(document.querySelector("#custom-smart-form")),
                            e.disconnect()
                        }
                    }), []),
                n.createElement("div", {
                    className: "form-wrap",
                    id: "custom-smart-form"
                }, n.createElement(a.Provider, {
                        value: Z(Z({}, i), {}, {
                            terminateClick: function (e) {
                                window.parent.postMessage(Z(Z({
                                            type: "app-engine-form-terminate"
                                        }, e), {}, {
                                        uniqueId: c
                                    }), i.origin)
                            },
                            resumingClick: function (e) {
                                window.parent.postMessage(Z(Z({
                                            type: "app-engine-form-resuming"
                                        }, e), {}, {
                                        uniqueId: c
                                    }), i.origin)
                            },
                            restartClick: function (e) {
                                window.parent.postMessage(Z(Z({
                                            type: "app-engine-form-restart"
                                        }, e), {}, {
                                        uniqueId: c
                                    }), i.origin)
                            }
                        })
                    }, n.createElement(T, null)))
            }
            r(6573),
            o.createRoot(document.getElementById("root")).render(n.createElement(z, null))
        },
        1399: (e, t, r) => {
            r.d(t, {
                Z: () => l
            });
            var n = r(7537),
            o = r.n(n),
            a = r(3645),
            i = r.n(a)()(o());
            i.push([e.id, ".form-wrap{height:100%;overflow-y:auto;padding:24px;box-sizing:border-box}.form-wrap .ant-card{box-shadow:0 2px 8px rgba(0,0,0,.08);border-radius:12px}.form-wrap .ant-table-thead>tr>th{background:#fafafa;font-weight:bold}.form-wrap .ant-btn[shape=circle]:hover{background-color:#ffecec !important;border-radius:50%}", "", {
                        version: 3,
                        sources: ["webpack://./src/styles/form.scss"],
                        names: [],
                        mappings: "AAAA,WAAW,WAAW,CAAC,eAAe,CAAC,YAAY,CAAC,qBAAqB,CAAC,qBAAqB,oCAAoC,CAAC,kBAAkB,CAAC,kCAAkC,kBAAkB,CAAC,gBAAgB,CAAC,wCAAwC,mCAAmC,CAAC,iBAAiB",
                        sourcesContent: [".form-wrap{height:100%;overflow-y:auto;padding:24px;box-sizing:border-box}.form-wrap .ant-card{box-shadow:0 2px 8px rgba(0,0,0,.08);border-radius:12px}.form-wrap .ant-table-thead>tr>th{background:#fafafa;font-weight:bold}.form-wrap .ant-btn[shape=circle]:hover{background-color:#ffecec !important;border-radius:50%}"],
                        sourceRoot: ""
                    }
                ]);
            const l = i
        },
        2445: e => {
            e.exports = "data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMTYiIGhlaWdodD0iMTYiIHZpZXdCb3g9IjAgMCAxNiAxNiIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj48cGF0aCBkPSJNMTQuNSAyLjVoLTEzQS41LjUgMCAwIDAgMSAzdjEwYS41LjUgMCAwIDAgLjUuNWgxM2EuNS41IDAgMCAwIC41LS41VjNhLjUuNSAwIDAgMC0uNS0uNXpNNS4yODEgNC43NWExIDEgMCAwIDEgMCAyIDEgMSAwIDAgMSAwLTJ6bTguMDMgNi44M2EuMTI3LjEyNyAwIDAgMS0uMDgxLjAzSDIuNzY5YS4xMjUuMTI1IDAgMCAxLS4wOTYtLjIwN2wyLjY2MS0zLjE1NmEuMTI2LjEyNiAwIDAgMSAuMTc3LS4wMTZsLjAxNi4wMTZMNy4wOCAxMC4wOWwyLjQ3LTIuOTNhLjEyNi4xMjYgMCAwIDEgLjE3Ny0uMDE2bC4wMTUuMDE2IDMuNTg4IDQuMjQ0YS4xMjcuMTI3IDAgMCAxLS4wMi4xNzV6IiBmaWxsPSIjOEM4QzhDIiBmaWxsLXJ1bGU9Im5vbnplcm8iLz48L3N2Zz4="
        }
    },
    o = {};
    function a(e) {
        var t = o[e];
        if (void 0 !== t)
            return t.exports;
        var r = o[e] = {
            id: e,
            loaded: !1,
            exports: {}
        };
        return n[e](r, r.exports, a),
        r.loaded = !0,
        r.exports
    }
    a.m = n,
    e = [],
    a.O = (t, r, n, o) => {
        if (!r) {
            var i = 1 / 0;
            for (f = 0; f < e.length; f++) {
                for (var [r, n, o] = e[f], l = !0, c = 0; c < r.length; c++)
                    (!1 & o || i >= o) && Object.keys(a.O).every((e => a.O[e](r[c]))) ? r.splice(c--, 1) : (l = !1, o < i && (i = o));
                if (l) {
                    e.splice(f--, 1);
                    var u = n();
                    void 0 !== u && (t = u)
                }
            }
            return t
        }
        o = o || 0;
        for (var f = e.length; f > 0 && e[f - 1][2] > o; f--)
            e[f] = e[f - 1];
        e[f] = [r, n, o]
    },
    a.n = e => {
        var t = e && e.__esModule ? () => e.default : () => e;
        return a.d(t, {
            a: t
        }),
        t
    },
    r = Object.getPrototypeOf ? e => Object.getPrototypeOf(e) : e => e.__proto__,
    a.t = function (e, n) {
        if (1 & n && (e = this(e)), 8 & n)
            return e;
        if ("object" == typeof e && e) {
            if (4 & n && e.__esModule)
                return e;
            if (16 & n && "function" == typeof e.then)
                return e
        }
        var o = Object.create(null);
        a.r(o);
        var i = {};
        t = t || [null, r({}), r([]), r(r)];
        for (var l = 2 & n && e; "object" == typeof l && !~t.indexOf(l); l = r(l))
            Object.getOwnPropertyNames(l).forEach((t => i[t] = () => e[t]));
        return i.default = () => e,
        a.d(o, i),
        o
    },
    a.d = (e, t) => {
        for (var r in t)
            a.o(t, r) && !a.o(e, r) && Object.defineProperty(e, r, {
                enumerable: !0,
                get: t[r]
            })
    },
    a.g = function () {
        if ("object" == typeof globalThis)
            return globalThis;
        try {
            return this || new Function("return this")()
        } catch (e) {
            if ("object" == typeof window)
                return window
        }
    }
    (),
    a.o = (e, t) => Object.prototype.hasOwnProperty.call(e, t),
    a.r = e => {
        "undefined" != typeof Symbol && Symbol.toStringTag && Object.defineProperty(e, Symbol.toStringTag, {
            value: "Module"
        }),
        Object.defineProperty(e, "__esModule", {
            value: !0
        })
    },
    a.nmd = e => (e.paths = [], e.children || (e.children = []), e),
    (() => {
        a.b = document.baseURI || self.location.href;
        var e = {
            826: 0
        };
        a.O.j = t => 0 === e[t];
        var t = (t, r) => {
            var n,
            o,
            [i, l, c] = r,
            u = 0;
            if (i.some((t => 0 !== e[t]))) {
                for (n in l)
                    a.o(l, n) && (a.m[n] = l[n]);
                if (c)
                    var f = c(a)
            }
            for (t && t(r); u < i.length; u++)
                o = i[u], a.o(e, o) && e[o] && e[o][0](), e[o] = 0;
            return a.O(f)
        },
        r = self.webpackChunkremote_component = self.webpackChunkremote_component || [];
        r.forEach(t.bind(null, 0)),
        r.push = t.bind(null, r.push.bind(r))
    })(),
    a.nc = void 0;
    var i = a.O(void 0, [216], (() => a(1333)));
    i = a.O(i)
})();
