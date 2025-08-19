delete
from store_plugin;

insert into store_plugin("plugin_id", "plugin_name", "extension", "deploy_status", "is_builtin", "source", "icon", "user_group_id")
values ('pid1', 'pname1', '{}', 'UNDEPLOYED', 'false', 'source', 'icon', 'g1'),
       ('pid2', 'pname2', '{}', 'UNDEPLOYED', 'false', 'source', 'icon', 'g1'),
       ('pid3', 'pname3', '{}', 'DEPLOYED', 'false', 'source', 'icon', 'g1');