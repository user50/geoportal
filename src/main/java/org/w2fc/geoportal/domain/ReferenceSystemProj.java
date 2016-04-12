package org.w2fc.geoportal.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@org.hibernate.annotations.Entity(
        dynamicUpdate = true,     /* Optimize update sql */
        dynamicInsert = true        /* Optimize insert sql */
)

@Table (name = "GEO_PROJECTIONS")
public class ReferenceSystemProj extends AbstractDomain<ReferenceSystemProj>{

    @Id
	@Column (name = "key", nullable = false)
    private String key;
    
    @Column (name = "description")
    private String description;
    
    @Column(name = "wkt")
    private String wkt;

    @Column(name = "provided_sr")
    private String providedSpatialReference;
    
    @Column(name = "dx")
    private Double dx;
    
    @Column(name = "dy")
    private Double dy;
    
    @Column(name = "q")
    private Double Q;
    
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getWkt() {
		return wkt;
	}

	public void setWkt(String wkt) {
		this.wkt = wkt;
	}

	public String getProvidedSpatialReference() {
		return providedSpatialReference;
	}

	public void setProvidedSpatialReference(String providedSpatialReference) {
		this.providedSpatialReference = providedSpatialReference;
	}

	public Double getDx() {
		return dx;
	}

	public void setDx(Double dx) {
		this.dx = dx;
	}

	public Double getQ() {
		return Q;
	}

	public void setQ(Double q) {
		Q = q;
	}

	public Double getDy() {
		return dy;
	}

	public void setDy(Double dy) {
		this.dy = dy;
	}
}
