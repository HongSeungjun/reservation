package com.fine.reservation.api.mapper;

import com.fine.reservation.api.dto.TodayBookingResponse;
import com.fine.reservation.domain.booking.entity.BookingEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BookingDtoMapper {

    @Mapping(target = "bookingPeople", source = "peopleCount")
    @Mapping(target = "bookingPlayHole", source = "holeCount")
    @Mapping(target = "bookingName", source = "bookerName")
    @Mapping(target = "cellNumber", source = "phoneNumber")
    @Mapping(target = "gameTime", source = "gameDurationMinutes")
    TodayBookingResponse toTodayResponse(BookingEntity booking);

    List<TodayBookingResponse> toTodayResponseList(List<BookingEntity> bookings);
}
