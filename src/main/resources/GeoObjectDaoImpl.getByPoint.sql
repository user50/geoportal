with index_query as (
  SELECT obj.id, obj.name, obj.created_by, obj.changed_by, obj.created, obj.changed, obj.fias_code, st_setsrid(obj.the_geom, 4326) as the_geom, obj.version    
	FROM geo_object obj
    left join geo_layer_to_object gl on gl.object_id = obj.id
    where gl.layer_id in (:layersIds)
	ORDER BY obj.the_geom <-> st_setsrid(st_makepoint(:lng, :lat),4326)
	LIMIT 100
)
select DISTINCT * from index_query
	where ST_Intersects(the_geom, ST_Buffer(CAST(ST_MakePoint(:lng, :lat) AS geography ), 100))
union 
select  obj.id, obj.name, obj.created_by, obj.changed_by, obj.created, obj.changed, obj.fias_code, st_setsrid(obj.the_geom, 4326) as the_geom, obj.version  
	from geo_object obj 
    left join geo_layer_to_object gl on gl.object_id = obj.id
where gl.layer_id in (:layersIds) and obj.fias_code like :fias	
