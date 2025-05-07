create or replace trigger trigger_update_task
    before update ON store_task
    for each row
execute function update_when_update();