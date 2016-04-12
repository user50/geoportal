select
	ol.layer_id,
	count(ol.object_id) as count_obj
from geo_layer_to_object ol 
inner join geo_object o on o.id = ol.object_id
group by ol.layer_id
