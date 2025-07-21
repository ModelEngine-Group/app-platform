drop trigger  if exists trigger_update_plugin_tool ON store_plugin_tool;
create  trigger trigger_update_plugin_tool
    before update ON store_plugin_tool
    for each row
execute function update_when_update();