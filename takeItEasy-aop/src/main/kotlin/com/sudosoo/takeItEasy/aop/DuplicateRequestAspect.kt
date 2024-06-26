package com.sudosoo.takeItEasy.aop

import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Pointcut
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import java.util.*
import java.util.concurrent.ConcurrentHashMap

@Aspect
@Component
class DuplicateRequestAspect {
    private val requestSet: MutableSet<String> = Collections.newSetFromMap(ConcurrentHashMap())

    @Pointcut("within(*..*Controller)")
    fun onRequest() {}

    @Around("onRequest()")
    @Throws(Throwable::class)
    fun duplicateRequestCheck(joinPoint: ProceedingJoinPoint): Any? {
        val request = (RequestContextHolder.currentRequestAttributes() as ServletRequestAttributes).request
        val httpMethod = request.method

        // GET 메소드인 경우 중복 체크를 하지 않음
        if ("GET".equals(httpMethod, ignoreCase = true)) {
            return joinPoint.proceed()
        }

        val requestId = joinPoint.signature.toLongString()
        if (requestSet.contains(requestId)) {
            // 중복 요청인 경우
            return handleDuplicateRequest()
        }
        requestSet.add(requestId)
        return try {
            joinPoint.proceed()
        } finally {
            requestSet.remove(requestId)
        }
    }

    private fun handleDuplicateRequest(): ResponseEntity<String> {
        // 중복 요청에 대한 응답 처리
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body("중복된 요청 입니다")
    }
}

