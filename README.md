### 핵심 기능 상세

### 1. 회원 관련 기능

### 1-1)회원가입

- Request의 Email과 Nickname의 중복을 검사합니다.
- 비밀번호를 암호화하고 새로운 회원을 생성합니다.
    - 새로 생성된 회원은 현재 비활성화 상태입니다.
- 새로운 회원이 생성되면 비동기 이벤트를 발행하여 인증 URL이 담긴 메일을 Request에 입력했던 Email로 발송합니다.
- Email에 첨부된 인증코드는 Redis에 유효시간동안 저장됩니다.
- Email에 첨부된 URL로 접속하면 계정이 활성화됩니다.

```java
@Transactional
public void signUp(SignUpRequest request) {
	validateSignUpRequest(request);
	Member member = createMemberFromRequest(request);
	//아직 비활성화 상태 (email 인증 필요)
	memberRepository.save(member);
	//EventListener가 이벤트 생성을 감지하면 이메일 발송한 뒤 redis에 저장
	member.publishCreatedEvent(publisher);
}
```

### 1-2)로그인

- Request의 Email과 일치하는 회원이 존재하는지 확인합니다.
- Email로 찾은 회원의 비밀번호와 Request의 Password가 일치하는지 확인합니다.
- Request의 Email과 Password로 새로운 UsernamePasswordAuthenticationToken을 생성합니다.
- 생성된 Token을 ProviderManager의 authenticate()메서드의 파라미터로 전달합니다.
- authenticate() 메서드가 성공적으로 끝마치고 Authentication 객체를 반환합니다.
- 반환된 Authentication 객체를 사용하여 AccessToken과 RefreshToken을 생성합니다.
- RefreshToken은 Redis에 유효시간동안 저장됩니다. 추후 AccessToken을 재발급할 때 사용됩니다.

```java
@Transactional
public TokenResponse signIn(SignInRequest req) {
	Member member = memberRepository.findByEmail(req.getEmail()).orElseThrow(LoginFailureException::new);
	validatePassword(req, member);
	Authentication authentication = getUserAuthentication(req);
	TokenResponse res = tokenProvider.generateToken(authentication);
	redisService.setDataWithExpiration(RedisKey.REFRESH_TOKEN, res.getRefreshToken(), req.getEmail(), REFRESH_TOKEN_EXPIRE_TIME);
	return res;
}
```

### 1-3)토큰 재발급

- Request의 RefreshToken의 유효성을 검증합니다.
- Request의 AccessToken에서 Authentication 객체를 추출합니다.
- Authentication 객체정보를 이용해 DB를 조회하여 일치하는 member를 찾습니다.
- 조회한 Member의 Email 정보로 Redis를 조회하여 RefreshToken을 가져옵니다.
- Redis에서 조회한 RefreshToken과 회원이 제출한 RefreshToken을 비교하여 검증합니다.
- 검증이 완료되었다면 새로운 AccessToken을 발급받습니다.

```java
@Transactional
public String reissue(TokenReissueRequest req) {
	validateTokenReissueRequest(req.getRefreshToken());
  Authentication authentication = tokenProvider.getAuthentication(req.getAccessToken());
  Member member = memberRepository.findById(Long.valueOf(authentication.getName())).orElseThrow(MemberNotFoundException::new);
  String refreshToken = redisService.getData(RedisKey.REFRESH_TOKEN, member.getEmail());
  validateRefreshTokenForReissue(refreshToken, req.getRefreshToken());
  return tokenProvider.generateAccessToken(authentication);
}
```

### 1-4)로그아웃

- Request의 RefreshToken을 검증합니다.
- Redis에 저장된 RefreshToken을 삭제합니다.
- Request의 AccessToken을 남은 유효시간 동안 Redis에 저장하여 접근을 차단합니다.
- 접근의 차단은 Filter에서 진행됩니다.

```java
@Transactional
	public void logout(LogoutRequest req) {
	  validateRefreshToken(req.getRefreshToken());
    //  refreshToken 삭제하여 accessToken을 재발급하지 못하게 함.
	  redisService.deleteData(RedisKey.REFRESH_TOKEN, req.getRefreshToken());

    //  이전에 발급받은 accessToken을 사용하지 못하도록 해야함.
    //  req.getAccessToken()으로 남은 유효시간을 읽어와서 유효시간동안 redis에 등록.
    //  redis에 등록된 accessToken으로 로그인이 불가(jwtAuthenticationFilter에서 확인)
    expireAccessToken(req.getAccessToken());
}
```

### 1-5)미인증 회원 삭제

- 이메일 인증을 진행하지 않은 상태로 24시간 이상 경과된 계정들을 삭제합니다.
- @Scheduled(cron = “0 0 0 * * ?”) 애노테이션을 사용하여 매일 정해진 시간에 실행됩니다.

### 2.게시글 관련 기능

### 2-1)게시글 목록 조회

- QueryDSL을 이용하여 동적쿼리를 작성하여 검색할 수 있습니다.
- 카테고리 Id, 회원 Id, 게시물 제목, page, size를 지정할 수 있습니다.

