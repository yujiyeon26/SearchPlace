package com.example.demo.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.demo.dto.Search;

@Mapper
public interface SearchDao {
	public List<Search> selectListTop10();
	public Search selectKeyword(@Param("keyword") String keyword);
	public Integer insertHist(@Param("keyword") String keyword);
	public Integer deletetHist(@Param("keyword") String keyword);
	public Integer updateKeywordCnt(@Param("Search") Search search);
}
