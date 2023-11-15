package com.sudosoo.takeiteasy.entity;

import com.sudosoo.takeiteasy.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @OneToMany(mappedBy = "comment")
    private List<Heart> hearts = new ArrayList<>();

    @Builder
    private Comment(String content, Member member, Post post, List<Heart> hearts) {
        this.content = content;
        this.member = member;
        this.post = post;
        this.hearts = hearts;
    }
    public static Comment buildEntityFromDto(Member member,String content){
        return Comment.builder()
                .member(member)
                .content(content)
                .build();
    }

    public void setPost(Post post) {
        this.post = post;
        post.getComments().add(this);
    }

    public String getUserName() {
        if(this.member == null){
            throw new IllegalArgumentException("사용자가 등록되어 있지 않습니다.");
        }
        return this.member.getMemberName();
    }
}
