package com.example.demo.service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dao.SearchDao;
import com.example.demo.dto.KakaoResponse;
import com.example.demo.dto.NaverResponse;
import com.example.demo.dto.PlaceList;
import com.example.demo.dto.Search;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import common.Constants;
import lombok.RequiredArgsConstructor;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService{
	
	private final Logger logger = LoggerFactory.getLogger(SearchServiceImpl.class);
	
	private static final String QUERY = "query";
    private static final String PAGE = "page";
    private static final String SIZE = "size";
    private static final String AUTHORIZATION = "Authorization";
    private static final String KAKAO_APP_KEY_PREFIX = "KakaoAK ";
    private static final String NAVER_APP_KEY_PREFIX = "X-Naver-Client-";

    @Autowired
    private SearchDao searchDao;

    @Autowired
    private RedissonClient redissonClient;

    @Value("${kakao.app.key}")
    private String KAKAO_APP_KEY;
    
    @Value("${naver.client.id}")
    private String NAVER_APP_ID;
    @Value("${naver.client.secret}")
    private String NAVER_APP_SECRET;
    
    //api호출 횟수
    private final int loop_cnt = 3;
	
    //private RestTemplate restTemplate = new RestTemplate();

    
    @Override
	public List<Search> selectListTop10(){
		List<Search> histList = searchDao.selectListTop10();
		Comparator<Search> byCnt = Comparator.comparing(Search::getCnt);
		Collections.sort(histList, byCnt.reversed());
		
		if(histList.size() > 10) {
			histList.subList(10, histList.size()).clear();
		}
		return histList;
	}
    
    @Override
    public Search selectKeyword(String keyword) {
    	Search outDTO = searchDao.selectKeyword(keyword);
    	return outDTO;
    }
    @Override
    public void insertHist(String keyword) {
    	searchDao.insertHist(keyword);
    }
    @Override
    public void deletetHist(String keyword) {
    	searchDao.deletetHist(keyword);
    }
    
    @Override
    public void saveSearchHistory(String keyword) {
    		final String lockName = keyword+":lock";
    		final RLock lock = redissonClient.getLock(keyword);
    		final String worker = Thread.currentThread().getName();
    		
    		try {
    			if(!lock.tryLock(10,1,TimeUnit.SECONDS)) {
    				logger.info("lock 획득실패");
    				return;
    			}
    			logger.info("lockName: [{}],thread: [{}]",lockName,worker);;
	    		//동일키워드 select
	        	Search inDTO = new Search();
	        	Search outDTO = this.selectKeyword(keyword);
	     
	        	//logger.info("outDTO:[{}]",outDTO);
	        	
	        	if(outDTO!=null && outDTO.getTotalCnt()>0) {
	        		//동일키워드가 있을경우 cnt+1
	        		inDTO.setIdx(outDTO.getIdx());
	        		inDTO.setKeyword(outDTO.getKeyword());
	        		inDTO.setCnt(outDTO.getCnt()+1);
	        		
	        		searchDao.updateKeywordCnt(inDTO);
	        		logger.info("inDTO:[{}]",inDTO);
	        	}else {
	        		//동일키워드가 없으면 InsertHist
	        		this.insertHist(keyword);
	        	}
    		}catch (InterruptedException e) {
				// TODO: handle exception
    			e.printStackTrace();
			}finally {
				if(lock != null && lock.isLocked()) {
					logger.info("lock 해제");
					lock.unlock();
				}
			}
    }
    
    @Override
    public PlaceList searchPlaceForKakao(String keyword, int page, int size) throws IOException, InterruptedException {

        logger.info(" KAKAO APP KEY [{}]", KAKAO_APP_KEY);
        
        CountDownLatch cdl = new CountDownLatch(loop_cnt);
        KakaoResponse kakaoResponse = new KakaoResponse();
        
        // Non-block 방식 webFlux 의 webClient 를 활용한 api 요청
        WebClient webClient =
                WebClient
                        .builder()
                        .baseUrl(Constants.KAKAO_SEARCH_API)
                        .defaultHeader(AUTHORIZATION, KAKAO_APP_KEY_PREFIX + KAKAO_APP_KEY)
                        .build();
        //3회 api호출 시도
        for(int i=0;i<loop_cnt;i++) {
	        webClient
		        .get()
		        .uri(uriBuilder ->
		                uriBuilder
		                        .path(Constants.KAKAO_SEARCH_PATH)
		                        .queryParam(QUERY, keyword)
		                        .queryParam(PAGE, page)
		                        .queryParam(SIZE, size)
		                        .build())
		        .retrieve()
		        .bodyToMono(KakaoResponse.class)
		        .subscribe(e ->{
		        	logger.info("result: [{}], dataType: [{}]",e,e.getClass().getSimpleName());
		        	kakaoResponse.setMeta(e.getMeta());
		        	kakaoResponse.setDocuments(e.getDocuments());
		        	cdl.countDown();
		        });
        }
    	cdl.await();
	
        int pageableCount = kakaoResponse.getMeta().getPageableCount();
        int totalPage = (pageableCount % size == 0) ? pageableCount / size : (pageableCount / size) + 1;

        // output setting
        PlaceList placeList = new PlaceList(page, totalPage, size, pageableCount, kakaoResponse.getDocuments());
		
        return placeList;

		/*
		 * RestTemplate 방식
		 * 
        // header setting
        HttpHeaders headers = new HttpHeaders();
        headers.set(AUTHORIZATION, KAKAO_APP_KEY_PREFIX + KAKAO_APP_KEY);


        // uri
        UriComponents uriComponents = UriComponentsBuilder.fromUriString(Constants.KAKAO_SEARCH_API)
                .path(Constants.KAKAO_SEARCH_PATH)
                .queryParam(QUERY, keyword)
                .queryParam(PAGE, page)
                .queryParam(SIZE, size)
                .build();


        // request
        HttpEntity<?> entity = new HttpEntity<>(headers);

        HttpEntity<String> response = restTemplate.exchange(
                uriComponents.toUriString(),
                HttpMethod.GET,
                entity,
                String.class);
		//ObjectMapper mapper = new ObjectMapper();
        //KakaoResponse kakaoResponse = mapper.readValue(response.getBody(), KakaoResponse.class);
       
        int pageableCount = kakaoResponse.getMeta().getPageableCount();
        int totalPage = (pageableCount % size == 0) ? pageableCount / size : (pageableCount / size) + 1;

        // output setting
        PlaceList placeList = new PlaceList(page, totalPage, size, pageableCount, kakaoResponse.getDocuments());
		*/  
    }

    @Override
    public PlaceList searchPlaceForNaver(String keyword, int page, int size) throws IOException,InterruptedException {

        logger.info(" Naver APP KEY [{}],[{}]", NAVER_APP_ID, NAVER_APP_SECRET);

        CountDownLatch cdl = new CountDownLatch(loop_cnt);
        NaverResponse naverResponse = new NaverResponse();
        
        // Non-block 방식 webFlux 의 webClient 를 활용한 api 요청
        WebClient webClient =
                WebClient
                        .builder()
                        .baseUrl(Constants.NAVER_SEARCH_API)
                        .defaultHeader(NAVER_APP_KEY_PREFIX+"Id", NAVER_APP_ID)
                        .defaultHeader(NAVER_APP_KEY_PREFIX+"Secret", NAVER_APP_SECRET)
                        .build();
        
        //3회 api호출 시도
        for(int i=0;i<loop_cnt;i++) {
	        webClient
		        .get()
		        .uri(uriBuilder ->
		                uriBuilder
				                .path(Constants.NAVER_SEARCH_PATH)
		                        .queryParam(QUERY, keyword)
		                        .queryParam("display", size)
		                        .queryParam("start", page)
		                        .queryParam("sort", "random")
		                        .build())
		        .retrieve()
		        .bodyToMono(NaverResponse.class)
		        .subscribe(e ->{
		        	logger.info("result: [{}], dataType: [{}]",e,e.getClass().getSimpleName());
		        	naverResponse.setLastBuildDate(e.getLastBuildDate());
		        	naverResponse.setTotal(e.getTotal());
		        	naverResponse.setStart(e.getStart());
		        	naverResponse.setDisplay(e.getDisplay());
		        	naverResponse.setItems(e.getItems());
		        	cdl.countDown();
		        });
        }
  
        cdl.await();
      
        int pageableCount = naverResponse.getDisplay();
        int totalPage = (pageableCount % size == 0) ? pageableCount / size : (pageableCount / size) + 1;

        // output setting
        PlaceList placeList = new PlaceList(page, totalPage, size, pageableCount,naverResponse.getItems());

        return placeList;
        
        /*
         * RestTemplate 방식
         * 
        // header setting
        HttpHeaders headers = new HttpHeaders();
        headers.set(NAVER_APP_KEY_PREFIX+"Id", NAVER_APP_ID);
        headers.set(NAVER_APP_KEY_PREFIX+"Secret", NAVER_APP_SECRET);

        // uri
        UriComponents uriComponents = UriComponentsBuilder.fromUriString(Constants.NAVER_SEARCH_API)
                .path(Constants.NAVER_SEARCH_PATH)
                .queryParam(QUERY, keyword)
                .queryParam("display", size)
                .queryParam("start", page)
                .queryParam("sort", "random")
                .build();

        // request
        HttpEntity<?> entity = new HttpEntity<>(headers);

        HttpEntity<String> response = restTemplate.exchange(
                uriComponents.toUriString(),
                HttpMethod.GET,
                entity,
                String.class);

        ObjectMapper mapper = new ObjectMapper();
        NaverResponse naverResponse = mapper.readValue(response.getBody(), NaverResponse.class);
        */
    }
}
