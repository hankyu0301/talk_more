# TALK_MORE

---
## 사용자 간 소통가능한 게시판 서비스

**Github:  📄 [Github Link](https://github.com/hankyu0301/talk_more)**

**API 명세: 📄 [API 명세서](https://www.notion.so/d7225fe2a7304890adc083d624fb1854?pvs=21)**

**개발 인원 : 1명**

**개발 기간 : 2023.09.12 ~ 2023.10**

---

### Tech Stack

- Java
- Spring Boot, Spring Security
- Spring Data JPA, Spring Data Redis, QueryDSL, MySQL
- JWT, Docker, Jenkins, Swagger

---

### **Features**

### 회원 기능

- **Spring Security를 사용한 JWT 로그인/로그아웃을 구현했습니다.**
    - **로그아웃 시 사용자의 AccessToken을 받아 남은 유효기간만큼 Redis에 저장하여 로그아웃 된 사용자의 AccessToken 재사용을 방지 했습니다.**
- **회원 가입 시 메일을 발송하는 과정에서 비동기 이벤트를 생성해 결합도를 줄이도록 구현했습니다.**
    - **메일을 발송하는 로직때문에 회원 가입이 실패되서는 안되기 때문에 트랜잭션이 커밋된 이후 리스너에서 이벤트를 처리 하도록 TransactionPhase.AFTER_COMMIT 을 사용했습니다.**
    
    ```java
    @Component
    @RequiredArgsConstructor
    @Slf4j
    public class MemberCreateEventListener {
    
        private final EmailService emailService;
    
        @TransactionalEventListener
        @Async
        public void handleAlarm(MemberCreateEvent event) {
            String email = event.getCreatedMember().getEmail();
            emailService.sendEmail(email);
        }
    ```
    
- **간단한 사용을 위한 OAuth 로그인을 구현했습니다.**

### 게시글 기능

- 페이징, 검색 조건 설정이 필요한 게시글 목록 조회에는 QueryDSL을 사용하였습니다.
- 이미지는 게시글에서만 사용됩니다. 그렇기 때문에 @OneToMany의 cascade, orphanRemoval 과 @OnDelete를 사용해 게시글이 저장될때 같이 저장되고, 게시글이 삭제될때 같이 삭제되도록 구현했습니다.

### 메세지 기능

- **다른 사용자와 메시지를 주고 받을 수 있는 메시지 기능을 구현**

### 댓글 기능

- **Selft Join을 통한 무한 뎁스의 댓글 구조를 구현했습니다.**
- **댓글 삭제 로직은 다음과 같은 순서로 진행됩니다.**
    1. **삭제 메서드가 호출되면 삭제된 댓글임을 나타내는 deleted 필드를 true로 변경합니다.**
    2. **현재 댓글의 자식 댓글들이 모두 삭제되어야 (deleted = true) 현재 댓글이 실제로 DB에서 삭제 가능한 상태가 되므로 하위 댓글을 검사합니다.**
    3. **하위 댓글이 모두 삭제 가능한 댓글은 상위 댓글이 삭제되었는지 재귀적인 방법으로 확인하며 삭제 가능한 최상위 레벨의 댓글을 반환합니다.**
    4. **삭제 가능한 최상위 레벨의 댓글을 반환받고 그 댓글을 삭제합니다.**
    5. **하위 댓글은 cascade 설정으로 일괄 삭제됩니다.**
    
    **Comment Entity의 삭제 로직입니다.**
    

```java
    @Column(nullable = false)
    private boolean deleted;
    
    p//삭제된 댓글임을 마킹해둠
    public void markAsDeleted() {
        this.deleted = true;
    }

    /*댓글이 삭제가능한 상태인지 확인 후 결과에 따라 다른 값을 return*/
    public Optional<Comment> delete() {
        if(deleted) {
            return Optional.empty();
        }
        this.markAsDeleted();
        /*  현재 댓글의 하위댓글이 모두 삭제된 상태인가?*/
        if(isDeletableComment()) {
            return Optional.of(findDeletableAncestorByParent());
        } return Optional.empty();
    }

    /*  삭제조건을 만족하는 최상위 댓글 반환*/
    private Comment findDeletableAncestorByParent() {
        /*  부모 댓글이 존재하고 그 댓글이 삭제되었는지?*/
        if (isDeletableParent()) {
            /*  부모 댓글에 findDeletableCommentByParent()을 재귀 호출
             *  삭제조건을 만족하는 최상위댓글을 반환함 -> 그 댓글을 삭제하면 하위 댓글도 CASCADE 설정으로 일괄 삭제됨*/
            Comment parent = getParent().findDeletableAncestorByParent();
            if(parent.isDeletableCommentForParent()) return parent;
        }
        return this;
    }

    /*  부모 댓글이 존재하고 부모 댓글의 자식댓글들이 모두 삭제된 상태인지? */
    private boolean isDeletableParent() {
        return getParent() != null && getParent().isDeleted() && getParent().isDeletableCommentForParent();
    }

    /*  마지막 댓글까지 조회하여 현재 댓글이 삭제 가능한 댓글인지 판단*/
    private boolean isDeletableComment() {
        for (Comment child : getChildren()) {
            if (!child.isDeletableComment()) {
                return false;
            }
        }
        return isDeleted();
    }

    /*  자신의 자식 레벨만 검사하는 메서드*/
    private boolean isDeletableCommentForParent() {
        for (Comment child : getChildren()) {
            if (!child.isDeleted()) {
                return false;
            }
        }
        return isDeleted();
    }
```

### JPA

- **모든 fetch 전략을 LAZY로 설정하여 해당 로직에 필요한 쿼리만 실행되도록 구현하였습니다.**
- **JPA의 지연로딩으로 N+1 문제가 발생하는 부분에 Fetch Join으로 처음부터 필요한 테이블을 함께 가져와서 성능을 최적화 하였습니다.**

### **DevOps**

- **Jenkins와 Docker를 통합하여 파이프라인을 구성하여 자동 배포를 구현하면서 CI/CD에 대해 깊이 있는 학습을 진행하였습니다.**

### **ETC**

- **Swagger를 이용하여 API문서를 생성 하였습니다.**
- **BDD의 단위테스트를 작성하여 모든 기능을 테스트했습니다.**
- **성공 메시지와 실패 메시지 모두 동일한 DTO에 담아 일관적인 구조로 반환하였습니다.**
- **@RestControllerAdvice를 사용하여 예외처리를 관리했습니다.**
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
