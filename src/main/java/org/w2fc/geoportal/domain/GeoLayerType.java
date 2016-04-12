package org.w2fc.geoportal.domain;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@org.hibernate.annotations.Entity(
        dynamicUpdate = true,     /* Optimize update sql */
        dynamicInsert = true        /* Optimize insert sql */
)

@Table (name = "GEO_LAYER_TYPE")
public class GeoLayerType extends AbstractDomain<GeoLayerType>{
    
    public static final Long TYPE_REGISTRY_SERVICE = 0L;

    public static final Long TYPE_WFS = 1L;

    public static final Long TYPE_WMS = 2L;

    public static final Long TYPE_TILES = 3L;
    
    public static final Long TYPE_REGISTRY_LAYER = 4L;
	public static final Long TYPE_WMSWFS = 6L;
    
    @Id
    @Column (name = "id", unique = true, nullable = false)
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    
    @Column (name = "name", nullable = false)
    private String name;
    
    @Version 
    @Column(name = "VERSION")
    private Integer version;

    
    /*  GeoLayer */
    @OneToMany(cascade=CascadeType.ALL, orphanRemoval=true)
    @JoinColumn(name = "type_id")
    @JsonIgnore
    private Set<GeoLayer> geoLayers;

    
    //=================================================

    public Long getId() {
        return id;
    }


    public void setId(Long id) {
        this.id = id;
    }


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public Integer getVersion() {
        return version;
    }


    public void setVersion(Integer version) {
        this.version = version;
    }


    public Set<GeoLayer> getGeoLayers() {
        return geoLayers;
    }


    public void setGeoLayers(Set<GeoLayer> geoLayers) {
        this.geoLayers = geoLayers;
    }
}
