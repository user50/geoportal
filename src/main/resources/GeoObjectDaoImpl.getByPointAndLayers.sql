with index_query as (
  SELECT obj.id, obj.name, obj.created_by, obj.changed_by, obj.created, obj.changed, obj.fias_code, st_setsrid(obj.the_geom, 4326) as the_geom, obj.version    
	FROM geo_object obj
    left join geo_layer_to_object gl on gl.object_id = obj.id
    where gl.layer_id in (:layers)
)
select DISTINCT * from index_query
	where ST_Contains(the_geom, st_setsrid(ST_MakePoint(:lng, :lat),4326))