package org.w2fc.geoportal.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Version;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;


@Entity
@org.hibernate.annotations.Entity(
        dynamicUpdate = true,     /* Optimize update sql */
        dynamicInsert = true        /* Optimize insert sql */
)

@Table (name = "AD_RATING")
public class AddnsRating extends AbstractDomain<AddnsRating>{

    @GenericGenerator(
            name = "generator", 
            strategy = "foreign", 
            parameters = @Parameter(name = "property", value = "geoLayer"))
    @Id
    @GeneratedValue(generator = "generator")
    private Long id;
    
    @Column (name = "clicks", nullable = true)
    private Long clicks;
    
    @Column (name = "likes", nullable = true)
    private Long likes;
    
    @Version 
    @Column(name = "VERSION")
    private Integer version;
    
    
    /* to PortalLayer */
    @OneToOne(fetch = FetchType.LAZY)
    @PrimaryKeyJoinColumn
    private GeoLayer geoLayer;


    //=================================================
    
    
    public Long getId() {
        return id;
    }


    public void setId(Long id) {
        this.id = id;
    }


    public Long getClicks() {
        return clicks;
    }


    public void setClicks(Long clicks) {
        this.clicks = clicks;
    }


    public Long getLikes() {
        return likes;
    }


    public void setLikes(Long likes) {
        this.likes = likes;
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
}
