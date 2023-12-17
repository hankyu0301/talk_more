package hankyu.board.spring_board.domain.post.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import hankyu.board.spring_board.domain.post.dto.PostReadCondition;
import hankyu.board.spring_board.domain.post.dto.PostSimpleDto;
import hankyu.board.spring_board.domain.post.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.querydsl.core.types.Projections.constructor;
import static hankyu.board.spring_board.domain.post.entity.QPost.post;


@Transactional(readOnly = true)
public class CustomPostRepositoryImpl extends QuerydslRepositorySupport implements CustomPostRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public CustomPostRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
        super(Post.class);
        this.jpaQueryFactory = jpaQueryFactory;
    }

    /*  QueryDSL을 이용하여 동적쿼리를 작성하는 메서드
    *   1.  createPredicate(cond)에서 where절에 사용할 조건을 작성함
    *   2.  getQuerydsl().applyPagination(pageable, createQuery(predicate))
    *   3.  createQuery(predicate)로 조회한 결과의 갯수를 카운트하여 반환*/
    @Override
    public Page<PostSimpleDto> findAllByCondition(PostReadCondition cond) {
        Pageable pageable = PageRequest.of(cond.getPage(), cond.getSize());
        Predicate predicate = createPredicate(cond);    //  1
        List<PostSimpleDto> postSimpleDtos = fetchAll(pageable, predicate); //  2
        Long totalCount = countTotal(predicate);  //  3
        return new PageImpl<>(postSimpleDtos, pageable, totalCount);
    }

    private Predicate createPredicate(PostReadCondition cond) {
        return new BooleanBuilder()
                .and(categoryExpression(cond))
                .and(memberIdExpression(cond))
                .and(titleExpression(cond));
    }

    private BooleanExpression titleExpression(PostReadCondition cond) {
        return (cond.getKeyword() == null) ? null : post.title.containsIgnoreCase(cond.getKeyword());
    }

    private BooleanExpression memberIdExpression(PostReadCondition cond) {
        return (cond.getMemberId() == null) ? null : post.member.id.eq(cond.getMemberId());
    }

    private BooleanExpression categoryExpression(PostReadCondition cond) {
        return cond.getCategoryId().stream().map(post.category.id::eq).reduce(BooleanExpression::or).orElse(null);
    }

    /*  스프링 데이터가 제공하는 페이징을 Querydsl로 편리하게 변환*/
    private List<PostSimpleDto> fetchAll(Pageable pageable, Predicate predicate) {
        return getQuerydsl().applyPagination(pageable, createQuery(predicate)).fetch();
    }

    private JPQLQuery<PostSimpleDto> createQuery(Predicate predicate) {
        return jpaQueryFactory
                .select(constructor(PostSimpleDto.class, post.id, post.title, post.member.nickname, post.createdAt))
                .from(post)
                .join(post.member)
                .where(predicate)
                .orderBy(post.id.desc());
    }

    private Long countTotal(Predicate predicate) {
        return createQuery(predicate).fetchCount();
    }


}
