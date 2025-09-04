create or replace function update_when_update()
returns trigger as $$
begin
	new.updated_time = current_timestamp;
return new;
end;
$$ language plpgsql;