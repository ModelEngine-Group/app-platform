drop trigger if exists trigger_update_app ON store_app;
create  trigger trigger_update_app
    before update ON store_app
    for each row
execute function update_when_update();