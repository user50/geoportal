-- SELECT adsrc FROM pg_attrdef WHERE adrelid = (SELECT oid FROM pg_class WHERE relname = 'TABLE NAME')
ALTER SEQUENCE geo_user_id_seq RESTART WITH 100;
ALTER SEQUENCE geo_user_role_id_seq RESTART WITH 100;
ALTER SEQUENCE geo_layer_type_id_seq RESTART WITH 100;

/* GEO_USER */
INSERT INTO GEO_USER (id, ext_system, login, password, enabled, first_name, last_name, email, phone) 
    VALUES(0, 'local', 'Anonymous', 'NONE', false, 'Все', '', '', '')
GO

INSERT INTO GEO_USER (id, ext_system, login, password, enabled, first_name, last_name, email, phone) 
    VALUES(1, 'local', 'Admin', '$2a$10$n/g1pI59EAOh24sAZjbMB.dDLVTodF9iuUO1x098g4OugzRd0und6', true, 'Администратор', '', '', '')
GO


/* GEO_USER_ROLE */
INSERT INTO GEO_USER_ROLE (id, name) 
    VALUES(1, 'Администратор геопортала')
GO

INSERT INTO GEO_USER_ROLE (id, name) 
    VALUES(2, 'Редактор геопортала')
GO


/* GEO_USER_TO_ROLE */

INSERT INTO GEO_USER_TO_ROLE (user_id, role_id) 
    VALUES(1, 1)
GO


/* GEO_LAYER_TYPE */
INSERT INTO GEO_LAYER_TYPE (id, name) 
    VALUES(1, 'WFS')
GO

INSERT INTO GEO_LAYER_TYPE (id, name) 
    VALUES(2, 'WMS')
GO

INSERT INTO GEO_LAYER_TYPE (id, name) 
    VALUES(3, 'Tiles')
GO
