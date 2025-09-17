create or replace trigger trigger_update_tag
    before update ON store_tag
    for each row
execute function update_when_update();