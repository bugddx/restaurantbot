package com.restaurantbot.reservation.mapper;

import com.restaurantbot.reservation.dto.CreateReservationRequest;
import com.restaurantbot.reservation.dto.ReservationResponse;
import com.restaurantbot.reservation.dto.UpdateReservationRequest;
import com.restaurantbot.reservation.entity.Reservation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ReservationMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "restaurant", ignore = true)
    @Mapping(target = "phoneNumber", ignore = true)
    @Mapping(target = "status", ignore = true)
    Reservation toEntity(CreateReservationRequest request);

    @Mapping(source = "restaurant.id", target = "restaurantId")
    ReservationResponse toResponse(Reservation reservation);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "restaurant", ignore = true)
    @Mapping(target = "phoneNumber", ignore = true)
    void update(UpdateReservationRequest request, @MappingTarget Reservation reservation);
}
