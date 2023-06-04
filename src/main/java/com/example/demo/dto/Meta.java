package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public class Meta {
	
	 	@JsonProperty("total_count")
	 	@JsonAlias("total")
	    private int totalCount;

	    @JsonProperty("pageable_count")
	    @JsonAlias("display")
	    private int pageableCount;

	    @JsonProperty("is_end")
	    private boolean isEnd;

	    @JsonProperty("same_name")
	    private SameName sameName;

	    public int getTotalCount() {
	        return totalCount;
	    }

	    public int getPageableCount() {
	        return pageableCount;
	    }

	    public boolean isEnd() {
	        return isEnd;
	    }

	    public SameName getSameName() {
	        return sameName;
	    }

	    @Override
	    public String toString() {
	        return "Meta{" +
	                "totalCount=" + totalCount +
	                ", pageableCount=" + pageableCount +
	                ", isEnd=" + isEnd +
	                ", sameName=" + sameName +
	                '}';
	    }

		public void setTotalCount(int totalCount) {
			this.totalCount = totalCount;
		}

		public void setPageableCount(int pageableCount) {
			this.pageableCount = pageableCount;
		}

		public void setEnd(boolean isEnd) {
			this.isEnd = isEnd;
		}

		public void setSameName(SameName sameName) {
			this.sameName = sameName;
		}
}
