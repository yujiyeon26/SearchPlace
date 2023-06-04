# searchPlace
키워드로 장소 검색하기

## 과제내용
키워드로 장소검색하기

### [테스트방법]
1.키워드 검색하기
> curl -X POST 'http://localhost:9090/search' -d ‘은행’  
curl -X POST 'http://localhost:9090/search' -d ‘카카오뱅크’  
curl -X POST 'http://localhost:9090/search' -d ‘카카오 뱅크’  
*검색 키워드는 공백을 허용하여 검색되도록 개발 했기 때문에  ‘카카오 뱅크’ 와 ‘카카오뱅크’ 는 다른 키워드로 인지합니다.  
*두 api간 비교는 공백제거한 업체명으로 비교 했습니다.  
*출력값은 해당 과제 수행 시 필요해보이는 두 api의 공통 field 4개만 출력했습니다.(장소명, 카테고리, 지번주소, 도로명주소)  

2.검색 키워드 목록
> curl -X GET 'http://localhost:9090/selectListTop10'  
*키워드순 정렬 및 최다검색 순 10개에 대한 비즈니스로직을 서버에서 구현했습니다.

## [개발환경]
- Spring boot+Mybatis. 
- JAVA 17  
- Maven  
- Oracle DB
- Junit
- macOS
- Gson(출력포맷을 json으로 뿌려주기위해 사용)
- Redis(동시성 해결을 위해 사용)

## [개발 시 고려사항]
1. 파라미터 인코딩
> - mac terminal에서 cUrl테스트 시 검색 키워드를 한글로 검색하면 인코딩 오류가 있어 controller에서 한글, 숫자, 영문으로만 받도록 정규식으로 필터링 및 decoding하는 부분을 추가했습니다.  
2. 동시성 문제 해결
> - 동시에 다수 사용자가 검색api 호출 시 검색 키워드 count의 데이터정합성을 유지하기 위해 redisson 분산락을 이용하여 동시성 제어를 했습니다.
*src/test/java/com.exmple.demo.service/SearchServiceTest.java 파일에서 테스트 가능합니다.
3. 대용량 트래픽처리
> - 대용량트래픽 처리를 위해 non-Blocking 방식인 webFlux 의 webClient 를 이용한 api 호출방식으로 개발했습니다. (RestTemplate방식으로 개발했다가 변경)
> - api호출 시 에러가 날 수 있어 loop_cnt로 3번 시도하는 로직으로 구현했습니다. (호출 횟수는 임의로 정했고, 변수를 사용해서 변경가능합니다.)
> - webClient는 비동기방식이기 때문에 api 결과값을 기다리기 위해 CountDownLatch를 이용했습니다.
4. 출력값 필터링
> - API출력값에 html태그가 포함되어 있는 경우가 있어 장소명 비교 전, 정규식을 이용해서 html태그를 제거했습니다.
5. 출력포맷 설정(json)
> - 출력값을 json포맷으로 뿌려주기 위해서 gson을 이용했습니다.
6. 자체 API응답코드 사용
> - 모든 출력 시 코드 및 메시지로 정상 및 에러메시지가 포함되어 출력되도록 했습니다.
7. 상수값 별도 파일로 분리
> - 상수로 관리되어야 할 값들은 Constants.java파일에 별도로 관리되도록 했습니다.

### [요구사항 명세서]
0. 공통
- Java11이상 또는 Kotlin 언어로 구현
- Spring boot 사용
- Gradle 또는 Maven 기반 프로젝트
- 유지보수 및 확장에 용이한 아키텍처 설계
- 동시성 이슈를 염두에 둔 설계 및 구현
- 카카오, 네이버 등 검색 API제공자의 다양한 장애 및 연동 오류 발생상황에 대한 고려
- 테스트코드를 통한 프로그램 검증 및 테스트용이성을 높이기 위한 코드 설계
- 새로운 API추가 시 변경 영역 최소화에 대한 고려

**1) 장소검색**
1.  카카오검색 API, 네이버 검색API를 통해 각각 5개씩, 총 10개의 키워드 관련 장소를 검색(특정 서비스 검색 결과가 5개 이하면 최대한 총 10개에 맞게 적용)
2. 두 API결과에 동일하게 나타나는 문서(장소)가 상위에 올 수 있도록 정렬(동일업체 판단기준 자유)
3. 정렬 우선순위는 두 결과값 동일업체 -> 카카오 -> 네이버 결과순으로

**2)검색 키워드 목록**
1. 사용자들이 많이 검색한 순서대로, 최대 10개 목록 표출
2. 키워드별 검색된 횟수도 함께 표기
3. 비즈니스 로직은 모두 서버에서 구현
