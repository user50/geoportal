package org.w2fc.geoportal.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@org.hibernate.annotations.Entity(
        dynamicUpdate = true,     /* Optimize update sql */
        dynamicInsert = true        /* Optimize insert sql */
)

@NamedQueries({
	@NamedQuery(
			name  = GeoSettings.Query.BY_NAME,
			query = "FROM GeoSettings gs"
					+ " WHERE gs.key = ?")
})

@Table (name = "GEO_SETTINGS")
public class GeoSettings  extends AbstractDomain<GeoSettings>{
	
	public static final class Query {
		public static final String BY_NAME = "GeoSettings_BY_NAME";
	}
	
	@Id
	private Long id;
    
    @Column 
    private String key;
    
    @Column
    private String value;

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
}
