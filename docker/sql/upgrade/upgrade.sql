-- 在store_tool与store_definition中添加order字段
UPDATE "public"."store_tool"
SET "schema" = jsonb_set(
    "schema"::jsonb,
    '{order}',
    ("schema"::jsonb->'parameters'->'required')::jsonb
)
WHERE "unique_name" IN (
    '25887d76-e358-4121-800c-31eb3390fdbd',
    '3bca6a3f-9623-4228-b120-1a5e0d41dc14',
    'e1d24716-9ffa-46e5-bd36-d3a8d8142559',
    'eeedf03c-10aa-41f1-b5e3-6623914242c8',
    'e2bd847f-2cfa-4cdc-b326-4d15a15c6a0d'
)
AND ("schema"->>'order') IS NULL;

UPDATE "public"."store_definition"
SET "schema" = jsonb_set(
    "schema"::jsonb,
    '{order}',
    ("schema"::jsonb->'parameters'->'required')::jsonb
)
WHERE "name" IN (
     'knowledge-retrieval-def-name',
     'application-text-extraction-def-name',
     'document-extractor-def-name',
     'image-generation-def-name',
     'document-extraction-def-name'
)
AND ("schema"->>'order') IS NULL;

-- 更新 store_tool 表
UPDATE "public"."store_tool"
SET "schema" = (
    WITH transformed AS (
        SELECT
            jsonb_set(
                "schema"::jsonb,
                '{parameters,properties}',
                (
                    SELECT jsonb_object_agg(
                        key,
                        CASE
                            -- 保留名为 examples 的参数
                            WHEN key = 'examples' THEN value
                            -- 其他参数移除 examples 和布尔类型的 required
                            ELSE
                                value::jsonb
                                - 'examples'
                                - CASE WHEN jsonb_typeof(value->'required') = 'boolean'
                                      THEN 'required'
                                      ELSE ''
                                 END
                        END
                    )
                    FROM jsonb_each(("schema"::jsonb)->'parameters'->'properties')
                )
            ) AS new_schema
    )
    SELECT new_schema FROM transformed
)
WHERE ("schema"::jsonb)->'parameters'->'properties' IS NOT NULL;

-- 同样的更新应用于 store_definition 表
UPDATE "public"."store_definition"
SET "schema" = (
    WITH transformed AS (
        SELECT
            jsonb_set(
                "schema"::jsonb,
                '{parameters,properties}',
                (
                    SELECT jsonb_object_agg(
                        key,
                        CASE
                            WHEN key = 'examples' THEN value
                            ELSE
                                value::jsonb
                                - 'examples'
                                - CASE WHEN jsonb_typeof(value->'required') = 'boolean'
                                      THEN 'required'
                                      ELSE ''
                                 END
                        END
                    )
                    FROM jsonb_each(("schema"::jsonb)->'parameters'->'properties')
                )
            ) AS new_schema
    )
    SELECT new_schema FROM transformed
)
WHERE ("schema"::jsonb)->'parameters'->'properties' IS NOT NULL;