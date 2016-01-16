CREATE TABLE AD_POPUP_TMPL ( 
    id          bigserial NOT NULL,
    name        varchar(255) NOT NULL,
    template    text NOT NULL,
    VERSION     int NOT NULL DEFAULT 0,
    PRIMARY KEY(id)
)
GO

CREATE TABLE AD_RATING ( 
    id      bigserial NOT NULL,
    clicks  int8 NOT NULL DEFAULT 1,
    likes   int8 NOT NULL DEFAULT 1,
    VERSION int NOT NULL DEFAULT 0,
    PRIMARY KEY(id)
)
GO

CREATE TABLE AD_TAGS_DICT ( 
    id      bigserial NOT NULL,
    key     varchar(255) NULL,
    alias   varchar(255) NULL,
    type    varchar(255) NULL,
    VERSION int NOT NULL DEFAULT 0,
    PRIMARY KEY(id)
)
GO

CREATE TABLE AD_TAGS_DICT_VALUES ( 
    id              bigserial NOT NULL,
    tags_dict_id    int8 NOT NULL,
    value           varchar(255) NULL,
    VERSION         int NOT NULL DEFAULT 0,
    PRIMARY KEY(id)
)
GO

CREATE TABLE GEO_ACL ( 
    id          bigserial NOT NULL,
    object_id   int8 NULL,
    name        varchar(255) NULL,
    permissions int NOT NULL DEFAULT 0,
    VERSION     int NOT NULL DEFAULT 0,
    PRIMARY KEY(id)
)
GO

CREATE TABLE GEO_LAYER ( 
    id              bigserial NOT NULL,
    name            varchar(255) NOT NULL,
    parent_id       int8 NULL,
    type_id         int8 NOT NULL,
    url             varchar(255) NULL,
    icon            oid NULL,
    treeicon        oid NULL,
    line_color      varchar(20) NULL,
    line_weight     int NULL,
    fill_color      varchar(20) NULL,
    fill_opacity    float NULL,
    tmpl_id         int8 NULL,
    VERSION         int NOT NULL DEFAULT 0,
    PRIMARY KEY(id)
)
GO

CREATE TABLE GEO_LAYER_METADATA ( 
    id                          bigserial NOT NULL,
    created_by                  int8 NULL,
    created                     timestamp NULL,
    changed_by                  int8 NULL,
    changed                     timestamp NULL,
    view_by_object              boolean NULL,
    owner_company               varchar(255) NULL,
    owner_name                  varchar(255) NULL,
    owner_email                 varchar(255) NULL,
    owner_phone                 varchar(255) NULL,
    desc_spatial_data           text NULL,
    source_spatial_data         varchar(255) NULL,
    doc_regulation              varchar(255) NULL,
    access_level                varchar(255) NULL,
    access_conditions           text NULL,
    map_accuracy                varchar(255) NULL,
    last_update_metadata        timestamp NULL,
    last_update_spatial_data    varchar(255) NULL,
    update_frequency            varchar(255) NULL,
    coordinate_system           varchar(255) NULL,
    coverage_area               varchar(255) NULL,
    data_amount                 varchar(255) NULL,
    export_format               varchar(255) NULL,
    VERSION                     int NOT NULL DEFAULT 0,
    PRIMARY KEY(id)
)
GO

CREATE TABLE GEO_LAYER_METADATA_AUD ( 
    id                          bigserial NOT NULL,
    created_by                  int8 NULL,
    created                     timestamp NULL,
    changed_by                  int8 NULL,
    changed                     timestamp NULL,
    view_by_object              boolean NULL,
    owner_company               varchar(255) NULL,
    owner_name                  varchar(255) NULL,
    owner_email                 varchar(255) NULL,
    owner_phone                 varchar(255) NULL,
    desc_spatial_data           text NULL,
    source_spatial_data         varchar(255) NULL,
    doc_regulation              varchar(255) NULL,
    access_level                varchar(255) NULL,
    access_conditions           text NULL,
    map_accuracy                varchar(255) NULL,
    last_update_metadata        timestamp NULL,
    last_update_spatial_data    varchar(255) NULL,
    update_frequency            varchar(255) NULL,
    coordinate_system           varchar(255) NULL,
    coverage_area               varchar(255) NULL,
    data_amount                 varchar(255) NULL,
    export_format               varchar(255) NULL,
    VERSION                     int NOT NULL DEFAULT 0,
    REV                         integer NOT NULL,
    REVTYPE                     int NULL,
    PRIMARY KEY(id,REV)
)
GO

CREATE TABLE GEO_LAYER_TO_OBJECT ( 
    layer_id    int8 NOT NULL,
    object_id   int8 NOT NULL,
    PRIMARY KEY(layer_id,object_id)
)
GO

