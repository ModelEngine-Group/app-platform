drop trigger if exists trigger_update_tag ON store_tag;
create trigger trigger_update_tag
    before update ON store_tag
    for each row
execute function update_when_update();
