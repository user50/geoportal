CREATE SEQUENCE hibernate_sequence
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 2
  CACHE 1

go

ALTER TABLE hibernate_sequence
  OWNER TO postgres

go
/*
INSERT INTO geo_projections VALUES('RED','Красные Ткачи','PROJCS["Красные Ткачи",GEOGCS["GCS_Pulkovo_1942",DATUM["D_Pulkovo_1942",SPHEROID["Krasovsky_1940",6378245.0,298.3]],PRIMEM["Greenwich",0.0],UNIT["Degree",0.0174532925199433]],PROJECTION["Gauss_Kruger"],PARAMETER["False_Easting",102907.22],PARAMETER["False_Northing",-6372893.0],PARAMETER["Central_Meridian",41.42],PARAMETER["Scale_Factor",1.0],PARAMETER["Latitude_Of_Origin",0.0],UNIT["Meter",1.0]]')
go
INSERT INTO geo_projections VALUES('CK76_1','76_Ярославская_СК_1','PROJCS["76_Ярославская_СК_1",GEOGCS["GCS_Pulkovo_1942",DATUM["D_Pulkovo_1942",SPHEROID["Krasovsky_1940",6378245.0,298.3]],PRIMEM["Greenwich",0.0],UNIT["Degree",0.0174532925199433]],PROJECTION["Gauss_Kruger"],PARAMETER["False_Easting",1250000.0],PARAMETER["False_Northing",-6000000.0],PARAMETER["Central_Meridian",38.55],PARAMETER["Scale_Factor",1.0],PARAMETER["Latitude_Of_Origin",0.1333333333333333],UNIT["Meter",1.0]]')
go
INSERT INTO geo_projections VALUES('CK76_2','76_Ярославская_СК_2','PROJCS["76_Ярославская_СК_2",GEOGCS["GCS_Pulkovo_1942",DATUM["D_Pulkovo_1942",SPHEROID["Krasovsky_1940",6378245.0,298.3]],PRIMEM["Greenwich",0.0],UNIT["Degree",0.0174532925199433]],PROJECTION["Gauss_Kruger"],PARAMETER["False_Easting",2250000.0],PARAMETER["False_Northing",-6000000.0],PARAMETER["Central_Meridian",41.55],PARAMETER["Scale_Factor",1.0],PARAMETER["Latitude_Of_Origin",0.1333333333333333],UNIT["Meter",1.0]]')
go
INSERT INTO geo_projections VALUES('Rybinsk','Рыбинск','PROJCS["Rubinsk",GEOGCS["GCS_Pulkovo_1942",DATUM["D_Pulkovo_1942",SPHEROID["Krasovsky_1940",6378245.0,298.3]],PRIMEM["Greenwich",0.0],UNIT["Degree",0.0174532925199433]],PROJECTION["Gauss_Kruger"],PARAMETER["False_Easting",-3550.5],PARAMETER["False_Northing",-6436783.7],PARAMETER["Central_Meridian",38.8],PARAMETER["Scale_Factor",1.0],PARAMETER["Latitude_Of_Origin",0.0],UNIT["Meter",1.0]]')
go
INSERT INTO geo_projections VALUES('MSK_76_2','MSK_76_2','PROJCS["MSK_76_2",GEOGCS["GCS_Pulkovo_1942",DATUM["D_Pulkovo_1942",SPHEROID["Krasovsky_1940",6378245.0,298.3]],PRIMEM["Greenwich",0.0],UNIT["Degree",0.0174532925199433]],PROJECTION["Gauss_Kruger"],PARAMETER["False_Easting",2250000.0],PARAMETER["False_Northing",-6014743.504],PARAMETER["Central_Meridian",41.55],PARAMETER["Scale_Factor",1.0],PARAMETER["Latitude_Of_Origin",0.0],UNIT["Meter",1.0]]')
go
INSERT INTO geo_projections VALUES('MSK_76_1','MSK_76_1','PROJCS["MSK_76_1",GEOGCS["GCS_Pulkovo_1942",DATUM["D_Pulkovo_1942",SPHEROID["Krasovsky_1940",6378245.0,298.3]],PRIMEM["Greenwich",0.0],UNIT["Degree",0.0174532925199433]],PROJECTION["Gauss_Kruger"],PARAMETER["False_Easting",1250000.0],PARAMETER["False_Northing",-6014743.504],PARAMETER["Central_Meridian",38.55],PARAMETER["Scale_Factor",1.0],PARAMETER["Latitude_Of_Origin",0.0],UNIT["Meter",1.0]]')
go
INSERT INTO geo_projections VALUES('YAR','76_23_г_Ярославль','PROJCS["76_23_г_Ярославль",GEOGCS["GCS_Pulkovo_1942",DATUM["D_Pulkovo_1942",SPHEROID["Krasovsky_1940",6378245.0,298.3]],PRIMEM["Greenwich",0.0],UNIT["Degree",0.0174532925199433]],PROJECTION["Gauss_Kruger"],PARAMETER["False_Easting",-2872.659],PARAMETER["False_Northing",-6389457.715],PARAMETER["Central_Meridian",39.8225],PARAMETER["Scale_Factor",1.0],PARAMETER["Latitude_Of_Origin",0.0],UNIT["Meter",1.0]]')
go
INSERT INTO geo_projections VALUES('UGL','76_22_г_Углич_parcels','PROJCS["76_22_г_Углич_parcels",GEOGCS["GCS_Pulkovo_1942",DATUM["D_Pulkovo_1942",SPHEROID["Krasovsky_1940",6378245.0,298.3]],PRIMEM["Greenwich",0.0],UNIT["Degree",0.0174532925199433]],PROJECTION["Gauss_Kruger"],PARAMETER["False_Easting",70906.4284317265],PARAMETER["False_Northing",-6330567.21489765],PARAMETER["Central_Meridian",39.8497222222],PARAMETER["Scale_Factor",1.0],PARAMETER["Latitude_Of_Origin",0.1],UNIT["Meter",1.0]]')
go
INSERT INTO geo_projections VALUES('TUT','76_21_г_Тутаев_parcels','PROJCS["76_21_г_Тутаев_parcels",GEOGCS["GCS_Pulkovo_1942",DATUM["D_Pulkovo_1942",SPHEROID["Krasovsky_1940",6378245.0,298.3]],PRIMEM["Greenwich",0.0],UNIT["Degree",0.0174532925199433]],PROJECTION["Gauss_Kruger"],PARAMETER["False_Easting",-34766.8217038544],PARAMETER["False_Northing",-6402007.68637202],PARAMETER["Central_Meridian",38.9027777778],PARAMETER["Scale_Factor",1.0],PARAMETER["Latitude_Of_Origin",0.1],UNIT["Meter",1.0]]')
go
INSERT INTO geo_projections VALUES('RYB','76_20_г_Рыбинск_parcels','PROJCS["76_20_г_Рыбинск_parcels",GEOGCS["GCS_Pulkovo_1942",DATUM["D_Pulkovo_1942",SPHEROID["Krasovsky_1940",6378245.0,298.3]],PRIMEM["Greenwich",0.0],UNIT["Degree",0.0174532925199433]],PROJECTION["Gauss_Kruger"],PARAMETER["False_Easting",-1458.7119026228],PARAMETER["False_Northing",-6425726.21668972],PARAMETER["Central_Meridian",38.8352777778],PARAMETER["Scale_Factor",1.0],PARAMETER["Latitude_Of_Origin",0.1],UNIT["Meter",1.0]]')
go
INSERT INTO geo_projections VALUES('ROS','76_19_г_Ростов','PROJCS["76_19_г_Ростов",GEOGCS["GCS_Pulkovo_1942",DATUM["D_Pulkovo_1942",SPHEROID["Krasovsky_1940",6378245.0,298.3]],PRIMEM["Greenwich",0.0],UNIT["Degree",0.0174532925199433]],PROJECTION["Gauss_Kruger"],PARAMETER["False_Easting",-11798.6025533375],PARAMETER["False_Northing",-6328155.92291308],PARAMETER["Central_Meridian",39.1888888888],PARAMETER["Scale_Factor",1.0],PARAMETER["Latitude_Of_Origin",0.1166666666666667],UNIT["Meter",1.0]]')
go
INSERT INTO geo_projections VALUES('PER','76_18_г_Переславль','PROJCS["76_18_г_Переславль",GEOGCS["GCS_Pulkovo_1942",DATUM["D_Pulkovo_1942",SPHEROID["Krasovsky_1940",6378245.0,298.3]],PRIMEM["Greenwich",0.0],UNIT["Degree",0.0174532925199433]],PROJECTION["Gauss_Kruger"],PARAMETER["False_Easting",1500.0],PARAMETER["False_Northing",-6198500.0],PARAMETER["Central_Meridian",39.0],PARAMETER["Scale_Factor",1.0],PARAMETER["Latitude_Of_Origin",0.0],UNIT["Meter",1.0]]')
go
*/
INSERT INTO geo_projections VALUES('msk_qone1','МСК 76 Зона 1','PROJCS["76_Ярославская_СК_1",GEOGCS["GCS_Pulkovo_1942",DATUM["D_Pulkovo_1942",SPHEROID["Krasovsky_1940",6378245.0,298.3]],PRIMEM["Greenwich",0.0],UNIT["Degree",0.0174532925199433]],PROJECTION["Gauss_Kruger"],PARAMETER["False_Easting",1250117.0],PARAMETER["False_Northing",-6000009.0],PARAMETER["Central_Meridian",38.55],PARAMETER["Scale_Factor",1.0],PARAMETER["Latitude_Of_Origin",0.1333333333333333],UNIT["Meter",1.0]]')
go
INSERT INTO geo_projections VALUES('msk_zone2','МСК 76 Зона 2','PROJCS["76_Ярославская_СК_2",GEOGCS["GCS_Pulkovo_1942",DATUM["D_Pulkovo_1942",SPHEROID["Krasovsky_1940",6378245.0,298.3]],PRIMEM["Greenwich",0.0],UNIT["Degree",0.0174532925199433]],PROJECTION["Gauss_Kruger"],PARAMETER["False_Easting",2250115.0],PARAMETER["False_Northing",-6000015.0],PARAMETER["Central_Meridian",41.55],PARAMETER["Scale_Factor",1.0],PARAMETER["Latitude_Of_Origin",0.1333333333333333],UNIT["Meter",1.0]]')
go
INSERT INTO geo_projections VALUES('g_ribinsk','МСК 76 Рыбинск','PROJCS["Rubinsk",GEOGCS["GCS_Pulkovo_1942",DATUM["D_Pulkovo_1942",SPHEROID["Krasovsky_1940",6370245.0,298.3]],PRIMEM["Greenwich",0.0],UNIT["Degree",0.0174532925199433]],PROJECTION["Gauss_Kruger"],PARAMETER["False_Easting",-1354],PARAMETER["False_Northing",-6413967.7],PARAMETER["Central_Meridian",38.8352777778],PARAMETER["Scale_Factor",1.0],PARAMETER["Latitude_Of_Origin",0.1333333333333333],UNIT["Meter",1.0]]')
go
INSERT INTO geo_projections VALUES('g_Pereslavl','МСК 76 Переславль','PROJCS["76_18_г_Переславль",GEOGCS["GCS_Pulkovo_1942",DATUM["D_Pulkovo_1942",SPHEROID["Krasovsky_1940",6378245.0,298.3]],PRIMEM["Greenwich",0.0],UNIT["Degree",0.0174532925199433]],PROJECTION["Gauss_Kruger"],PARAMETER["False_Easting",1617.0],PARAMETER["False_Northing",-6198510.0],PARAMETER["Central_Meridian",39.0],PARAMETER["Scale_Factor",1.0],PARAMETER["Latitude_Of_Origin",0.0],UNIT["Meter",1.0]]')
go
INSERT INTO geo_projections VALUES('g_yar','МСК 76 Ярославль','PROJCS["76_23_г_Ярославль",GEOGCS["GCS_Pulkovo_1942",DATUM["D_Pulkovo_1942",SPHEROID["Krasovsky_1940",6378245.0,298.3]],PRIMEM["Greenwich",0.0],UNIT["Degree",0.0174532925199433]],PROJECTION["Gauss_Kruger"],PARAMETER["False_Easting",-2755.659],PARAMETER["False_Northing",-6374725.715],PARAMETER["Central_Meridian",39.8225],PARAMETER["Scale_Factor",1.0],PARAMETER["Latitude_Of_Origin",0.1333333333333333],UNIT["Meter",1.0]]')
go
INSERT INTO geo_projections VALUES('g_rostov','МСК 76 Ростов','PROJCS["76_19_г_Ростов",GEOGCS["GCS_Pulkovo_1942",DATUM["D_Pulkovo_1942",SPHEROID["Krasovsky_1940",6378245.0,298.3]],PRIMEM["Greenwich",0.0],UNIT["Degree",0.0174532925199433]],PROJECTION["Gauss_Kruger"],PARAMETER["False_Easting",-11669.6025533375],PARAMETER["False_Northing",-6328141.92291308],PARAMETER["Central_Meridian",39.1888888888],PARAMETER["Scale_Factor",1.0],PARAMETER["Latitude_Of_Origin",0.1166666666666667],UNIT["Meter",1.0]]')
go
INSERT INTO geo_projections VALUES('g_yglich','МСК 76 Углич','PROJCS["76_22_г_Углич_parcels",GEOGCS["GCS_Pulkovo_1942",DATUM["D_Pulkovo_1942",SPHEROID["Krasovsky_1940",6378245.0,298.3]],PRIMEM["Greenwich",0.0],UNIT["Degree",0.0174532925199433]],PROJECTION["Gauss_Kruger"],PARAMETER["False_Easting",71083.4284317265],PARAMETER["False_Northing",-6326902.21489765],PARAMETER["Central_Meridian",39.8497222222],PARAMETER["Scale_Factor",1.0],PARAMETER["Latitude_Of_Origin",0.1333333333333333],PARAMETER["Azimuth",30.28813975277778],UNIT["Meter",1.0]]')
go
INSERT INTO geo_projections VALUES('g_poshehon','МСК 76 Пошехон','PROJCS["76_22_г_Пошехон",GEOGCS["GCS_Pulkovo_1942",DATUM["D_Pulkovo_1942",SPHEROID["Krasovsky_1940",6379495.0,298.3]],PRIMEM["Greenwich",0.0],UNIT["Degree",0.0174532925199433]],PROJECTION["Gauss_Kruger"],PARAMETER["False_Easting",-26161.4284317265],PARAMETER["False_Northing",-6467540.21489725],PARAMETER["Central_Meridian",38.55],PARAMETER["Scale_Factor",1.0],PARAMETER["Latitude_Of_Origin",0.1333333333333333],UNIT["Meter",1.0]]')
go
INSERT INTO geo_projections VALUES('g_gav_yam','МСК 76 Гав-Ям','PROJCS["76_18_г_Гав_Ям",GEOGCS["GCS_Pulkovo_1942",DATUM["D_Pulkovo_1942",SPHEROID["Krasovsky_1940",6378245.0,298.3]],PRIMEM["Greenwich",0.0],UNIT["Degree",0.0174532925199433]],PROJECTION["Gauss_Kruger"],PARAMETER["False_Easting",-48229.0],PARAMETER["False_Northing",-6348618.0],PARAMETER["Central_Meridian",39.0],PARAMETER["Scale_Factor",1.0],PARAMETER["Latitude_Of_Origin",0.0],UNIT["Meter",1.0]]')
go
INSERT INTO geo_projections VALUES('g_lubim','МСК-76 Любим','PROJCS["76_г_Любим",GEOGCS["GCS_Pulkovo_1942",DATUM["D_Pulkovo_1942",SPHEROID["Krasovsky_1940",6378245.0,298.3]],PRIMEM["Greenwich",0.0],UNIT["Degree",0.0174532925199433]],PROJECTION["Gauss_Kruger"],PARAMETER["False_Easting",-95503.0],PARAMETER["False_Northing",-6470850.0],PARAMETER["Central_Meridian",39.0],PARAMETER["Scale_Factor",1.0],PARAMETER["Latitude_Of_Origin",0.0],UNIT["Meter",1.0]]')
go
INSERT INTO geo_projections VALUES('g_Prechst','МСК 76 Пречистое','PROJCS["76_18_г_Пречистое",GEOGCS["GCS_Pulkovo_1942",DATUM["D_Pulkovo_1942",SPHEROID["Krasovsky_1940",6378245.0,298.3]],PRIMEM["Greenwich",0.0],UNIT["Degree",0.0174532925199433]],PROJECTION["Gauss_Kruger"],PARAMETER["False_Easting",-75389.0],PARAMETER["False_Northing",-6477510.0],PARAMETER["Central_Meridian",39.0],PARAMETER["Scale_Factor",1.0],PARAMETER["Latitude_Of_Origin",0.0],UNIT["Meter",1.0]]')
go


insert into geo_settings (key, value) values ('CLUSTERING_AMOUNT', '100')
go
insert into geo_settings (key, value) values ('JPG_URL', 'http://localhost/')
go
insert into geo_settings (key, value) values ('JPG_FOLDER', 'c://tmp/')
go
insert into geo_settings (key, value) values ('PRINT_SERVER', 'http://geoportal.yarcloud.ru/geoserver')
go
insert into geo_settings (key, value) values ('PRINT_DPI', '150')
go
insert into geo_settings (key, value) values ('SHOW_LABEL', '0')
go
insert into geo_settings (key, value) values ('MASS_PERMISSION_CONTROL', '0')
go

alter table geo_projections add column  provided_sr varchar(25)
go
alter table geo_projections add column  dx double precision
go
alter table geo_projections add column  dy double precision
go
alter table geo_projections add column  q double precision
go

update geo_projections set provided_sr = 'msk_qone1', dx = 1268308.32, dy = 422080.63 where key = 'g_ribinsk'
go
