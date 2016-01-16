select cast(count(*) as int) from geo_layer_to_object
where layer_id = :layerId and object_id in (:ids)