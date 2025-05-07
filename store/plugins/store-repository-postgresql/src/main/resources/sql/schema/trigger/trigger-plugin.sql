create or replace trigger trigger_update_plugin
    before update ON store_plugin
    for each row
execute function update_when_update();