package org.w2fc.geoportal.domain;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;


@Entity
@org.hibernate.annotations.Entity(
        dynamicUpdate = true,     /* Optimize update sql */
        dynamicInsert = true        /* Optimize insert sql */
)

@Table (name = "AD_TAGS_DICT")
public class AddnsTagsDictionary extends AbstractDomain<AddnsTagsDictionary>{

    @Id
    @Column (name = "id", unique = true, nullable = false)
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    
    @Column (name = "key", unique=true, nullable = true)
    private String key;
    
    @Column (name = "alias", nullable = true)
    private String alias;
    
    @Column (name = "type", nullable = true)
    private String type;
        
    
    @Version 
    @Column(name = "VERSION")
    private Integer version;
    
    
    /*  AddnsTagsDictionaryValues */
    @OneToMany(mappedBy = "dictionary", cascade=CascadeType.ALL, orphanRemoval=true, fetch = FetchType.EAGER)
    private Set<AddnsTagsDictionaryValues> values;

    
    
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


    public String getAlias() {
        return alias;
    }


    public void setAlias(String alias) {
        this.alias = alias;
    }


    public String getType() {
        return type;
    }


    public void setType(String type) {
        this.type = type;
    }


    public Integer getVersion() {
        return version;
    }


    public void setVersion(Integer version) {
        this.version = version;
    }


    public Set<AddnsTagsDictionaryValues> getValues() {
        return values;
    }


    public void setValues(Set<AddnsTagsDictionaryValues> values) {
        this.values = values;
    }
}
