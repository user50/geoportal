package org.w2fc.geoportal.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.hibernate.envers.RelationTargetAuditMode;


@Entity
@org.hibernate.annotations.Entity(
        dynamicUpdate = true,     /* Optimize update sql */
        dynamicInsert = true        /* Optimize insert sql */
)

@Audited
@Table (name = "GEO_LAYER_METADATA")
public class GeoLayerMetadata extends AbstractDomain<GeoLayerMetadata>{
    
    @GenericGenerator(
            name = "generator", 
            strategy = "foreign", 
            parameters = @Parameter(name = "property", value = "geoLayer"))
    @Id
    @GeneratedValue(generator = "generator")
    private Long id;
    
    /*  GeoUser */
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED) 
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "changed_by", nullable=true)
    private GeoUser changedBy;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column (name = "changed", nullable = true)
    private Date changed;
    
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED) 
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable=true)
    private GeoUser createdBy;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column (name = "created", nullable = true)
    private Date created;
    
    @Column (name = "view_by_object", nullable = true)
    private Boolean viewByObject;
    
    @Column (name = "owner_company", nullable = true)
    private String ownerCompany;
    
    @Column (name = "owner_name", nullable = true)
    private String ownerName;
    
    @Column (name = "owner_email", nullable = true)
    private String ownerEmail;
    
    @Column (name = "owner_phone", nullable = true)
    private String ownerPhone;
    
    
    @Column (name = "desc_spatial_data", nullable = true)
    @Lob()
    @Type(type = "org.hibernate.type.TextType")
    private String descSpatialData;
    
    @Column (name = "source_spatial_data", nullable = true)
    private String sourceSpatialData;
    
    @Column (name = "doc_regulation", nullable = true)
    private String docRegulation;
    
    @Column (name = "access_level", nullable = true)
    private String accessLevel;
    
    @Column (name = "access_conditions", nullable = true)
    @Lob()
    @Type(type = "org.hibernate.type.TextType")
    private String accessConditions;
    
    @Column (name = "map_accuracy", nullable = true)
    private String mapAccuracy;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column (name = "last_update_metadata", nullable = true)
    private Date lastUpdateMetadata;
    
    @Column (name = "last_update_spatial_data", nullable = true)
    private String lastUpdateSpatialData;
    
    @Column (name = "update_frequency", nullable = true)
    private String updateFrequency;
    
    @Column (name = "coordinate_system", nullable = true)
    private String coordinateSystem;
    
    @Column (name = "coverage_area", nullable = true)
    private String coverageArea;
    
    @Column (name = "data_amount", nullable = true)
    private String dataAmount;
    
    @Column (name = "export_format", nullable = true)
    private String exportFormat;
    
    
    @Version 
    @Column(name = "VERSION")
    private Integer version;
    
    /* GeoLayer */
    @NotAudited
    @OneToOne(fetch = FetchType.EAGER)
    @PrimaryKeyJoinColumn
    private GeoLayer geoLayer;

    
    public Boolean getViewByObject() {
        return null == viewByObject? false : viewByObject;
    }


    //=================================================

    public Long getId() {
        return id;
    }


    public void setId(Long id) {
        this.id = id;
    }


    public GeoUser getChangedBy() {
        return changedBy;
    }


    public void setChangedBy(GeoUser changedBy) {
        this.changedBy = changedBy;
    }


    public Date getChanged() {
        return changed;
    }


    public void setChanged(Date changed) {
        this.changed = changed;
    }


    public void setViewByObject(Boolean viewByObject) {
        this.viewByObject = viewByObject;
    }


    public String getOwnerCompany() {
        return ownerCompany;
    }


    public void setOwnerCompany(String ownerCompany) {
        this.ownerCompany = ownerCompany;
    }


    public String getOwnerName() {
        return ownerName;
    }


    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }


    public String getOwnerEmail() {
        return ownerEmail;
    }


    public void setOwnerEmail(String ownerEmail) {
        this.ownerEmail = ownerEmail;
    }


    public String getOwnerPhone() {
        return ownerPhone;
    }


    public void setOwnerPhone(String ownerPhone) {
        this.ownerPhone = ownerPhone;
    }


    public String getDescSpatialData() {
        return descSpatialData;
    }


    public void setDescSpatialData(String descSpatialData) {
        this.descSpatialData = descSpatialData;
    }


    public String getSourceSpatialData() {
        return sourceSpatialData;
    }


    public void setSourceSpatialData(String sourceSpatialData) {
        this.sourceSpatialData = sourceSpatialData;
    }


    public String getDocRegulation() {
        return docRegulation;
    }


    public void setDocRegulation(String docRegulation) {
        this.docRegulation = docRegulation;
    }


    public String getAccessLevel() {
        return accessLevel;
    }


    public void setAccessLevel(String accessLevel) {
        this.accessLevel = accessLevel;
    }


    public String getAccessConditions() {
        return accessConditions;
    }


    public void setAccessConditions(String accessConditions) {
        this.accessConditions = accessConditions;
    }


    public String getMapAccuracy() {
        return mapAccuracy;
    }


    public void setMapAccuracy(String mapAccuracy) {
        this.mapAccuracy = mapAccuracy;
    }


    public Date getLastUpdateMetadata() {
        return lastUpdateMetadata;
    }


    public void setLastUpdateMetadata(Date lastUpdateMetadata) {
        this.lastUpdateMetadata = lastUpdateMetadata;
    }


    public String getLastUpdateSpatialData() {
        return lastUpdateSpatialData;
    }


    public void setLastUpdateSpatialData(String lastUpdateSpatialData) {
        this.lastUpdateSpatialData = lastUpdateSpatialData;
    }


    public String getUpdateFrequency() {
        return updateFrequency;
    }


    public void setUpdateFrequency(String updateFrequency) {
        this.updateFrequency = updateFrequency;
    }


    public String getCoordinateSystem() {
        return coordinateSystem;
    }


    public void setCoordinateSystem(String coordinateSystem) {
        this.coordinateSystem = coordinateSystem;
    }


    public String getCoverageArea() {
        return coverageArea;
    }


    public void setCoverageArea(String coverageArea) {
        this.coverageArea = coverageArea;
    }


    public String getDataAmount() {
        return dataAmount;
    }


    public void setDataAmount(String dataAmount) {
        this.dataAmount = dataAmount;
    }


    public String getExportFormat() {
        return exportFormat;
    }


    public void setExportFormat(String exportFormat) {
        this.exportFormat = exportFormat;
    }


    public Integer getVersion() {
        return version;
    }


    public void setVersion(Integer version) {
        this.version = version;
    }


    public GeoLayer getGeoLayer() {
        return geoLayer;
    }


    public void setGeoLayer(GeoLayer geoLayer) {
        this.geoLayer = geoLayer;
    }


	public GeoUser getCreatedBy() {
		return createdBy;
	}


	public void setCreatedBy(GeoUser createdBy) {
		this.createdBy = createdBy;
	}


	public Date getCreated() {
		return created;
	}


	public void setCreated(Date created) {
		this.created = created;
	}
}
