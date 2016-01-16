with recursive child_to_parents as (
  ( select l.*, /* All assigned from roles  */
           rl.permissions, 
           mt.view_by_object
      from geo_layer l
     left join geo_layer_metadata mt on mt.id = l.id
     inner join geo_layer_to_role rl on l.id = rl.layer_id
     inner join geo_user_to_role ru on ru.role_id = rl.role_id
     where ru.user_id = :id /* HOLE */
     union all /*<<<<< UNION >>>>>*/
    select l.*, /* All assigned from user  */
           lu.permissions, 
           mt.view_by_object
      from geo_layer l 
     left join geo_layer_metadata mt on mt.id = l.id
     inner join geo_layer_to_user lu on l.id = lu.layer_id
     where lu.user_id = :id /* HOLE */
  )
   union all /*<<<<< UNION >>>>>*/
  select b.*, /* add recursively parents of tree as read only  */ 
         0 as permissions,
         mt.view_by_object
    from geo_layer b, 
         child_to_parents c, 
        geo_layer_metadata mt
   where b.id = c.parent_id and mt.id = b.id
) select id, 
         name, 
         parent_id, 
         type_id,
/*         created_by,
         changed_by,
         created,
         changed,*/
         url, 
/*         icon,*/
         line_color,
         line_weight,
         fill_color,
         fill_opacity,
         tmpl_id, 
/*         version,*/
         max(permissions) as permissions,
         view_by_object
    from child_to_parents
   group by id,
         name, 
         parent_id, 
         type_id,
/*         created_by,
         changed_by,
         created,
         changed,*/
         url, 
/*         icon,*/
         line_color,
         line_weight,
         fill_color,
         fill_opacity,
         tmpl_id/*, 
         version*/,
         view_by_object
