package com.example.demo.dto;
import java.util.List;
public class KakaoResponse {
	private Meta meta;
    private List<Document> documents;

    public Meta getMeta() {
        return meta;
    }
    public List<Document> getDocuments() {
        return documents;
    }
    
    public void setMeta(Meta meta) {
		this.meta=meta;
	}
	public void setDocuments(List<Document> documents) {
		this.documents = documents;
	}
	@Override
    public String toString() {
        return "KakaoResponse{" +
                "meta=" + meta +
                ", documents=" + documents +
                '}';
    }
}
