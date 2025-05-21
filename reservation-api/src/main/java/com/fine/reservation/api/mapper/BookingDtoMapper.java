package com.fine.reservation.api.mapper;

import com.fine.reservation.api.dto.BookingResponse;
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
    BookingResponse toResponse(BookingEntity booking);

    List<BookingResponse> toResponseList(List<BookingEntity> bookings);
}
