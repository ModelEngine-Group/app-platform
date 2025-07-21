drop trigger  if exists trigger_update_tool ON store_tool;
create  trigger trigger_update_tool
    before update ON store_tool
    for each row
execute function update_when_update();