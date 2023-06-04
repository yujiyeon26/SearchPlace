package com.example.demo.service;

import java.io.IOException;
import java.util.List;

import com.example.demo.dto.PlaceList;
import com.example.demo.dto.Search;

public interface SearchService {

	public List<Search> selectListTop10();
	public Search selectKeyword(String keyword);
	public void insertHist(String keyword);
	public void deletetHist(String keyword);
	public void saveSearchHistory(String keyword);
	public PlaceList searchPlaceForKakao(String keyword, int page, int size) throws IOException,InterruptedException;
	public PlaceList searchPlaceForNaver(String keyword, int page, int size) throws IOException,InterruptedException;
}
