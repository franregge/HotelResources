package com.imatia.campusdual.grupoun_bootcampbackend.model.dto.dtomapper;

import com.imatia.campusdual.grupoun_bootcampbackend.model.dto.BookingDTO;
import com.imatia.campusdual.grupoun_bootcampbackend.model.entity.Booking;
import com.imatia.campusdual.grupoun_bootcampbackend.model.entity.Room;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;


import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring") //Create managed bean
public interface BookingMapper {
    @Mapping(source = "room",target = "roomId",qualifiedByName = "roomIdFromRoom")
    BookingDTO toDTO(Booking booking);
    @Mapping(source = "roomId",target ="room",qualifiedByName = "roomFromId")
    Booking toEntity(BookingDTO bookingDTO);
    List<BookingDTO>toDTOList(List<Booking>bookings);
    List<Booking>toEntityList(List<BookingDTO>bookingDTOs);

    @Named("roomFromId")
    default Room roomFromId(int bookingIds){

        Room room = new Room();
        room.setId(bookingIds);
        return room;
    }

    @Named("roomIdFromRoom")
    default int roomIdFromRoom(Room room) {

        assert room != null;
        return room.getId();

    }

}
