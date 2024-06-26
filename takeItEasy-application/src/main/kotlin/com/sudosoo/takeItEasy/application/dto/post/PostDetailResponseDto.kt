package com.sudosoo.takeItEasy.application.dto.post

import com.sudosoo.takeItEasy.application.dto.comment.CommentResponseDto
import com.sudosoo.takeItEasy.domain.entity.Post

class PostDetailResponseDto(
    val postId: Long, val writerName: String,
    val title: String, val content: String, val comments: MutableList<CommentResponseDto>
){

    constructor(post : Post, comments: MutableList<CommentResponseDto>): this (
            postId = post.id,
            writerName = post.writer,
            title = post.title,
            content = post.content ,
            comments = comments
        )

}