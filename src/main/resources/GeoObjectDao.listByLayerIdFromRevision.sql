select
    distinct o.id,
    o.name,
    o.created_by,
    o.changed_by,
    o.created,
    o.changed,
    o.fias_code,
    st_setsrid(o.the_geom, 4326) as the_geom,
    o.version
from
    geo_object o
inner join
    geo_layer_to_object lo
        on lo.object_id = o.id
left join
    geo_object_aud a
        on a.id = o.id
where
    lo.layer_id = :layerId 
    and a.rev > :revId
union
select
    distinct  o.id,
    'DELETED' as name,
    o.created_by,
    o.changed_by,
    o.created,
    o.changed,
    o.fias_code,
    st_setsrid(o.the_geom, 4326) as the_geom,
    o.version
from
    geo_object_aud o
inner join
    geo_layer_to_object_aud lo
        on lo.object_id = o.id and lo.revtype = 2
where
    o.rev > :revId and lo.layer_id = :layerId