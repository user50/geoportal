select distinct(key) from geo_object_tag 
where object_id in (
    select object_id from geo_layer_to_object where layer_id = :layerId
)
order by key