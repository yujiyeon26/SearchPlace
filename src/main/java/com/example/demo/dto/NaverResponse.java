package com.example.demo.dto;

import java.util.List;

public class NaverResponse {
	private String lastBuildDate;
	private int total;
	private int start;
	private int display;
	private List<Document> items;
	public String getLastBuildDate() {
		return lastBuildDate;
	}
	public void setLastBuildDate(String lastBuildDate) {
		this.lastBuildDate = lastBuildDate;
	}
	public int getTotal() {
		return total;
	}
	public void setTotal(int total) {
		this.total = total;
	}
	public int getStart() {
		return start;
	}
	public void setStart(int start) {
		this.start = start;
	}
	public int getDisplay() {
		return display;
	}
	public void setDisplay(int display) {
		this.display = display;
	}
	public List<Document> getItems() {
		return items;
	}
	public void setItems(List<Document> items) {
		this.items = items;
	}
	@Override
	public String toString() {
		return "NaverResponse [lastBuildDate=" + lastBuildDate + ", total=" + total + ", start=" + start + ", display="
				+ display + ", items=" + items + "]";
	}
	
	
}
