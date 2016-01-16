select n.id,
       n.name,
       ST_MakePoint(n.lon, n.lat) as the_geom,
       t2.id as tag_id,
       t2.key,
       t2.value
  from  map_nodes n 
 inner join map_node_tag t2
    on t2.owner_id = n.id
 where n.isDeleted = 0
   and n.id in (:ids) /*(14905651, 141505070)*/
 order by 
       n.id, 
       t2.id
