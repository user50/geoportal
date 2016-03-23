package org.w2fc.geoportal.domain;

import java.sql.Blob;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Version;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.envers.NotAudited;

import com.fasterxml.jackson.annotation.JsonIgnore;


@Entity
@org.hibernate.annotations.Entity(
        dynamicUpdate = true,     /* Optimize update sql */
        dynamicInsert = true        /* Optimize insert sql */
)

@Table (name = "GEO_OBJECT_PROPERTIES")
public class GeoObjectProperties extends AbstractDomain<GeoObjectProperties> {
    
    @GenericGenerator(
            name = "generator", 
            strategy = "foreign", 
            parameters = @Parameter(name = "property", value = "geoObject"))
    @Id
    @GeneratedValue(generator = "generator")
    private Long id;
    
    @Column (name = "icon", nullable = true)
    @Lob()
    @JsonIgnore
    private Blob icon;
    
    @Column (name = "line_color", nullable = true)
    private String lineColor;
    
    @Column (name = "line_weight", nullable = true)
    private Integer lineWeight;

    @Column (name = "fill_color", nullable = true)
    private String fillColor;
    
    @Column (name = "fill_opacity", nullable = true)
    private Double fillOpacity;
    
    
    @Version 
    @Column(name = "VERSION")
    private Integer version;
    
    
    /* GeoObject */
    @NotAudited
    @OneToOne
    @PrimaryKeyJoinColumn
    private GeoObject geoObject;


    
    //===============================================================
    
    public Long getId() {
        return id;
    }


    public void setId(Long id) {
        this.id = id;
    }


    public Blob getIcon() {
        return icon;
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


    public Double getFillOpacity() {
        return fillOpacity;
    }


    public void setFillOpacity(Double fillOpacity) {
        this.fillOpacity = fillOpacity;
    }


    public Integer getVersion() {
        return version;
    }


    public void setVersion(Integer version) {
        this.version = version;
    }


    public GeoObject getGeoObject() {
        return geoObject;
    }


    public void setGeoObject(GeoObject geoObject) {
        this.geoObject = geoObject;
    }
}
