create or replace trigger trigger_update_tool
    before update ON store_tool
    for each row
execute function update_when_update();