package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.demo.dao.SearchDao;
import com.example.demo.dto.Search;

@SpringBootTest
class SearchServiceTest {
    
    @Autowired
    private SearchService searchService;
    
    private static String keyword = "카카오뱅크";
    
    @BeforeEach
    public void delete() {
    	searchService.deletetHist(keyword);
    }
    
	@Test
    public void 동시에_100명이_검색() throws InterruptedException {
		int threadCount = 100;
		String t = keyword;
        ExecutorService executorService = Executors.newFixedThreadPool(30);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                	searchService.saveSearchHistory(t);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        Search tDto = searchService.selectKeyword(t);

        assertEquals(100, tDto.getCnt());
    }

}
