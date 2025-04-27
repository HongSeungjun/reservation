package com.fine.reservation.domain.reservation.mapper;

import com.fine.reservation.domain.reservation.entity.ReservationEntity;
import com.fine.reservation.domain.reservation.model.Reservation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ReservationMapper {
    ReservationMapper INSTANCE = Mappers.getMapper(ReservationMapper.class);

    @Mapping(source = "reserveDatetime", target = "reserveStartAt")
    @Mapping(source = "reserveDatetimeEnd", target = "reserveEndAt")
    @Mapping(source = "registDate", target = "createdAt")
    @Mapping(target = "modifyDate", ignore = true)
    @Mapping(target = "reserveCancelReason", ignore = true)
    @Mapping(target = "alarmStatus", ignore = true)
    @Mapping(target = "autocallStatus", ignore = true)
    @Mapping(target = "reserveNameMask", ignore = true)
    @Mapping(target = "reservePhoneNumberMask", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "nickname", ignore = true)
    @Mapping(target = "sex", ignore = true)
    @Mapping(target = "isSelfReserve", ignore = true)
    @Mapping(target = "isSelfRegist", ignore = true)
    Reservation toModel(ReservationEntity entity);

    @Mapping(source = "reserveStartAt", target = "reserveDatetime")
    @Mapping(source = "reserveEndAt", target = "reserveDatetimeEnd")
    @Mapping(source = "createdAt", target = "registDate")
    @Mapping(target = "modifyDate", ignore = true)
    @Mapping(target = "reserveCancelReason", ignore = true)
    @Mapping(target = "alarmStatus", ignore = true)
    @Mapping(target = "autocallStatus", ignore = true)
    @Mapping(target = "reserveNameMask", ignore = true)
    @Mapping(target = "reservePhoneNumberMask", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "nickname", ignore = true)
    @Mapping(target = "sex", ignore = true)
    @Mapping(target = "isSelfReserve", ignore = true)
    @Mapping(target = "isSelfRegist", ignore = true)
    ReservationEntity toEntity(Reservation model);
}

