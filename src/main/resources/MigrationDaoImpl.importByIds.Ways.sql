select id, 
       name, 
       case /* try to get Polygon and return just one piece of geometry */
         when not first_row then null
         when ST_IsClosed(the_geom) and GeometryType(the_geom) != 'POLYGON' then ST_MakePolygon(the_geom)
         else the_geom
       end as the_geom,
       tag_id, /* need for 'order by' */
       key,
       value
  from (select res1.id, /* make a geometry from lan+lat */
               res1.name,
               t2.id as tag_id,
               t2.key, 
               t2.value, 
               ST_MakeLine(ST_MakePoint(lon, lat)) as the_geom,
               (select t2.id = min(t.id) from map_way_tag t where t.owner_id = res1.id ) as first_row
          from (select w.id, /* get and order nodes */
                       w.name,
                       n.lat,
                       n.lon
                  from map_ways w
                 inner join map_way_nodes wn
                    on w.id = wn.wayid
                 inner join map_nodes n
                    on n.id = wn.node_id
                 where w.isDeleted = 0
                   and w.geom is null /* where there is NOT real geometry */
                   and w.id in (:ids) /* HOLE */
              order by w.id, 
                       wn.orderindex
               ) as res1
         inner join map_way_tag t2
            on t2.owner_id = res1.id
      group by res1.id,
               res1.name,
               tag_id, /* need for 'order by' */
               t2.key,
               t2.value
     union all  /*<<<<< UNION >>>>>*/
        select w.id,
               w.name,
               t2.id as tag_id,
               t2.key,
               t2.value,
               geom as the_geom,
               (select t2.id = min(t.id) from map_way_tag t where t.owner_id = w.id ) as first_row
          from  map_ways w
         inner join map_way_tag t2
            on t2.owner_id = w.id
         where w.isDeleted = 0
           and w.geom is not null /* where there is real geometry */
           /*and w.id in (141853735, 141615417, 141631853)*/
           and w.id in (:ids) /* HOLE */
    ) res
order by id, tag_id
