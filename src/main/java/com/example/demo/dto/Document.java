package com.example.demo.dto;

import java.util.Map;
import java.lang.reflect.Field;
import java.util.HashMap;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.Expose;

@JsonIgnoreProperties(ignoreUnknown=true)
public class Document {

    @JsonProperty("place_name")
    @JsonAlias("title")
    @Expose
    private String placeName;

    @JsonProperty("category_name")
    @JsonAlias("category")
    @Expose
    private String categoryName;

    @JsonProperty("address_name")
    @JsonAlias("address")
    @Expose
    private String addressName;

    @JsonProperty("road_address_name")
    @JsonAlias("roadAddress")
    @Expose
    private String roadAddressName;
    
 

    public void setPlaceName(String placeName) {
		this.placeName = placeName.replaceAll("<(/)?([a-zA-Z]*)(\\s[a-zA-Z]*=[^>]*)?(\\s)*(/)?>", "");
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName.replaceAll("<(/)?([a-zA-Z]*)(\\s[a-zA-Z]*=[^>]*)?(\\s)*(/)?>", "");
	}

	public void setAddressName(String addressName) {
		this.addressName = addressName.replaceAll("<(/)?([a-zA-Z]*)(\\s[a-zA-Z]*=[^>]*)?(\\s)*(/)?>", "");
	}

	public void setRoadAddressName(String roadAddressName) {
		this.roadAddressName = roadAddressName.replaceAll("<(/)?([a-zA-Z]*)(\\s[a-zA-Z]*=[^>]*)?(\\s)*(/)?>", "");
	}

	public String getPlaceName() {
        return placeName;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public String getAddressName() {
        return addressName;
    }

    public String getRoadAddressName() {
        return roadAddressName;
    }
 
    @Override
    public String toString() {
        return "Document{" +
                ", placeName='" + placeName + '\'' +
                ", categoryName='" + categoryName + '\'' +
                ", addressName='" + addressName + '\'' +
                ", roadAddressName='" + roadAddressName + '\'' +
                '}';
    }

}
