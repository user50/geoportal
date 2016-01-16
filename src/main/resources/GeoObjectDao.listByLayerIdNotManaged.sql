select 	o.id, 
		o.name, 
		o.created_by, 
		o.changed_by, 
		o.created, 
		o.changed, 
		o.fias_code, 
		o.the_geom, 
		o.version 
from geo_layer_to_object lo 
	inner join geo_object o 
			on o.id = lo.object_id 
where lo.layer_id = :id