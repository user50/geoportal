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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;


@Entity
@org.hibernate.annotations.Entity(
        dynamicUpdate = true,     /* Optimize update sql */
        dynamicInsert = true        /* Optimize insert sql */
)

@Table (name = "AD_TAGS_DICT_VALUES")
public class AddnsTagsDictionaryValues extends AbstractDomain<AddnsTagsDictionaryValues>{

    @Id
    @Column (name = "id", unique = true, nullable = false)
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    
    @Column (name = "value", nullable = true)
    private String value;
    
    
    @Version 
    @Column(name = "VERSION")
    private Integer version;
    
    
    /* to AddnsTagsDictionary */
    @ManyToOne
    @JoinColumn(name = "tags_dict_id", nullable=false)
    private AddnsTagsDictionary dictionary;


    //=================================================
    
    public Long getId() {
        return id;
    }


    public void setId(Long id) {
        this.id = id;
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


    @JsonIgnore
    public AddnsTagsDictionary getDictionary() {
        return dictionary;
    }


    @JsonProperty
    public void setDictionary(AddnsTagsDictionary dictionary) {
        this.dictionary = dictionary;
    }
}
