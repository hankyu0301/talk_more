# TALK_MORE

---

게시글과 댓글을 작성하고 회원간 메세지를 통해 소통가능한 게시판입니다.

- 프로젝트 명칭 : 커뮤니티 게시판 (Talk_More)
- 개발 인원 : 1명
- 개발 기간 : 2023.09.12~

📄 [API 명세](https://www.notion.so/d7225fe2a7304890adc083d624fb1854?pvs=21)

---

### 사용 Skills

- Java, SpringBoot, Gradle
- MariaDB, Redis
- Junit5, Mockito
- Github, Jenkins, Docker
- AWS EC2, AWS RDS, AWS ElastiCache, AWS S3

---

### 주요 기능

### 회원

- Spring Security를 사용한 JWT 로그인을 구현하였습니다.
- 회원 가입 시 메일을 발송하는 과정을 비동기 이벤트를 생성해 결합도를 줄이도록 구현했습니다.
    - 메일을 발송하는 로직때문에 회원 가입이 실패되서는 안되기 때문에 트랜잭션이 커밋된 이후, 리스너에서 이벤트를 처리 하도록 TransactionPhase.AFTER_COMMIT 을 사용했습니다.
- Redis를 활용한 로그아웃 기능을 구현했습니다.
    - 로그아웃 할 때 사용자의 AccessToken을 받아 남은 유효기간만큼 Redis에 저장하여 로그아웃 된 AccessToken의 재사용을 막았습니다.
- 간단한 사용을 위한 OAuth2 가입을 구현했습니다.

### 게시글

- 페이징, 검색 조건 설정이 필요한 게시글 목록 조회에는 QueryDSL을 사용하였습니다.
- 이미지 첨부가 가능합니다. 이미지는 S3에 저장됩니다.
    - 이미지는 게시글에서만 사용됩니다. 그렇기 때문에 @OneToMany의 cascade, orphanremoval 과 @OnDelete를 사용해 게시글이 저장될때 같이 저장되고, 게시글이 삭제될때 같이 삭제되도록 구현했습니다.

### 댓글

- Selft Join을 통한 무한 뎁스의 댓글 구조를 구현했습니다.

### 메시지

- 커서 기반 페이징의 무한 스크롤을 구현했습니다.

---

### JPA

- 모든 fetch 전략을 LAZY로 설정하여 해당 로직에 필요한 쿼리만 실행되도록 구현하였습니다.
- JPA의 지연로딩으로 N+1 문제가 발생하는 부분에 Fetch Join으로 처음부터 필요한 테이블을 함께 가져와서 성능을 최적화 하였습니다.
- 복잡한 쿼리는 JPQL을 직접 작성해 주었습니다.

### DevOps

- Docker, Jenkins를 사용한 배포 자동화 구축하였습니다.
- JWT에 사용되는 키와 DB 정보 등 민감한 정보를 SecretsManager에 분리하여 관리하였습니다.

### ETC

- API 서버를 구현하고 Swagger를 이용하여 API문서를 생성 하였습니다.
- BDD의 단위테스트를 약 200여개 작성하여 모든 기능을 테스트했습니다.
- 성공 메시지와 실패 메시지 모두 동일한 DTO에 담아 일관적인 구조로 반환하였습니다.
- @RestControllerAdvice를 사용하여 예외처리를 관리했습니다.

---

### ERD

<img width="485" alt="%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA_2023-11-12_%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE_4 52 59" src="https://github.com/hankyu0301/spring_board/assets/77604789/9c0bf176-1141-4028-9940-a40a029dcc76">

---

### 배포 프로세스

![image](https://github.com/hankyu0301/spring_board/assets/77604789/999c40fa-b445-470e-8fb2-9b1bcb58a568)

- 로컬에서 작업한 내용을 Jenkins와 연동된 Github 원격 repository에 push 합니다.
- Webhook을 이용해 새롭게 push된 내용을 기반으로 Jenkins 서버에서 Gradle을 통해 build를 실행합니다.
- Gradlew build를 통해 jar 파일이 자동 생성되고, 해당 jar 파일을 기반으로 도커 이미지가 자동으로 빌드됩니다.
- 생성된 도커 이미지는 DockerHub에 push 됩니다.
- Spring Boot 프로젝트를 배포할 EC2 서버에서 도커 허브에 올라간 도커 이미지를 pull 합니다.
- 내려받은 도커 이미지를 기반으로 컨테이너에 감싸 해당 프로젝트를 실행시켜 줍니다.
