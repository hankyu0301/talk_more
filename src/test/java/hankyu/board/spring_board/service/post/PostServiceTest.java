package hankyu.board.spring_board.service.post;

import hankyu.board.spring_board.domain.category.repository.CategoryRepository;
import hankyu.board.spring_board.domain.member.repository.MemberRepository;
import hankyu.board.spring_board.domain.post.dto.PostCreateRequest;
import hankyu.board.spring_board.domain.post.dto.PostDto;
import hankyu.board.spring_board.domain.post.dto.PostListDto;
import hankyu.board.spring_board.domain.post.dto.PostUpdateRequest;
import hankyu.board.spring_board.domain.post.entity.Image;
import hankyu.board.spring_board.domain.post.entity.Post;
import hankyu.board.spring_board.domain.post.repository.PostRepository;
import hankyu.board.spring_board.domain.post.service.PostService;
import hankyu.board.spring_board.global.auth.utils.AuthUtils;
import hankyu.board.spring_board.global.exception.category.CategoryNotFoundException;
import hankyu.board.spring_board.global.exception.member.MemberNotFoundException;
import hankyu.board.spring_board.global.exception.post.PostNotFoundException;
import hankyu.board.spring_board.global.file.FileService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static hankyu.board.spring_board.factory.dto.post.PostCreateRequestFactory.createPostCreateRequest;
import static hankyu.board.spring_board.factory.dto.post.PostReadConditionFactory.createPostReadCondition;
import static hankyu.board.spring_board.factory.dto.post.PostUpdateRequestFactory.createPostUpdateRequest;
import static hankyu.board.spring_board.factory.entity.category.CategoryFactory.createCategory;
import static hankyu.board.spring_board.factory.entity.member.MemberFactory.createMember;
import static hankyu.board.spring_board.factory.entity.post.PostFactory.createPost;
import static hankyu.board.spring_board.factory.entity.post.PostFactory.createPostWithImages;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@Transactional
class PostServiceTest {

    @InjectMocks
    PostService postService;
    @Mock
    PostRepository postRepository;
    @Mock
    MemberRepository memberRepository;
    @Mock
    CategoryRepository categoryRepository;
    @Mock
    FileService fileService;
    @Mock
    AuthUtils authUtils;

    @Test
    void create_Success() {
        //given
        PostCreateRequest req = createPostCreateRequest();
        given(authUtils.getMemberId()).willReturn(1L);
        given(memberRepository.findById(anyLong())).willReturn(Optional.of(createMember()));
        given(categoryRepository.findById(anyLong())).willReturn(Optional.of(createCategory()));
        given(postRepository.save(any())).willReturn(createPost());

        //when
        postService.create(req);

        //then
        verify(postRepository).save(any());
    }

    @Test
    void create_memberNotFound_ThrowsException() {
        //given
        given(authUtils.getMemberId()).willReturn(1L);
        given(memberRepository.findById(anyLong())).willReturn(Optional.empty());

        //when,then
        assertThatThrownBy( () -> postService.create(createPostCreateRequest())).isInstanceOf(MemberNotFoundException.class);
    }

    @Test
    void create_categoryNotFound_ThrowsException() {
        //given
        given(authUtils.getMemberId()).willReturn(1L);
        given(memberRepository.findById(anyLong())).willReturn(Optional.of(createMember()));
        given(categoryRepository.findById(anyLong())).willReturn(Optional.empty());

        //when,then
        assertThatThrownBy( () -> postService.create(createPostCreateRequest())).isInstanceOf(CategoryNotFoundException.class);
    }

    @Test
    void read_Success() {
        // given
        Post post = createPostWithImages();
        given(postRepository.findById(1L)).willReturn(Optional.of(post));


        // when
        PostDto postDto = postService.read(1L);

        // then
        assertThat(postDto.getTitle()).isEqualTo(post.getTitle());
        assertThat(postDto.getImages().size()).isEqualTo(post.getImages().size());
    }

    @Test
    void read_postNotFound_ThrowsException() {
        //given
        given(postRepository.findById(anyLong())).willReturn(Optional.empty());

        //when,then
        assertThatThrownBy(() -> postService.read(1L)).isInstanceOf(PostNotFoundException.class);
    }

    @Test
    void update_Success() {
        // given
        Post post = createPost();
        given(postRepository.findById(anyLong())).willReturn(Optional.of(post));
        MockMultipartFile dFile = new MockMultipartFile("d", "d.png", MediaType.IMAGE_PNG_VALUE, "d".getBytes());
        PostUpdateRequest postUpdateRequest = createPostUpdateRequest("title", "content", List.of(dFile),List.of(0L));

        // when
        postService.update(1L, postUpdateRequest);

        // then
        List<Image> images = post.getImages();
        List<String> originNames = images.stream().map(Image::getOriginName).collect(toList());
        assertThat(originNames.size()).isEqualTo(1);
    }

    @Test
    void update_postNotFound_ThrowsException() {

        given(postRepository.findById(anyLong())).willReturn(Optional.empty());

        assertThatThrownBy( () -> postService.update(
                1L,
                createPostUpdateRequest("title","content",List.of(),List.of())))
                .isInstanceOf(PostNotFoundException.class);
    }

    @Test
    void delete_Success() {
        // given
        Post post = createPostWithImages();
        given(postRepository.findById(anyLong())).willReturn(Optional.of(post));

        // when
        postService.delete(1L);

        // then
        verify(postRepository).delete(any());
    }

    @Test
    void delete_postNotFound_ThrowsException() {
        // given
        given(postRepository.findById(anyLong())).willReturn(Optional.empty());

        // when, then
        assertThatThrownBy(() -> postService.delete(1L)).isInstanceOf(PostNotFoundException.class);
    }

    @Test
    void findAll_Success() {
        // given
        given(postRepository.findAllByCondition(any())).willReturn(Page.empty());

        // when
        PostListDto postListDto = postService.readAll(createPostReadCondition(1, 1));

        // then
        assertThat(postListDto.getPostList().size()).isZero();
    }
}
