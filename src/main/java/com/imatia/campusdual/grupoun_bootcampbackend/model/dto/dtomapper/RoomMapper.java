package com.imatia.campusdual.grupoun_bootcampbackend.model.dto.dtomapper;

import com.imatia.campusdual.grupoun_bootcampbackend.model.dto.RoomDTO;
import com.imatia.campusdual.grupoun_bootcampbackend.model.dto.SimpleRoomDTO;
import com.imatia.campusdual.grupoun_bootcampbackend.model.entity.Hotel;
import com.imatia.campusdual.grupoun_bootcampbackend.model.entity.Room;
import com.imatia.campusdual.grupoun_bootcampbackend.service.HotelService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RoomMapper {

    RoomDTO toDTO(Room room);

    @Mapping(source = "hotelId", target = "hotel", qualifiedByName = "hotelFromId")
    Room toEntity(RoomDTO roomDTO);

    List<RoomDTO> toDTOList(List<Room> room);

    List<Room> toEntityList(List<RoomDTO> roomDTOs);

    @Named("hotelFromId")
    default Hotel hotelFromId(int id) {
        Hotel result = new Hotel();
        result.setId(id);
        return result;
    }
}