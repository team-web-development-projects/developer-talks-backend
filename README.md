# developer-talks Backend
### 프로젝트 목표
- 개발자 커뮤니티 사이트를 참고해 커뮤니티(글 작성, 소통), 스터디, 관리자 기능이 있는 사이트 개발
- 프론트와 백엔드간의 소통 및 협업 경험
- Restful API 설계, 코드 리뷰, 리팩토링
- 새로운 RDBMS 사용 (postgreSQL)
- aws를 이용한 서버 배포 및 관리(nginx, s3)

### 개발 기간
2023.04.19 ~ 2023.09.19

### 프론트
<https://github.com/team-web-development-projects/developer-talks-frontend>

## :books: 백엔드 기술 스택
- Java jdk 17
- Spring Boot 3.0.5
- Spring Data JPA, Querydsl
- Spring Security, JWT, OAuth2
- PostgreSQL, Redis
- Gmail SMTP
- WebSocket, STOMP, FCM
- Springdoc-Swagger, Postman
- AWS(EC2, S3)
- 소통: github, notion, discord

## 배포
- 사용자: <https://developer-talks.com/>    
- 관리자: <https://team-web-development-projects.github.io/developer-talks-admin/>
<!--  # 실행 방법
## 로컬에서 실행하는 방법
1. jdk 설치 (https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
2. 환경 변수 설정 (https://coding-factory.tistory.com/823)
3. 백엔드 레포지토리 git clone
4. cmd창에서 서버 실행 (https://ottl-seo.tistory.com/21) -->

## swagger
http://dtalks-api.site/swagger-ui/index.html#/

## ERD
<!-- ![캡처](https://github.com/team-web-development-projects/developer-talks-backend/assets/39542757/c3a29c67-ca3d-4ff3-b604-9e564aff1aea) -->
![dtalks](https://github.com/team-web-development-projects/developer-talks-backend/assets/68698007/823912f3-f3b5-4553-8a5b-719961391f07)

## 주요 기능
### 사용자 기능
- 로그인    
  - JWT, OAuth2, Security, 로그인 시 헤더로 토큰 반환
- 사용자
  - 사용자의 최근활동 조회 (게시글, 댓글, 질문글, 답변)
- 게시글 작성, 수정, 추천, 즐겨찾기, 검색
  -  이미지 업로드 및 수정 s3 사용    
- 댓글 작성, 수정
- 알림
  - FCM 사용: 로그인 시 FCM 토큰 여부 검사 및 저장
  - ApplicationEventPublisher.publishEvent로 알림 발생
- 쪽지
- IT 뉴스
- 질문글 작성, 수정, 추천, 즐겨찾기, 검색
- 답변 작성, 수정
- 신고
  - 사용자 신고, 게시글 신고
- 스터디

### 관리자 기능
- 공지사항 작성
- 문의글 관리
- 게시글 관리
  - 게시글 조회, 접근 금지, 복구
- 사용자 관리
  - 사용자 조회, 정지, 복구, 임시 비밀번호 발급(메일 전송)
- 신고 관리
  - 조회, 처리
- 일일 접속자 그래프

### 아쉬운 점
- 알림: SSE를 사용하려 했으나 구현 후 로컬은 잘 되나 서버에서는 timeout이 제대로 적용되지 않고 연결 해제 전 반복적인 재연결이 이루어짐. 해결되지 않아 FCM으로 변경
- 테스트 코드 작성 부족

### 패키지 구조
```txt
└── src
    ├── main
    │   ├── java
    │   │   └── com.dtalks.dtalks
    │   │       ├── admin
    │   │       │   ├── announcement
    │   │       │   │   ├── controller
    │   │       │   │   ├── dto
    │   │       │   │   ├── entity
    │   │       │   │   ├── repository
    │   │       │   │   └── service
    │   │       │   ├── inquiry
    │   │       │   ├── post
    │   │       │   ├── report
    │   │       │   ├── user
    │   │       │   └── visitor
    │   │       ├── base
    │   │       │   ├── component
    │   │       │   │   └── S3Uploader.java
    │   │       │   ├── config
    │   │       │   ├── dto
    │   │       │   │   └── DocumentResponseDto.java
    │   │       │   ├── entity
    │   │       │   │   ├── BaseTimeEntity.java
    │   │       │   │   └── Document.java
    │   │       │   ├── repository
    │   │       │   │   └── DocumentRepository.java
    │   │       │   ├── service
    │   │       │   │   └── Schedule.java // 기간이 지난 알림 삭제, 일시 정지된 계정 기간이 자나면 해제
    │   │       │   └── validation
    │   │       │       ├── EnumValidator.java
    │   │       │       ├── FileValidtaor.java
    │   │       │       └── ValidEnum.java 
    │   │       ├── board
    │   │       │   ├── comment
    │   │       │   └── post
    │   │       ├── exception
    │   │       │   ├── dto
    │   │       │   │   └── ErrorResponseDto.java
    │   │       │   ├── exception
    │   │       │   │   └── CustomException.java
    │   │       │   ├── ErrorCode.java                          
    │   │       │   └── GlobalExceptionHandler.java
    │   │       ├── fcm
    │   │       │   ├── FCMListener.java       
    │   │       │   ├── FCMService.java
    │   │       │   └── FCMTokenManager.java  
    │   │       ├── message   
    │   │       ├── news
    │   │       ├── notification
    │   │       ├── qna
    │   │       ├── report
    │   │       ├── studyroom
    │   │       └── user // JWT 관련 포함
    │   └── resources
    │       ├── firebase
    │       │   └── firebase-config.json    // FCM 사용을 위한 설정 파일     
    │       ├── application.properties      // 깃헙에는 안 올라감
    │       └── messages.properties         // 깃헙에는 안 올라감
```
