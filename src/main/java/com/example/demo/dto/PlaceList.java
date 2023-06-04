package com.example.demo.dto;

import java.util.List;

import com.google.gson.annotations.Expose;

import lombok.Data;

@Data
public class PlaceList {
	private int currentPage;
    private int totalPage;
    private int size;
    private int pageableCount;
    @Expose
    private List<Document> documents;

    public PlaceList(int currentPage, int totalPage, int size, int pageableCount, List<Document> documents) {
        this.currentPage = currentPage;
        this.totalPage = totalPage;
        this.size = size;
        this.pageableCount = pageableCount;
        this.documents = documents;
    }

    public PlaceList() {

	}

	public int getCurrentPage() {
        return currentPage;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public int getSize() {
        return size;
    }

    public int getPageableCount() {
        return pageableCount;
    }

    public List<Document> getDocuments() {
        return documents;
    }
    
    

    public void setDocuments(List<Document> documents) {
		this.documents = documents;
	}


    @Override
    public String toString() {
        return "PlaceList{" +
                "currentPage=" + currentPage +
                ", totalPage=" + totalPage +
                ", size=" + size +
                ", pageableCount=" + pageableCount +
                ", documents=" + documents +
                '}';
    }
}
