package com.sudosoo.takeItEasy.presentation.controller

import com.sudosoo.takeItEasy.application.service.NoticeService
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter

@RestController
@RequestMapping("/api/notice")
class NoticeController(val noticeService: NoticeService) {

    @GetMapping("/subscribe", produces = ["text/event-stream"])
    fun subscribe(
        @RequestParam memberName : String,
        @RequestHeader(value = "Last-Event-ID", required = false, defaultValue = "") lastEventId : String
    ): SseEmitter {
        return noticeService.subscribe(memberName, lastEventId)
    }
}