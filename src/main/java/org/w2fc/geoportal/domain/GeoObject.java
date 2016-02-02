package org.w2fc.geoportal.domain;

import java.util.Date;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import org.hibernate.annotations.Type;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.hibernate.envers.RelationTargetAuditMode;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.IndexedEmbedded;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vividsolutions.jts.geom.Geometry;


@Entity
@org.hibernate.annotations.Entity(
        dynamicUpdate = true,     /* Optimize update sql */
        dynamicInsert = true        /* Optimize insert sql */
)
@Indexed
@Audited
@Table (name = "GEO_OBJECT")
public class GeoObject extends AbstractDomain<GeoObject>{
    
    @Id
    @Column (name = "id", unique = true, nullable = false)
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    
    @Field
    @Column (name = "name", nullable = false)
    private String name;

    @Field
    @Column (name = "guid", nullable = true)
    private String guid;

    /*  GeoUser */
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED) 
    @OneToOne
    @JoinColumn(name = "created_by", nullable=true)
    private GeoUser createdBy;

    /*  GeoUser */
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED) 
    @OneToOne
    @JoinColumn(name = "changed_by", nullable=true)
    private GeoUser changedBy;
    
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column (name = "created", nullable = true)
    private Date created;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column (name = "changed", nullable = true)
    private Date changed;
    
    @Field
    @Column (name = "fias_code", nullable = true)
    private String fiasCode;
    
    //@FieldBridge(impl=GeometryFieldBridge.class) //unusable, because of JPA 
    @Type(type = "org.hibernatespatial.GeometryUserType")
    @Column(name = "the_geom")
    @JsonIgnore
    private Geometry theGeom;
    
    @Version 
    @Column(name = "VERSION")
    private Integer version;
    
    
    /* GeoLayer */
    @NotAudited
    @JsonIgnore
    @ManyToMany(mappedBy = "geoObjects", fetch = FetchType.EAGER, cascade={CascadeType.PERSIST, CascadeType.MERGE})
    private Set<GeoLayer> geoLayers;
    
    /* GeoObjectTag */
    @IndexedEmbedded
    @NotAudited
    @OneToMany(mappedBy = "geoObject", cascade=CascadeType.ALL, orphanRemoval=true)
    @JsonIgnore
    private Set<GeoObjectTag> tags;
    
    /* GeoObjectProperties */
    @JsonIgnore
    @NotAudited
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @PrimaryKeyJoinColumn
    private GeoObjectProperties geoObjectProperties;
    
    //=================================================

   	public Long getId() {
        return id;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
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

    public GeoUser getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(GeoUser createdBy) {
        this.createdBy = createdBy;
    }

    public GeoUser getChangedBy() {
        return changedBy;
    }

    public void setChangedBy(GeoUser changedBy) {
        this.changedBy = changedBy;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getChanged() {
        return changed;
    }

    public void setChanged(Date changed) {
        this.changed = changed;
    }

    public String getFiasCode() {
        return fiasCode;
    }

    public void setFiasCode(String fiasCode) {
        this.fiasCode = fiasCode;
    }

    public Geometry getTheGeom() {
        return theGeom;
    }

    public void setTheGeom(Geometry theGeom) {
        this.theGeom = theGeom;
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

    public void setGeoLayers(Set<GeoLayer> geoLayer) {
        this.geoLayers = geoLayer;
    }

    public Set<GeoObjectTag> getTags() {
        return tags;
    }

    public void setTags(Set<GeoObjectTag> tags) {
        this.tags = tags;
    }

    public void addTag(GeoObjectTag tag)
    {
        this.tags.add(tag);
    }

    public GeoObjectProperties getGeoObjectProperties() {
        return geoObjectProperties;
    }

    public void setGeoObjectProperties(GeoObjectProperties geoObjectProperties) {
        this.geoObjectProperties = geoObjectProperties;
    }
    
    @Override
    public boolean equals(Object obj) {
    	if(getId() == null)return false; //a new imported object in list
    	if(obj instanceof GeoObject){
    		return getId().equals(((GeoObject)obj).getId());
    	}
    	return false;
    }
}
