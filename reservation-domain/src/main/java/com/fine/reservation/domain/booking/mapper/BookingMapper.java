package com.fine.reservation.domain.booking.mapper;

import com.fine.reservation.domain.booking.entity.BookingEntity;
import com.fine.reservation.domain.booking.model.Booking;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface BookingMapper {
    BookingMapper INSTANCE = Mappers.getMapper(BookingMapper.class);

    @Mapping(source = "bookingSeq", target = "bookingNo")
    @Mapping(source = "rgnNo", target = "shopNo")
    @Mapping(source = "workNo", target = "machineNo")
    @Mapping(source = "bookingDate", target = "bookingStartAt")
    @Mapping(source = "endDate", target = "bookingEndAt")
    @Mapping(source = "playerCnt", target = "bookingPeople")
    @Mapping(source = "playHole", target = "bookingPlayHole")
    @Mapping(source = "memo", target = "bookingMemo")
    @Mapping(source = "reserveNo", target = "reserveNo")
    @Mapping(source = "bookingChannel", target = "bookingChannel")
    @Mapping(source = "searchPhone", target = "searchPhone")
    @Mapping(source = "gameMode", target = "gameMode")
    @Mapping(source = "gameTime", target = "gameTime")
    @Mapping(source = "bookingName", target = "bookingName")
    @Mapping(source = "phone", target = "cellNumber")
    @Mapping(target = "machineNos", ignore = true)
    @Mapping(target = "reserveStatus", ignore = true)
    @Mapping(target = "result", ignore = true)
    @Mapping(target = "firstBookingSeq", ignore = true)

    Booking toModel(BookingEntity entity);

    @Mapping(source = "bookingNo", target = "bookingSeq")
    @Mapping(source = "shopNo", target = "rgnNo")
    @Mapping(source = "machineNo", target = "workNo")
    @Mapping(source = "bookingStartAt", target = "bookingDate")
    @Mapping(source = "bookingEndAt", target = "endDate")
    @Mapping(source = "bookingPeople", target = "playerCnt")
    @Mapping(source = "bookingPlayHole", target = "playHole")
    @Mapping(source = "bookingMemo", target = "memo")
    @Mapping(target = "regDt", ignore = true)
    @Mapping(target = "modDt", ignore = true)
    @Mapping(source = "reserveNo", target = "reserveNo")
    @Mapping(source = "bookingChannel", target = "bookingChannel")
    @Mapping(source = "searchPhone", target = "searchPhone")
    @Mapping(source = "gameMode", target = "gameMode")
    @Mapping(source = "gameTime", target = "gameTime")
    @Mapping(source = "bookingName", target = "bookingName")
    @Mapping(source = "cellNumber", target = "phone")
    @Mapping(target = "firstBookingDate", ignore = true)
    @Mapping(target = "firstBookingEndDate", ignore = true)
    @Mapping(target = "fixedYn", ignore = true)
    @Mapping(target = "alarmStatus", ignore = true)
    @Mapping(target = "name", ignore = true)
    @Mapping(target = "regNm", ignore = true)
    @Mapping(target = "bookingPhoneNumber", ignore = true)

    BookingEntity toEntity(Booking model);
}