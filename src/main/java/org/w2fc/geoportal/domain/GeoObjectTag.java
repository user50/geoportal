package org.w2fc.geoportal.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;
import org.hibernate.search.annotations.Field;


@Entity
@org.hibernate.annotations.Entity(
        dynamicUpdate = true,     /* Optimize update sql */
        dynamicInsert = true        /* Optimize insert sql */
)

//@Indexed
@Audited
@Table (name = "GEO_OBJECT_TAG")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(namespace = "http://ws.portal.maps.yarcloud.ru/tags/getTagsResponse")
public class GeoObjectTag extends AbstractDomain<GeoObjectTag>{

    @Id
    @Column (name = "id", unique = true, nullable = false)
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @XmlTransient
    private Long id;
    
    @Field
    @Column (name = "key", nullable = false)
    @XmlElement(name="key")
    private String key;
    
    @Field
    @Column (name = "value", nullable = true)
    @XmlElement(name="value")
    private String value;
        
    @Version 
    @Column(name = "VERSION")
    @XmlTransient
    private Integer version;
    
    
    /* GeoObject */
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED) 
    @XmlTransient
    @ManyToOne
    @JoinColumn(name = "object_id", nullable=false)
    private GeoObject geoObject;

    
    //=================================================
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
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
