with  object_ids as (
select a.object_id 
			from  geo_user_to_role rr 
				inner join geo_user_role r on rr.role_id = r.id 
				inner join geo_role_to_acl ar on r.id = ar.role_id 
				inner join geo_acl a on a.id = ar.acl_id where rr.user_id = :id 
			union 
			select a.object_id 
			from geo_user_to_acl ar 
				inner join geo_acl a on a.id = ar.acl_id 
			where ar.user_id = :id)
			
select  path[1], geom from (select (st_dumprings((ST_Dump(o.the_geom)).geom)).*
                                from object_ids ids
                                    left join geo_object o on o.id = ids.object_id
                            ) as objs
union 

select -1 as path, ST_Union(o.the_geom) as geom
	from object_ids ids
	left join geo_object o on o.id = ids.object_id
