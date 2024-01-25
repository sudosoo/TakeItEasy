package com.sudosoo.takeiteasy.entity;

import com.sudosoo.takeiteasy.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter
@EqualsAndHashCode(callSuper=false)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Heart extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    @JoinColumn(name = "comment_id")
    private Comment comment;

    @Enumerated(EnumType.STRING)
    private HeartType heartType;

    @Builder
    public Heart(Member member, Post post, Comment comment,HeartType heartType) {
        this.member = member;
        this.post = post;
        this.comment = comment;
        this.heartType = heartType;
    }

    public static Heart getPostHeart(Post post , Member member){
        return Heart.builder()
                .post(post)
                .member(member)
                .heartType(HeartType.POST)
                .build();
    }
    public static Heart getCommentHeart(Comment comment , Member member){
        return Heart.builder()
                .comment(comment)
                .member(member)
                .heartType(HeartType.COMMENT)
                .build();
    }

    public void setPost(Post post) {
        this.post = post;
        post.getHearts().add(this);
    }

    public void setComment(Comment comment) {
        this.comment = comment;
        comment.getHearts().add(this);
    }

    public void unHeartPost() {
        if (post != null) {
            post.getHearts().remove(this);
            post = null;
        }
    }

    public void unHeartComment() {
        if (comment != null) {
            comment.getHearts().remove(this);
            comment = null;
        }
    }
    public String getMemberName(){
        return this.member.getMemberName();
    }

}
