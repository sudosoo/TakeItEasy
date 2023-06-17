package com.sudosoo.takeiteasy.service;

import com.sudosoo.takeiteasy.dto.HeartRequestDto;
import com.sudosoo.takeiteasy.entity.Comment;
import com.sudosoo.takeiteasy.entity.Heart;
import com.sudosoo.takeiteasy.entity.Member;
import com.sudosoo.takeiteasy.entity.Post;
import com.sudosoo.takeiteasy.repository.HeartRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class HeartServiceImpl implements HeartService {

    private final HeartRepository heartRepository;
    private final MemberService memberService;
    private final PostService postService;
    private final CommentService commentService;

    public void postHeart(HeartRequestDto heartRequestDTO){
        Member member = memberService.getMemberByMemberId(heartRequestDTO.getMemberId());
        Post post = postService.getPostByPostId(heartRequestDTO.getPostId());

        // 이미 좋아요되어있으면 에러 반환
        existLike(heartRepository.findByMemberAndPost(member, post));
        Heart heart = Heart.postLike(post,member);
        heart.setPost(post);

        heartRepository.save(heart);
        log.info("New PostHeart created : memberId {} , PostHeartId {}"+ member.getId(),heart.getId());
    }
    public void commentHeart(HeartRequestDto heartRequestDTO)  {
        Member member = memberService.getMemberByMemberId(heartRequestDTO.getMemberId());
        Comment comment  = commentService.getCommentByCommentId(heartRequestDTO.getCommentId());

        // 이미 좋아요되어있으면 에러 반환
        existLike(heartRepository.findByMemberAndComment(member, comment));
        Heart heart = Heart.commentLike(comment,member);
        heart.setComment(comment);
        
        heartRepository.save(heart);
        log.info("New CommentHeart created : memberName {} , CommentHeartId {}"+ member.getUserName(),heart.getId());

    }
    public void postDisHeart(HeartRequestDto heartRequestDTO) {
        Member member = memberService.getMemberByMemberId(heartRequestDTO.getMemberId());
        Post post = postService.getPostByPostId(heartRequestDTO.getPostId());
        Heart heart = findHeartByMemberAndPostOrComment(member,post);
        heart.unHeartPost();

        heartRepository.delete(heart);
        log.info("Deleted PostHeart : memberName {} , PostHeartId {}"+ member.getUserName(),heart.getId());
    }

    public void commentDisHeart(HeartRequestDto heartRequestDTO) {
        Member member = memberService.getMemberByMemberId(heartRequestDTO.getMemberId());
        Comment comment  = commentService.getCommentByCommentId(heartRequestDTO.getCommentId());
        Heart heart = findHeartByMemberAndPostOrComment(member, comment);

        heart.unHeartComment();

        heartRepository.delete(heart);
        log.info("Deleted CommentHeart : memberName {} , CommentHeartId {}"+ member.getUserName(),heart.getId());
    }

    private Heart findHeartByMemberAndPostOrComment(Member member, Object reference) {
        if (reference instanceof Post) {
            return heartRepository.findByMemberAndPost(member, (Post) reference)
                    .orElseThrow(() -> new IllegalArgumentException("Could not find heart id"));
        } else if (reference instanceof Comment) {
            return heartRepository.findByMemberAndComment(member, (Comment) reference)
                    .orElseThrow(() -> new IllegalArgumentException("Could not find heart id"));
        } else {
            throw new IllegalArgumentException("Unsupported reference type");
        }
    }

    private void existLike(Optional<Heart> heartRepository) {
        if (heartRepository.isPresent()) {
            throw new IllegalArgumentException("Duplicated Like !");
        }
    }

   

}
