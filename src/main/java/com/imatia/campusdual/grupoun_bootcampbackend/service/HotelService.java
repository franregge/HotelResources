package com.imatia.campusdual.grupoun_bootcampbackend.service;

import com.imatia.campusdual.grupoun_bootcampbackend.api.IHotelService;
import com.imatia.campusdual.grupoun_bootcampbackend.model.dao.HotelDAO;
import com.imatia.campusdual.grupoun_bootcampbackend.model.dto.HotelDTO;
import com.imatia.campusdual.grupoun_bootcampbackend.model.dto.dtomapper.HotelMapper;
import com.imatia.campusdual.grupoun_bootcampbackend.model.entity.Hotel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("HotelService")
@Lazy
public class HotelService implements IHotelService {
    @Autowired
    HotelDAO hotelDAO;
    @Autowired
    HotelMapper hotelMapper;

    @Override
    public HotelDTO queryHotel(HotelDTO hotelDTO) {
        Hotel hotel = hotelDAO.getReferenceById(hotelDTO.getId());

        return hotelMapper.toDTO(hotel);
    }

    @Override
    public List<HotelDTO> queryAll() {
        return hotelMapper.toDTOList(hotelDAO.findAll());
    }

    @Override
    public int insertHotel(HotelDTO hotelDTO) throws HotelAlreadyExistsException {
        List<HotelDTO> allHotelDTOs = queryAll();

        if (allHotelDTOs.stream().anyMatch(dto -> dto.getName().equals(hotelDTO.getName()))) {
            throw new HotelAlreadyExistsException("This hotel already exists");
        }

        Hotel hotel = hotelMapper.toEntity(hotelDTO);
        hotel = hotelDAO.saveAndFlush(hotel);

        return hotel.getId();
    }

    @Override
    public int updateHotel(HotelDTO hotelDTO) {
        return 0;
    }

    @Override
    public int deleteHotel(HotelDTO hotelDTO) throws HotelDoesNotExistException {
        if (queryHotel(hotelDTO) == null) {
            throw new HotelDoesNotExistException("Hotel not found");
        }

        hotelDAO.deleteById(hotelDTO.getId());

        return hotelDTO.getId();
    }
}
