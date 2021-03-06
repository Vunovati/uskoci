
package com.randombit.uskoci.card.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
// JAX-RS supports an automatic mapping from JAXB annotated class to XML and JSON
public class Card {
    private String id = "";
    private String summary = "";
    private String type = "";
    private String description = "";
    private String position = "";

    private String value = "";

    private String color = "";
    public Card() {
    }

    public Card(String id, String summary, String type, String description, String position, String value) {
        this.id = id;
        this.summary = summary;
        this.type = type;
        this.description = description;
        this.position = position;
        this.value = value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @XmlElement
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @XmlElement
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @XmlElement
    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    @XmlElement
    public String getDescription() {
        return description;
    }

    @XmlElement
    public String getPosition() {
        return position;
    }

    @XmlElement
    public String getValue() {
        return value;
    }
    
    @XmlElement
    public String getColor() {
        return color;
    }
    
    public void setColor(String color){
    	this.color = color;
    }
    

    public void setDescription(String description) {
        this.description = description;
    }


}
