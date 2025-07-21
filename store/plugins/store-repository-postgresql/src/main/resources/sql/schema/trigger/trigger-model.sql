drop trigger if exists trigger_update_model ON store_model;
create  trigger trigger_update_model
    before update ON store_model
    for each row
execute function update_when_update();