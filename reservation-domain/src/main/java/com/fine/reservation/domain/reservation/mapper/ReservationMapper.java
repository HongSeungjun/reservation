package com.fine.reservation.domain.reservation.mapper;

import com.fine.reservation.domain.reservation.entity.ReservationEntity;
import com.fine.reservation.domain.reservation.model.Reservation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

// ReservationMapper.java

@Mapper(componentModel = "spring")
public interface ReservationMapper {
    ReservationMapper INSTANCE = Mappers.getMapper(ReservationMapper.class);

    @Mapping(source = "reserveNo", target = "reserveNo")
    @Mapping(source = "shopNo", target = "shopNo")
    @Mapping(source = "usrNo", target = "usrNo")
    @Mapping(source = "reserveStatus", target = "reserveStatus")
    @Mapping(source = "reserveDatetime", target = "reserveStartAt")
    @Mapping(source = "reserveDatetimeEnd", target = "reserveEndAt")
    @Mapping(source = "reserveName", target = "reserveName")
    @Mapping(source = "reservePhoneNumber", target = "reservePhoneNumber")
    @Mapping(source = "reserveSoftware", target = "reserveSoftware")
    @Mapping(source = "reservePeople", target = "reservePeople")
    @Mapping(source = "reserveRequestMessage", target = "reserveRequestMessage")
    @Mapping(source = "registDate", target = "createdAt")
    @Mapping(source = "userId", target = "usrId")
    @Mapping(source = "nickname", target = "usrNickName")
    @Mapping(source = "sex", target = "sex")
    @Mapping(source = "bookingName", target = "bookingName")
    @Mapping(source = "bookingPhoneNumber", target = "cellNumber")
    @Mapping(source = "gameType", target = "gameType")
    @Mapping(source = "roomCount", target = "roomCount")
    @Mapping(source = "gameCount", target = "gameCount")
    @Mapping(source = "optionFlag", target = "optionFlag")
    Reservation toModel(ReservationEntity entity);

    @Mapping(source = "reserveNo", target = "reserveNo")
    @Mapping(source = "shopNo", target = "shopNo")
    @Mapping(source = "usrNo", target = "usrNo")
    @Mapping(source = "reserveStatus", target = "reserveStatus")
    @Mapping(source = "reserveStartAt", target = "reserveDatetime")
    @Mapping(source = "reserveEndAt", target = "reserveDatetimeEnd")
    @Mapping(source = "reserveName", target = "reserveName")
    @Mapping(source = "reservePhoneNumber", target = "reservePhoneNumber")
    @Mapping(source = "reserveSoftware", target = "reserveSoftware")
    @Mapping(source = "reservePeople", target = "reservePeople")
    @Mapping(source = "reserveRequestMessage", target = "reserveRequestMessage")
    @Mapping(source = "createdAt", target = "registDate")
    @Mapping(source = "usrId", target = "userId")
    @Mapping(source = "usrNickName", target = "nickname")
    @Mapping(source = "sex", target = "sex")
    @Mapping(source = "bookingName", target = "bookingName")
    @Mapping(source = "cellNumber", target = "bookingPhoneNumber")
    @Mapping(source = "gameType", target = "gameType")
    @Mapping(source = "roomCount", target = "roomCount")
    @Mapping(source = "gameCount", target = "gameCount")
    @Mapping(source = "optionFlag", target = "optionFlag")
    ReservationEntity toEntity(Reservation model);
}

