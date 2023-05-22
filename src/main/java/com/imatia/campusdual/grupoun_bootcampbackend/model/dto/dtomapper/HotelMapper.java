package com.imatia.campusdual.grupoun_bootcampbackend.model.dto.dtomapper;

import com.imatia.campusdual.grupoun_bootcampbackend.model.dto.RoomDTO;
import com.imatia.campusdual.grupoun_bootcampbackend.model.entity.Hotel;
import com.imatia.campusdual.grupoun_bootcampbackend.model.dto.HotelDTO;
import com.imatia.campusdual.grupoun_bootcampbackend.model.entity.Room;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring") // Create managed bean
public interface HotelMapper {
    @Mapping(source = "rooms", target = "roomIds", qualifiedByName = "roomIdsFromEntityList")
    HotelDTO toDTO(Hotel hotel);

    @Mapping(source = "roomIds", target = "rooms", qualifiedByName = "roomListFromIds")
    Hotel toEntity(HotelDTO hotelDTO);

    List<HotelDTO> toDTOList(List<Hotel> hotels);

    List<Hotel> toEntityList(List<HotelDTO> hotelDTOs);

    @Named("roomListFromIds")
    default List<Room> roomDTOListFromIds(List<Integer> roomIds) {
        return roomIds
                .stream()
                .map(id -> {
                            Room room = new Room();
                            room.setId(id);
                            return room;
                        }
                )
                .collect(Collectors.toList());
    }

    @Named("roomIdsFromEntityList")
    default List<Integer> roomIdsFromEntityList(List<Room> rooms) {
        return rooms
                .stream()
                .map(Room::getId)
                .collect(Collectors.toList());
    }

}