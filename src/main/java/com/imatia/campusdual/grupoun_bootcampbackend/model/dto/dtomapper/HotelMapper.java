package com.imatia.campusdual.grupoun_bootcampbackend.model.dto.dtomapper;

import com.imatia.campusdual.grupoun_bootcampbackend.model.entity.Hotel;
import com.imatia.campusdual.grupoun_bootcampbackend.model.dto.HotelDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring") // Create managed bean
public interface HotelMapper {
    HotelDTO toDTO (Hotel hotel);
    Hotel toEntity(HotelDTO hotelDTO);
    List<HotelDTO> toDTOList(List<Hotel>hotels);
    List<Hotel>toEntityList(List<HotelDTO>hotelDTOs);
}