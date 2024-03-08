package com.sudosoo.takeiteasy.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sudosoo.takeiteasy.dto.KafkaResponseDto;
import com.sudosoo.takeiteasy.dto.comment.CommentResponseDto;
import com.sudosoo.takeiteasy.dto.kafkaMemberValidateRequestDto;
import com.sudosoo.takeiteasy.dto.post.*;
import com.sudosoo.takeiteasy.entity.Category;
import com.sudosoo.takeiteasy.entity.Comment;
import com.sudosoo.takeiteasy.entity.Post;
import com.sudosoo.takeiteasy.kafka.KafkaProducer;
import com.sudosoo.takeiteasy.redis.RedisService;
import com.sudosoo.takeiteasy.repository.CommentRepository;
import com.sudosoo.takeiteasy.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
@Transactional
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final CategoryService categoryService;
    private final CommentRepository commentRepository;
    private final KafkaProducer kafkaProducer;
    private final ObjectMapper objectMapper;
    private final RedisService redisService;

    @Override
    public PostResponseDto create(CreatePostRequestDto requestDto) {
        KafkaResponseDto kafkaResponseDto = null;
        try{
            String kafkaResult = kafkaProducer.replyRecord(new kafkaMemberValidateRequestDto(requestDto.getMemberId()));
            kafkaResponseDto = objectMapper.readValue(kafkaResult, KafkaResponseDto.class);

        }catch (ExecutionException | InterruptedException | IOException e ){
            e.getStackTrace();
        }
        if (kafkaResponseDto == null){
            throw new IllegalArgumentException("kafka reply error");
        }

        Post post = Post.of(requestDto);
        Category category = categoryService.getById(requestDto.getCategoryId());
        post.setMemberIdAndWriter(kafkaResponseDto.getMemberId(),kafkaResponseDto.getMemberName());
        post.setCategory(category);

        Post result = postRepository.save(post);

        //redis ReadRepository 데이터 삽입
        PostResponseDto responseDto = result.toResponseDto();
        redisService.saveReadValue(result.toResponseDto());
        return responseDto;
    }


    @Override
    public PostResponseDto redisTest(PostRequestDto requestDto){
        Post post = Post.testOf(requestDto);
        Category category = categoryService.getById(requestDto.getCategoryId());
        post.setCategory(category);
        Post result = postRepository.save(post);
        return result.toResponseDto();
    }


    @Override
    public Post getByPostId(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Could not found post id : " + postId));
    }


    @Override
    @Transactional(readOnly = true)
    public PostDetailResponseDto getPostDetailByPostId(Long postId, Pageable pageRequest) {
        Post post = postRepository.findById(postId).orElseThrow(()->new IllegalArgumentException("해당 게시물이 존재 하지 않습니다."));
        post.incrementViewCount();

        Page<Comment> comments = commentRepository.findCommentsByPostId(postId,pageRequest);
        //TODO MemberSetting 각 코멘트의 유저 이름을 찾아와서 넣어주기
        List<CommentResponseDto> responseCommentDtos = comments.stream().map(Comment::toResponseDto).toList();
        return post.toDetailDto(new PageImpl<>(responseCommentDtos));
    }


    @Override
    @Transactional(readOnly = true)
    public List<PostResponseDto> getAllPost() {
        List<Post> posts = postRepository.findAll();
        return posts.stream().map(Post::toResponseDto).toList();
    }


    @Override
    public List<PostTitleOnlyResponseDto> getPaginationPost(Pageable pageable) {
        return postRepository.findAll(pageable).map(Post::toTitleOnlyDto).toList();
    }


    @Override
    public Post createBatchPosts(int count){
        return Post.builder()
                .title("title"+count)
                .content("content"+count)
                .memberId((long) count)
                .writerName("member"+count)
                .build();
    }

}