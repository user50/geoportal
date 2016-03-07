package org.w2fc.geoportal.domain;

import java.sql.Blob;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonIgnore;


@Entity
@org.hibernate.annotations.Entity(
        dynamicUpdate = true,     /* Optimize update sql */
        dynamicInsert = true        /* Optimize insert sql */
)

@Table (name = "GEO_LAYER")
public class GeoLayer extends AbstractDomain<GeoLayer>{

    @Id
    @Column (name = "id", unique = true, nullable = false)
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    
    @Column (name = "name", nullable = false)
    private String name;

    @Column (name = "parent_id", nullable = true)
    private Long parentId;
    
    @Column (name = "type_id", nullable = true)
    private Long typeId;
    
    
    @Column (name = "url", nullable = true)
    private String url;
    
    @Column (name = "icon", nullable = true)
    @Lob()
    @JsonIgnore
    private Blob icon;
    
    @Column (name = "treeicon", nullable = true)
    @Lob()
    @JsonIgnore
	private Blob treeIcon;
    
    @Column (name = "line_color", nullable = true)
    private String lineColor;
    
    @Column (name = "line_weight", nullable = true)
    private Integer lineWeight;

    @Column (name = "fill_color", nullable = true)
    private String fillColor;
    
    @Column (name = "fill_opacity", nullable = true)
    private Integer fillOpacity;
    
    @Version 
    @Column(name = "VERSION")
    private Integer version;
    
    
    /* GeoUser */
    @JsonIgnore
    @OneToMany(cascade=CascadeType.ALL, mappedBy="referenceId.geoLayer", orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<GeoLayerToUserReference> layerToUserReferences;
    
    /* GeoUserRole */
    @JsonIgnore
    @OneToMany(cascade=CascadeType.ALL, mappedBy="referenceId.geoLayer", orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<GeoLayerToRoleReference> layerToRoleReferences;
        
    /* GeoObject */
    @JsonIgnore
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinTable(
            name = "GEO_LAYER_TO_OBJECT", 
            joinColumns = {
                    @JoinColumn(name = "layer_id", nullable = false, updatable = false)}, 
            inverseJoinColumns = {
                    @JoinColumn(name = "object_id", nullable = false, updatable = false)}
    )  
    private Set<GeoObject> geoObjects;

    /* GeoLayerMetadata */
    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @PrimaryKeyJoinColumn
    private GeoLayerMetadata metadata;
    
    /*Template*/
    @JsonIgnore
    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = false)
    @JoinColumn(name="tmpl_id")
    private AddnsPopupTemplate popupTemplate;

    @Transient
    private Integer permissions;


    
    
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


    public Long getParentId() {
        return parentId;
    }


    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }


    public Long getTypeId() {
        return typeId;
    }


    public void setTypeId(Long typeId) {
        this.typeId = typeId;
    }


    public String getUrl() {
        return url;
    }


    public void setUrl(String url) {
        this.url = url;
    }


    public Blob getIcon() {
        return icon;
    }

    public Blob getTreeIcon() {
		return treeIcon;
	}

    public void setTreeIcon(Blob treeIcon) {
		this.treeIcon = treeIcon;
	}


	public void setIcon(Blob icon) {
        this.icon = icon;
    }


    public String getLineColor() {
        return lineColor;
    }


    public void setLineColor(String lineColor) {
        this.lineColor = lineColor;
    }


    public Integer getLineWeight() {
        return lineWeight;
    }


    public void setLineWeight(Integer lineWeight) {
        this.lineWeight = lineWeight;
    }


    public String getFillColor() {
        return fillColor;
    }


    public void setFillColor(String fillColor) {
        this.fillColor = fillColor;
    }


    public Integer getFillOpacity() {
        return fillOpacity;
    }


    public void setFillOpacity(Integer fillOpacity) {
        this.fillOpacity = fillOpacity;
    }


    public Integer getVersion() {
        return version;
    }


    public void setVersion(Integer version) {
        this.version = version;
    }


    public Set<GeoObject> getGeoObjects() {
        return geoObjects;
    }


    public void setGeoObjects(Set<GeoObject> geoObjects) {
        this.geoObjects = geoObjects;
    }


    public GeoLayerMetadata getMetadata() {
        return metadata;
    }


    public void setMetadata(GeoLayerMetadata metadata) {
        this.metadata = metadata;
    }


    public Set<GeoLayerToUserReference> getLayerToUserReferences() {
        return layerToUserReferences;
    }


    public void setLayerToUserReferences(Set<GeoLayerToUserReference> layerToUserReferences) {
        this.layerToUserReferences = layerToUserReferences;
    }


    public Set<GeoLayerToRoleReference> getLayerToRoleReferences() {
        return layerToRoleReferences;
    }


    public void setLayerToRoleReferences(Set<GeoLayerToRoleReference> layerToRoleReferences) {
        this.layerToRoleReferences = layerToRoleReferences;
    }


	public AddnsPopupTemplate getPopupTemplate() {
		return popupTemplate;
	}


	public void setPopupTemplate(AddnsPopupTemplate popupTemplate) {
		this.popupTemplate = popupTemplate;
	}


    public Integer getPermissions() {
        return permissions;
    }


    public void setPermissions(Integer permissions) {
        this.permissions = permissions;
    }
    
    @Override
    public boolean equals(Object obj) {
    	if(obj instanceof GeoLayer){
    		return getId().equals(((GeoLayer)obj).getId());
    	}
    	return false;
    }
}
