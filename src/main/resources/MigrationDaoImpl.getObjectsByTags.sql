select n.id,
       'n' as prefix,
       n.name,
       'POINT' as the_geom_type,
       t2.key,
       t2.value
  from map_nodes n 
 inner join map_node_tag t
    on t.owner_id = n.id
 inner join map_node_tag t2
    on t2.owner_id = n.id
 where n.isDeleted = 0
   and t.key = :key0      /* HOLE */
   and t.value = :value0  /* HOLE */
   and (select Coalesce(sum(count1), 0) = 1 /* HOLE */
          from (select distinct key, 1 as count1
                  from map_node_tag t3 
                 where n.id = t3.owner_id
                   and (
                         true /* HOLE */
                       )
               ) as c
       )
 union all
select w.id,
       'w' as prefix,
       w.name,
       case 
         when ST_IsClosed(geom) and GeometryType(geom) != 'POLYGON' then GeometryType(ST_MakePolygon(geom)) 
         else GeometryType(geom)
       end as the_geom_type,
       t2.key,
       t2.value
  from map_ways w
 inner join map_way_tag t
    on t.owner_id = w.id
 inner join map_way_tag t2
    on t2.owner_id = w.id
 where w.isDeleted = 0
   /*and w.geom is not null*/
   and t.key = :key0      /* HOLE */
   and t.value = :value0  /* HOLE */
   and (select Coalesce(sum(count1), 0) = 1 /* HOLE */ 
          from (select distinct key, 1 as count1
                  from map_way_tag t3
                 where w.id = t3.owner_id
                   and (
                         true /* HOLE */
                       )
               ) as c
       )
 order by id
