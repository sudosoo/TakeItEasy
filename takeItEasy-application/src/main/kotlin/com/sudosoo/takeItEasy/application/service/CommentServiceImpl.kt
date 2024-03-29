package com.sudosoo.takeItEasy.application.service

import com.sudosoo.takeItEasy.application.common.service.JpaService
import com.sudosoo.takeItEasy.application.dto.comment.CreateCommentRequestDto
import com.sudosoo.takeItEasy.domain.entity.Category
import com.sudosoo.takeItEasy.domain.entity.Comment
import com.sudosoo.takeItEasy.domain.entity.Post
import com.sudosoo.takeItEasy.domain.repository.CommentRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class CommentServiceImpl(
    val commentRepository: CommentRepository,
    val postService: PostService
) : CommentService, JpaService<Comment, Long>{
    override var jpaRepository: JpaRepository<Comment, Long> = commentRepository

    override fun create(createCommentRequestDto: CreateCommentRequestDto): Comment {
        //TODO MemberSetting
        val memberId: Long = createCommentRequestDto.memberId
        val post: Post = postService.getByPostId(createCommentRequestDto.postId)
        val comment = Comment(createCommentRequestDto.content)
        comment.post = post
        comment.setMemberId(memberId)

        return save(comment)
    }

    override fun getByCommentId(commentId: Long): Comment {
        return findById(commentId)
    }

    override fun getCommentPaginationByPostId(postId: Long, pageRequest: Pageable): Page<Comment> {
        return commentRepository.findCommentsByPostId(postId, pageRequest)
    }
}
