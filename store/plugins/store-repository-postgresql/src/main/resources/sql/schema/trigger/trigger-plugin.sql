drop trigger if exists trigger_update_plugin ON store_plugin;
create trigger trigger_update_plugin
    before update ON store_plugin
    for each row
execute function update_when_update();
