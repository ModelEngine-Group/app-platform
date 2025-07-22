drop trigger if exists trigger_update_collection ON store_collection;
create trigger trigger_update_collection
    before update ON store_collection
    for each row
execute function update_when_update();
