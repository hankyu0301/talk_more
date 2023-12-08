# 커뮤니티 게시판 제작

---

게시글과 댓글을 작성하고 회원간 메세지를 통해 소통가능한 게시판을 제작했습니다 !

- 프로젝트 명칭 : 커뮤니티 게시판
- 개발 인원 : 1명
- 개발 기간 : 2023.09.12~2023.10.19

🖥 [Swagger API Link](http://54.180.15.95:8080/swagger-ui/index.html#/) (프로젝트의 API 명세서 입니다.)

---

### 사용 Skills

- Java, SpringBoot, Gradle
- MariaDB, Redis
- Junit5, Mockito
- Github, Jenkins, Docker
- AWS EC2, AWS RDS, AWS ElastiCache, AWS S3

---

### JAVA, Spring

- 코드 중복과 수정가능성은 줄이고, 확장용이성과 가독성은 높이는 코드를 작성하는 방법을 배워나갈 수 있었습니다.
- 가독성 향상을 위해 스트림, 람다 등 함수형 프로그래밍을 적극 활용하였습니다.

### JWT

- Spring Security를 사용하는 JWT 로그인 방식을 구현하였습니다.
- Refresh Token Storage로 Redis를 사용하여 만료된 토큰의 자동 삭제되도록 하였습니다.
- 로그아웃한 회원의 Access Token를 블랙리스트 처리하여 재사용을 방지하였습니다.

### JPA

- 모든 fetch 전략을 LAZY로 설정하여 해당 로직에 필요한 쿼리만 실행되도록 구현하였습니다.
- N+1 문제가 발생하는 부분을 찾아 리팩토링 하는 과정을 거쳐 쿼리 실행을 최소화 하였습니다.
- 복잡한 쿼리는 JPQL을 직접 작성해 주었습니다.
- 페이징, 검색 조건 설정이 필요한 게시글 목록 조회에는 QueryDSL을 사용하였습니다.
- 댓글은 SelfJoin으로 부모, 자식 댓글을 참조해 계층 구조를 형성했습니다.

### DevOps

- Docker, Jenkins를 사용한 배포 자동화 구축하였습니다.
- JWT에 사용되는 키와 DB 정보 등 민감한 정보를 SecretsManager에 분리하여 관리

### ETC

- API 서버를 구현하고 Swagger를 이용하여 API문서를 생성 하였습니다.
- BDD의 단위테스트를 작성하여 모든 기능을 테스트했습니다.
- 회원 가입시 이메일을 발송하는 비동기 이벤트를 발행 해주었습니다.
- 성공 메시지와 실패 메시지 모두 동일한 DTO에 담아 일관적인 구조로 반환하였습니다.
- @RestControllerAdvice를 사용하여 예외처리를 관리했습니다.

---

### ERD

---<img width="485" alt="%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA_2023-11-12_%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE_4 52 59" src="https://github.com/hankyu0301/spring_board/assets/77604789/9c0bf176-1141-4028-9940-a40a029dcc76">

### 배포 프로세스

![image](https://github.com/hankyu0301/spring_board/assets/77604789/999c40fa-b445-470e-8fb2-9b1bcb58a568)

- 로컬에서 작업한 내용을 Jenkins와 연동된 Github 원격 repository에 push 합니다.
- Webhook을 이용해 새롭게 push된 내용을 기반으로 Jenkins 서버에서 Gradle을 통해 build를 실행합니다.
- Gradlew build를 통해 jar 파일이 자동 생성되고, 해당 jar 파일을 기반으로 도커 이미지가 자동으로 빌드됩니다.
- 생성된 도커 이미지는 DockerHub에 push 됩니다.
- Spring Boot 프로젝트를 배포할 EC2 서버에서 도커 허브에 올라간 도커 이미지를 pull 합니다.
- 내려받은 도커 이미지를 기반으로 컨테이너에 감싸 해당 프로젝트를 실행시켜 줍니다.
