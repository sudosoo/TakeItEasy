package com.sudosoo.takeItEasy.batch.step

import org.springframework.batch.core.Step
import org.springframework.batch.item.ItemWriter
import org.springframework.batch.item.database.JpaPagingItemReader
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder
import org.springframework.beans.factory.annotation.Value

//TODO 상태패턴 적용 하기
interface StepService <T>{
    fun step(): Step
    fun reader (@Value("#{jobParameters[date]}") date :String?): JpaPagingItemReader<T>
    fun bulkWriter () : ItemWriter<T>

}