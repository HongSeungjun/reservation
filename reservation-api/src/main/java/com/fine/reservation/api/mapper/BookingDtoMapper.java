package com.fine.reservation.api.mapper;

import com.fine.reservation.api.dto.TodayBookingResponse;
import com.fine.reservation.domain.booking.model.Booking;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BookingDtoMapper {
    
    @Mapping(target = "bookingNo", source = "bookingNo")
    @Mapping(target = "machineNo", source = "machineNo")
    @Mapping(target = "bookingStartAt", source = "bookingStartAt")
    @Mapping(target = "bookingEndAt", source = "bookingEndAt")
    @Mapping(target = "bookingPeople", source = "bookingPeople")
    @Mapping(target = "bookingPlayHole", source = "bookingPlayHole")
    @Mapping(target = "bookingName", source = "bookingName")
    @Mapping(target = "cellNumber", source = "cellNumber")
    @Mapping(target = "bookingChannel", source = "bookingChannel")
    @Mapping(target = "gameMode", source = "gameMode")
    @Mapping(target = "gameTime", source = "gameTime")
    TodayBookingResponse toTodayResponse(Booking booking);
    
    List<TodayBookingResponse> toTodayResponseList(List<Booking> bookings);
}