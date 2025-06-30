UPDATE
	t_app_engine_model
SET
	base_url = CONCAT(
		base_url,
		CASE WHEN RIGHT(base_url, 1) = '/' THEN 'v1' ELSE '/v1' END
	)
WHERE
	base_url NOT LIKE '%/v1%';