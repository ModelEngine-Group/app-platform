-- 1. 导出 store_plugin 表数据
INSERT INTO store_plugin (plugin_id, plugin_name, extension, deploy_status, is_builtin, source, icon, user_group_id) VALUES ('54e9a5117a5e3806c32fcefbec3368e90fdb57fe592415d47c553f62fc14f6c4', '联网搜索插件', '{"pluginFullName":"demo-1759045119331.zip","checksum":"d2c24dcd8436a0d7c7f7beb3717c0da1f3b0593b7cab2b2666d2542dd64a1db9","name":"联网搜索插件","description":"联网搜索插件","type":"python","uniqueness.name":"abbb-20250926103953-ea11a707"}', 'DEPLOYED', true, '', NULL, '*') ON CONFLICT (plugin_id) DO NOTHING;

-- 2. 导出 store_plugin_tool 表数据
INSERT INTO store_plugin_tool (tool_name, plugin_id, tool_unique_name, source, icon, user_group_id) VALUES ('联网搜索工具', '54e9a5117a5e3806c32fcefbec3368e90fdb57fe592415d47c553f62fc14f6c4', '4b2ad1e7-5019-40c3-9de9-cbbb7cb7dea6', '', NULL, '*') ON CONFLICT(plugin_id, tool_unique_name) DO NOTHING;

-- 3. 导出 store_tool 表数据
INSERT INTO store_tool (name, schema, runnables, extensions, unique_name, version, is_latest, group_name, definition_name, definition_group_name) VALUES ('联网搜索工具', '{"name":"联网搜索工具","description":"联网搜索工具","parameters":{"type":"object","properties":{"query":{"examples":"","defaultValue":"","name":"query","description":"问题","type":"string","required":false}},"required":["query"]},"return":{"convertor":"","examples":"","name":"","description":"联网搜索的结果","type":"array","items":{"type":"object","properties":{"score":{"type":"number"},"metadata":{"type":"object"},"id":{"type":"string"},"text":{"type":"string"}}}},"order":["query"]}', '{"FIT":{"genericableId":"Search.Online.tool","fitableId":"Python_REPL"}}', '{"tags":["FIT"]}', '4b2ad1e7-5019-40c3-9de9-cbbb7cb7dea6', '1.0.0', true, 'Search-Online-tool-Impl', '联网搜索工具', 'Search-Online-tool') ON CONFLICT(unique_name, version) DO NOTHING;

-- 4. 导出 store_definition 表数据
INSERT INTO store_definition (name, schema, definition_group_name) VALUES ('联网搜索工具', '{"name":"联网搜索工具","description":"联网搜索工具","parameters":{"type":"object","properties":{"query":{"defaultValue":"","description":"问题","name":"query","type":"string","examples":"","required":true}},"required":["query"]},"order":["query"],"return":{"type":"array","items":{"type":"object","properties":{"id":{"type":"string"},"text":{"type":"string"},"score":{"type":"number"},"metadata":{"type":"object"}}},"convertor":""}}', 'Search-Online-tool') ON CONFLICT (definition_group_name, name) DO NOTHING;

-- 5. 导出 store_definition_group 表数据（去重 name）
INSERT INTO store_definition_group (name, summary, description, extensions) VALUES ('Search-Online-tool', '', '', '{}') ON CONFLICT(name) DO NOTHING;

-- 6. 导出 store_tag 表数据
INSERT INTO store_tag (tool_unique_name, name) VALUES ('4b2ad1e7-5019-40c3-9de9-cbbb7cb7dea6', 'FIT') ON CONFLICT(tool_unique_name, name) DO NOTHING;
INSERT INTO store_tag (tool_unique_name, name) VALUES ('4b2ad1e7-5019-40c3-9de9-cbbb7cb7dea6', 'SEARCHONLINENODESTATE') ON CONFLICT(tool_unique_name, name) DO NOTHING;

-- 7. 导出 store_tool_group 表数据（去重 name）
INSERT INTO store_tool_group (name, definition_group_name, summary, description, extensions) VALUES ('Search-Online-tool-Impl', 'Search-Online-tool', '', '', '{}') ON CONFLICT(name) DO NOTHING;

