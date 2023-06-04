package com.example.demo.controller;

import com.google.gson.*;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.Document;
import com.example.demo.dto.PlaceList;
import com.example.demo.dto.Search;
import com.example.demo.service.SearchServiceImpl;

import common.Constants;

@RestController
public class SearchController {
	
	private final Logger logger = LoggerFactory.getLogger(SearchController.class);

	@Autowired
	SearchServiceImpl searchService;
	
	private Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().disableHtmlEscaping().setPrettyPrinting().create();
	
	/*키워드로 장소검색*/
	@PostMapping("/search")
	public ResponseEntity searchPlaceByKeyword(@RequestBody String keyword) throws Exception {

		int page = 1;
        int size = Constants.SEARCH_CNT; //검색결과 갯수지정(5개)
        HashMap<String, Object> resultMap = new HashMap<String, Object>();
        PlaceList rpl = new PlaceList();
        
        String decode = URLDecoder.decode(keyword, "utf-8").replaceAll("[^\uAC00-\uD7A30-9a-zA-Z\s/g]", ""); //디코딩 및 숫자,영어,한글만 체크(띄어쓰기 포함)
        
        logger.info(" /search api start. keyword [{}], page [{}], size [{}], decode[{}]",keyword,page,size,decode);
        
        //save history(검색 키워드 DB저장)
        searchService.saveSearchHistory(decode);
        
        try {
	        //카카오 장소검색 API 호출
	        PlaceList list = searchService.searchPlaceForKakao(decode,page,size); 
	        //네이버 장소검색 API 호출
	        PlaceList list2 = searchService.searchPlaceForNaver(decode,page,size); 
	        
	        if(list.getDocuments().size() >0 && list2.getDocuments().size()>0) {
	        	
	        	//결과 장소 정렬(동일업체->카카오->네이버)
	            rpl.setDocuments(compareLists(list.getDocuments(),list2.getDocuments()));
	            
				resultMap.put("res_cd", "0000");
				resultMap.put("res_msg", "정상");
				resultMap.put("placeList", rpl);			
			}else{
				resultMap.put("res_cd", "1000");
				resultMap.put("res_msg", "검색 결과가 없습니다.");
				resultMap.put("placeList", rpl);
			}
        }catch (Exception e) {
			// TODO: handle exception
        	resultMap.put("res_cd", "9999");
			resultMap.put("res_msg", "시스템 에러");
			resultMap.put("placeList", rpl);
		}
        
        //결과값 json방식으로 parsing
        String json = gson.toJson(resultMap);
        return new ResponseEntity(json, HttpStatus.OK);
    }
	
	/*api 결과값 비교 및 정렬메소드*/
	private List<Document> compareLists(List<Document> list, List<Document> list2) {
		
		List<Document> copyList = new ArrayList<Document>(list);
		List<Document> copyList2 = new ArrayList<Document>(list2);
		
		
		List<Document> common = copyList.stream().filter(two->copyList2.stream().anyMatch(one->one.getPlaceName().replaceAll("\\s", "").equals(two.getPlaceName().replaceAll("\\s", "")))).collect(Collectors.toList());

		common.addAll(copyList.stream()
	            .filter(element -> !common.stream().anyMatch(e->e.getPlaceName().replaceAll("\\s", "").equals(element.getPlaceName().replaceAll("\\s", ""))))
	            .collect(Collectors.toList()));
		common.addAll(copyList2.stream()
	            .filter(element -> !common.stream().anyMatch(e->e.getPlaceName().replaceAll("\\s", "").equals(element.getPlaceName().replaceAll("\\s", ""))))
	            .collect(Collectors.toList()));
		
        return common; 
    }
	
	/*검색 키워드 목록 Top10*/
	@GetMapping("/selectListTop10")
	public ResponseEntity<Search> selectListTop10() {
		HashMap<String, Object> resultMap = new HashMap<String, Object>();
		List<Search> histList = null;

		try {
			//검색 키워드 목록 Top10조회
			histList = searchService.selectListTop10();
			
			if(histList.size() >0) {
				resultMap.put("res_cd", "0000");
				resultMap.put("res_msg", "정상");
				resultMap.put("searchListTop10", histList);			
			}else {
				resultMap.put("res_cd", "1000");
				resultMap.put("res_msg", "검색 결과가 없습니다.");
				resultMap.put("searchListTop10",histList);
			}
			
		}catch (Exception e) {
			// TODO: handle exception
			resultMap.put("res_cd", "9999");
			resultMap.put("res_msg", "시스템 에러");
			resultMap.put("searchListTop10", histList);
		}
		//결과값 json방식으로 parsing
		String histListTojson = gson.toJson(resultMap);
		return new ResponseEntity(histListTojson, HttpStatus.OK);
	}
}