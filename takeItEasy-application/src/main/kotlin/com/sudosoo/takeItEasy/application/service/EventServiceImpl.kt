package com.sudosoo.takeItEasy.application.service

import com.sudosoo.takeItEasy.application.common.service.JpaService
import com.sudosoo.takeItEasy.application.dto.coupon.CouponIssuanceRequestDto
import com.sudosoo.takeItEasy.application.dto.event.CreateEventRequestDto
import com.sudosoo.takeItEasy.application.dto.event.EventResponseDto
import com.sudosoo.takeItEasy.domain.entity.Coupon
import com.sudosoo.takeItEasy.domain.entity.Event
import com.sudosoo.takeItEasy.domain.repository.EventRepository
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.function.Supplier

@Transactional
@Service
class EventServiceImpl (
    val eventRepository: EventRepository,
    val couponService: CouponService
) :EventService , JpaService<Event, Long>{
    override var jpaRepository: JpaRepository<Event, Long> = eventRepository

    override fun create(requestDto: CreateEventRequestDto): EventResponseDto {
        validateDiscountFields(requestDto)

        val coupon = if (requestDto.discountRate != null) {
            //할인율 적용 쿠폰일때
            couponService.rateCouponCreate(requestDto)
        } else {
            //할인가격 적용 쿠폰 일때
            couponService.priceCouponCreate(requestDto)
        }

        var event = Event.of(requestDto.eventName,requestDto.couponQuantity, requestDto.eventDeadline, coupon)
        event = save(event)
        return EventResponseDto(event.id, coupon.id)
    }

    @Transactional(timeout = 5)
    override fun couponIssuance(requestDto: CouponIssuanceRequestDto) {
        val event: Event = eventRepository.findByEventIdForUpdate(requestDto.eventId)
            .orElseThrow { IllegalArgumentException("Event is not found") }
        //TODO MemberSetting
        val memberId: Long = requestDto.memberId
        /* 이벤트 종료 시점은 프론트 or 앞단에서 처리 해 준다.
        if (event.isDeadlineExpired()) {
            System.out.println("이벤트가 종료되었습니다.");
        }
        */
        event.decreaseCouponQuantity()
        event.setMember(memberId)
        save(event)
    }

    companion object {
        private fun validateDiscountFields(requestDto: CreateEventRequestDto) {
            require(!(requestDto.discountRate == null && requestDto.discountPrice == null) &&
                    !(requestDto.discountRate != null && requestDto.discountPrice != null))
            { "discountRate 또는 discountPrice 중 하나만 존재해야 합니다." }
        }
    }
}