CREATE TABLE GEO_LAYER_TO_ROLE ( 
    layer_id    int8 NOT NULL,
    role_id     int8 NOT NULL,
    permissions int NOT NULL DEFAULT 0,
    PRIMARY KEY(layer_id,role_id)
)
GO

CREATE TABLE GEO_LAYER_TO_USER ( 
    layer_id    int8 NOT NULL,
    user_id     int8 NOT NULL,
    permissions int NOT NULL DEFAULT 0,
    PRIMARY KEY(layer_id,user_id)
)
GO

CREATE TABLE GEO_LAYER_TYPE ( 
    id      bigserial NOT NULL,
    name    varchar(255) NOT NULL,
    VERSION int NOT NULL DEFAULT 0,
    PRIMARY KEY(id)
)
GO

CREATE TABLE GEO_OBJECT ( 
    id          bigserial NOT NULL,
    name        varchar(255) NULL,
    created_by  int8 NULL,
    changed_by  int8 NULL,
    created     timestamp NULL,
    changed     timestamp NULL,
    fias_code   char(50) NULL,
    the_geom    geometry NOT NULL,
    VERSION     int NOT NULL DEFAULT 0,
    PRIMARY KEY(id)
)
GO

CREATE TABLE GEO_OBJECT_AUD ( 
    id          bigserial NOT NULL,
    name        varchar(255) NULL,
    created_by  int8 NULL,
    changed_by  int8 NULL,
    created     timestamp NULL,
    changed     timestamp NULL,
    fias_code   char(50) NULL,
    the_geom    geometry NULL,
    VERSION     int NOT NULL DEFAULT 0,
    REV         integer NOT NULL,
    REVTYPE     int NULL,
    PRIMARY KEY(id,REV)
)
GO

CREATE TABLE GEO_OBJECT_PROPERTIES ( 
    id              bigserial NOT NULL,
    icon            oid NULL,
    line_color      varchar(20) NULL,
    line_weight     int NULL,
    fill_color      varchar(20) NULL,
    fill_opacity    float NULL,
    VERSION         int NOT NULL DEFAULT 0,
    PRIMARY KEY(id)
)
GO

CREATE TABLE GEO_OBJECT_TAG ( 
    id          bigserial NOT NULL,
    object_id   int8 NULL,
    key         varchar(255) NULL,
    value       varchar(1024) NULL,
    VERSION     int NOT NULL DEFAULT 0,
    PRIMARY KEY(id)
)
GO

CREATE TABLE GEO_OBJECT_TAG_AUD ( 
    id          bigserial NOT NULL,
    object_id   int8 NULL,
    key         varchar(255) NULL,
    value       varchar(1024) NULL,
    VERSION     int NOT NULL DEFAULT 0,
    REV         integer NOT NULL,
    REVTYPE     int NULL,
    PRIMARY KEY(id,REV)
)
GO

CREATE TABLE GEO_ROLE_TO_ACL ( 
    role_id int8 NOT NULL,
    acl_id  int8 NOT NULL,
    PRIMARY KEY(role_id,acl_id)
)
GO

CREATE TABLE GEO_USER ( 
    id          bigserial NOT NULL,
    ext_system  varchar(255) NULL,
    login       varchar(255) NULL,
    password    varchar(255) NULL,
    enabled     boolean NULL,
    first_name  varchar(255) NULL,
    last_name   varchar(255) NULL,
    email       varchar(255) NULL,
    phone       varchar(25) NULL,
    VERSION     int NOT NULL DEFAULT 0,
    PRIMARY KEY(id)
)
GO

CREATE TABLE GEO_USER_ROLE ( 
    id      bigserial NOT NULL,
    name    varchar(255) NOT NULL,
    VERSION int NOT NULL DEFAULT 0,
    PRIMARY KEY(id)
)
GO

CREATE TABLE GEO_USER_TO_ACL ( 
    user_id int8 NOT NULL,
    acl_id  int8 NOT NULL,
    PRIMARY KEY(user_id,acl_id)
)
GO

CREATE TABLE GEO_USER_TO_ROLE ( 
    user_id int8 NOT NULL,
    role_id int8 NOT NULL,
    PRIMARY KEY(user_id,role_id)
)
GO

CREATE TABLE REVINFO ( 
    REV         bigserial NOT NULL,
    REVTSTMP    bigint NULL,
    PRIMARY KEY(REV)
)
GO

CREATE TABLE geo_projections (
		key VARCHAR(25) NOT NULL,
		description VARCHAR(255),
		wkt TEXT NOT NULL
)
GO

CREATE TABLE geo_settings (
		id bigserial NOT NULL,
		key VARCHAR(255) NOT NULL,
		value VARCHAR(1024)
)
GO

CREATE UNIQUE INDEX indx_unique_geoacl_objectid
    ON GEO_ACL(object_id)
GO

CREATE UNIQUE INDEX indx_unique_user_externalId
    ON GEO_USER(ext_system, login)
GO

