package com.imatia.campusdual.grupoun_bootcampbackend.model.dto.dtomapper;

import com.imatia.campusdual.grupoun_bootcampbackend.model.dto.BookingDTO;
import com.imatia.campusdual.grupoun_bootcampbackend.model.entity.Booking;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;


import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring") //Create managed bean

public interface BookingMapper {
    @Mapping(source = "room",target = "roomId",qualifiedByName = "roomIdFromBooking")
    BookingDTO toDTO(Booking booking);
    @Mapping(source = "bookingId",target ="booking",qualifiedByName = "bookingFromId")
    Booking toEntity(BookingDTO bookingDTO);
    List<BookingDTO>toDTOList(List<Booking>bookings);
    List<Booking>toEntityList(List<BookingDTO>bookingDTOs);

    @Named("bookingFromId")
    default Booking bookingDTOListFromIds(List<Integer>bookingIds){
        return bookingId
                .stream()
                .map(id->{
                    Booking booking = new Booking();
                    booking.setId(id);
                    return booking;
    }

    @Named("roomIdFromEntity")
    default List<Integer>bookingIdsFromEntityList(List<Booking>bookings){
        return bookings
                .stream()
                .map(Booking::getId)
                .collect(Collectors.toList());
    }

}
