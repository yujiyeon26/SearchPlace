package com.example.demo.dto;

import org.springframework.data.annotation.Id;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.Expose;

import lombok.Data;
import lombok.ToString.Exclude;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@Data
public class Search {
		@Id
		private int idx;
		@Expose
		private String keyword;
		@Expose
		private int cnt;
		private int totalCnt;

		
		public int getIdx() {
			return idx;
		}

		public void setIdx(int idx) {
			this.idx = idx;
		}

		public String getKeyword() {
			return keyword;
		}

		public void setKeyword(String keyword) {
			this.keyword = keyword;
		}

		public int getCnt() {
			return cnt;
		}

		public void setCnt(int cnt) {
			this.cnt = cnt;
		}

		public int getTotalCnt() {
			return totalCnt;
		}

		public void setTotalCnt(int totalCnt) {
			this.totalCnt = totalCnt;
		}

		@Override
		public String toString() {
			return "SearchVO [keyword=" + keyword + ", cnt=" + cnt + "]";
		}
		
}
