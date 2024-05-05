package com.sudosoo.takeItEasy.presentation.controller

import com.sudosoo.takeItEasy.application.dto.post.PostRequestDto
import com.sudosoo.takeItEasy.application.dto.post.TestPostResponseDto
import com.sudosoo.takeItEasy.application.redis.RedisService
import com.sudosoo.takeItEasy.application.service.EsPostService
import com.sudosoo.takeItEasy.application.service.PostService
import com.sudosoo.takeItEasy.batch.schedule.Scheduler
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/test")
class TestController (
    val scheduler: Scheduler,
    val redisService: RedisService,
    val postService: PostService,
//    val esPostService: EsPostService
)
{
    @PostMapping("/batchCreateDummyPost", name = "batchCreateDummyPost")
    fun batchCreateDummyPost(): ResponseEntity<Void> {
        //batch 실행
        scheduler.testRun()
        return ResponseEntity.ok().build()
    }

    @GetMapping("/getV", name = "getV")
    fun redisGetTest(): List<TestPostResponseDto> {
        val methodName = "PostResponseDto"
        return redisService.getValues(methodName)
    }

    @PostMapping("/createPost", name = "createPost")
    fun createPost(@RequestBody requestDto: PostRequestDto): TestPostResponseDto {
        return postService.redisTest(requestDto)
    }

    @PostMapping("/synchronize", name = "repositoryRedisSynchronization")
    fun repositoryRedisSynchronization() {
        redisService.postRepositoryRedisSynchronization()
    }

//    @PostMapping("/export", name = "exportPostsToElasticsearch")
//    fun exportPostsToElasticsearch() {
//        esPostService.exportPostsToElasticsearch()
//    }

}