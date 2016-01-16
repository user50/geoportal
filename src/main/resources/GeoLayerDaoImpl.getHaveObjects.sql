with obj as (
	select * from geo_layer_to_object ol 
	inner join geo_object o on o.id = ol.object_id
	where layer_id = :layerId limit 1
)
select count(*) from obj



