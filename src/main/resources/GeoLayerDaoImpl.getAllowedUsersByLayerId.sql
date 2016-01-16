select u.* from geo_user u
inner join geo_layer_to_user lu on lu.user_id = u.id and lu.permissions = 1
where lu.layer_id = :layerId
union
select u.* from geo_user u
inner join geo_user_to_role ur on ur.user_id = u.id
inner join geo_layer_to_role lu on lu.role_id = ur.role_id and lu.permissions = 1
where lu.layer_id = :layerId