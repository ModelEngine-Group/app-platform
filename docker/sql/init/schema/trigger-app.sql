create or replace trigger trigger_update_app
    before update ON store_app
    for each row
execute function update_when_update();