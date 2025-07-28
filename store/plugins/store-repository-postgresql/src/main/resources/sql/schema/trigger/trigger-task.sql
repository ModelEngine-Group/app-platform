drop trigger if exists trigger_update_task ON store_task;
create trigger trigger_update_task
    before update ON store_task
    for each row
execute function update_when_update();
