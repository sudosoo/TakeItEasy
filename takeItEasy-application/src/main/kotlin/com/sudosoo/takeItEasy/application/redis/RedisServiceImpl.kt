package com.sudosoo.takeItEasy.application.redis

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.sudosoo.takeItEasy.application.dto.post.TestPostResponseDto
import com.sudosoo.takeItEasy.domain.repository.PostRepository
import org.springframework.data.domain.PageRequest
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.io.IOException

@Service
@Transactional
class RedisServiceImpl(
    val redisTemplate: RedisTemplate<String, String>,
    val postRepository: PostRepository
) : RedisService {

    private companion object {
        val objectMapper = ObjectMapper()
    }

    override fun <T> findByPagination(responseDtoName: String, pageable: PageRequest): MutableList<T> {
        val clazz = try {
            Class.forName(responseDtoName)
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
            return mutableListOf()
        }

        val start = pageable.pageNumber * pageable.pageSize
        val end = start + pageable.pageSize - 1

        val jsonValues = redisTemplate.opsForList().range(responseDtoName, start.toLong(), end.toLong())
        checkNotNull(jsonValues)
            return jsonValues.mapNotNull { jsonValue ->
                try {
                    objectMapper.readValue(jsonValue, clazz)?.let {
                        clazz.cast(it) as T
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                    null // Skip invalid entries
                }
            }.toMutableList()
    }

    override fun <T> getValues(methodName: String): MutableList<T> {
        return try {
            val fullClassName = "com.sudosoo.takeItEasy.application.dto.$methodName"
            val clazz = Class.forName(fullClassName) as Class<T>
            val jsonValues = redisTemplate.opsForList().range(methodName, 0, -1)

            checkNotNull(jsonValues)
            jsonValues.mapNotNull { jsonValue ->
                try {
                    objectMapper.readValue(jsonValue, clazz)
                } catch (e: Exception) {
                    e.printStackTrace()
                    null
                }
            }.toMutableList()
        } catch (e: Exception) {
            e.printStackTrace()
            mutableListOf() // Return empty list on errors
        }
    }

    override fun saveReadValue(value: Any) {
        val className = value.javaClass.simpleName
        val jsonObject = try {
            objectMapper.writeValueAsString(value)
        } catch (e: JsonProcessingException) {
            throw IllegalArgumentException("Invalid value")
        }
        redisTemplate.opsForList().leftPush(className, jsonObject)
    }

    override fun postRepositoryRedisSynchronization() {
        redisTemplate.delete("PostResponseDto")
        val posts = postRepository.findAll()

        posts.forEach { post ->
            val jsonPost: String = try {
                objectMapper.writeValueAsString(TestPostResponseDto(post))
            } catch (e: JsonProcessingException) {
                throw IllegalArgumentException("Invalid value")
            }
            redisTemplate.opsForList().leftPush("PostResponseDto", jsonPost)
        }
    }
}