```java
/*  QueryDSL을 이용하여 동적쿼리를 작성하는 메서드
 *   1.  createPredicate(cond)에서 where절에 사용할 조건을 작성함
 *   2.  fetchAll(pageable, predicate) 에서 List<PostSimpleDto>를 반환
 *   3.  createQuery(predicate)로 조회한 결과의 갯수를 카운트하여 반환*/
@Override
public Page<PostSimpleDto> findAllByCondition(PostReadCondition cond) {
	Pageable pageable = PageRequest.of(cond.getPage(), cond.getSize());
	Predicate predicate = createPredicate(cond);    //  1
	List<PostSimpleDto> postSimpleDtos = fetchAll(pageable, predicate); //  2
	Long totalCount = countTotal(predicate);  //  3
	return new PageImpl<>(postSimpleDtos, pageable, totalCount);
}
```

### 3.댓글 관련 기능

### 3-1)댓글 조회

- 댓글은 @ManyToOne으로 부모 댓글을 참조하고 있는 계층형 구조입니다.
    
    ```java
    @Query("select c from Comment c join fetch c.member left join fetch c.parent where c.post.id = :postId order by c.parent.id asc nulls first, c.id asc")
    List<Comment> findAllWithMemberAndParentByPostIdOrderByParentIdAscNullsFirstCommentIdAsc(Long postId);
    ```
    
- 해당 게시글과 연관 관계를 맺은 댓글 전부를 가져옵니다.

```java
private List<CommentDto> convertCommentListToDtoList(List<Comment> comments) {
        //  모든 comment를 <id, dto> 쌍으로 map에 담는다.
        Map<Long, CommentDto> commentMap = new HashMap<>();
        //  parentId가 null인 최상위 댓글만을 담을 List
        List<CommentDto> roots = new ArrayList<>();
        for (Comment comment : comments) {
            Long id = comment.getId();
            CommentDto dto = toDto(comment);
            commentMap.put(id, dto);
            Comment parent = comment.getParent();
            if(parent == null) {
                roots.add(dto);
            } else {
                try {
                    CommentDto parentDto = commentMap.get(parent.getId());
                    parentDto.getChildren().add(dto);
                } catch (NullPointerException e) {
                    throw new CannotConvertNestedStructureException(e.getMessage());
                }
            }
        }
        return roots;
    }
```

- 변환 메서드를 사용하여 계층구조를 변환합니다.

### 3-2)댓글 삭제

- 댓글은 하위댓글이 모두 삭제된 상태일때 삭제가 가능합니다.
- 삭제 요청을 전달받으면 현재 댓글이 삭제 가능한 상태인지 확인합니다.
- 삭제 가능한 상태라면 상위 댓글을 호출하여 삭제 가능한 상태인지 확인합니다.
- 재귀 호출을 통해 삭제 가능한 최상위 댓글을 찾아낸 후 해당 댓글을 반환합니다.

```java
 	//삭제된 댓글임을 마킹해둠
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
        return getParent() != null && getParent().isDeleted();
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
        return true;
    }
```

### 4.메세지 관련 기능

### 4-1)메세지 삭제

- 메세지의 삭제는 메세지의 상태를 ‘삭제 되었음’ 으로 변경합니다.
    - ‘삭제 되었음’ 상태의 메세지는 조회가 불가능합니다.
- 수신자와 발신자 모두 메세지를 삭제했다면 실제 DB에서 삭제처리를 합니다.
- 반복문을 사용해 메세지를 한건씩 확인합니다.
- 내가 보낸/받은 메세지가 맞는지 확인한 후에 삭제되었음 처리를 합니다.
- 그 후에 삭제 가능한 상태인지 확인하고 (수신자와 발신자 모두 삭제 처리하였음) 삭제 가능한 메세지라면 List에 추가합니다.

```java
/*  보낸 Message 삭제*/
@Transactional
public void deleteBySender(MessageDeleteRequest req) {
	// 제거할 메세지를 In 으로 한번에 조회 -> 조회한 메세지가 내가 보낸 메세지가 맞는지 확인
	List<Message> deletedMessages = messageRepository.findByIdIn(req.getDeletedMessageIds());
	messageRepository.deleteAll(deleteBySender(deletedMessages, Message::deleteBySender));
	}

/*  받은 Message 삭제*/
@Transactional
public void deleteByReceiver(MessageDeleteRequest req) {
	List<Message> deletedMessages = messageRepository.findByIdIn(req.getDeletedMessageIds());
	messageRepository.deleteAll(deleteByReceiver(deletedMessages, Message::deleteByReceiver));
	}

/*  1.  삭제하고자 하는 메세지를 한건씩 조회합니다.
    *   2.  삭제 권한 있는지 확인합니다.
    *   3.  메세지의 상태를 '삭제 되었음' 으로 변경합니다.
    *   4.  메세지를 실제로 삭제해도 되는지 (수신자, 발신자 모두 '삭제 되었음' 상태로 변경했는지) 확인합니다.
    *   5.  실제로 삭제해도 되는 메세지는 List에 담아 반환합니다.*/
private List<Message> deleteBySender(List<Message> deletedMessages, Consumer<Message> deleteFunction) {
	List<Message> result = new ArrayList<>();
  for (Message message : deletedMessages) { //    1
		authChecker.authorityCheck(message.getSender().getId());    //  2
		deleteFunction.accept(message); //  3
		if (message.isDeletable()) {    //  4
				result.add(message);    //  5
			}
		}
	return result;
	}

private List<Message> deleteByReceiver(List<Message> deletedMessages, Consumer<Message> deleteFunction) {
	List<Message> result = new ArrayList<>();
		for (Message message : deletedMessages) {
			authChecker.authorityCheck(message.getReceiver().getId());
			deleteFunction.accept(message);
			if (message.isDeletable()) {
				result.add(message);
			}
		}
		return result;
	}
```
