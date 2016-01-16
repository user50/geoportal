select 	o.id, 
		o.name, 
		o.created_by, 
		o.changed_by, 
		o.created, 
		o.changed, 
		o.fias_code, 
		o.the_geom, 
		o.version 
from geo_object o 
where o.id in (:ids)