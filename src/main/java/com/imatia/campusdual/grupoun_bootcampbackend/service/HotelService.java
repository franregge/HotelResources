package com.imatia.campusdual.grupoun_bootcampbackend.service;

import com.imatia.campusdual.grupoun_bootcampbackend.api.IHotelService;
import com.imatia.campusdual.grupoun_bootcampbackend.model.dao.HotelDAO;
import com.imatia.campusdual.grupoun_bootcampbackend.model.dto.HotelDTO;
import com.imatia.campusdual.grupoun_bootcampbackend.model.dto.dtomapper.HotelMapper;
import com.imatia.campusdual.grupoun_bootcampbackend.model.entity.Hotel;
import com.imatia.campusdual.grupoun_bootcampbackend.service.exception.HotelAlreadyExistsException;
import com.imatia.campusdual.grupoun_bootcampbackend.service.exception.HotelDoesNotExistException;
import com.imatia.campusdual.grupoun_bootcampbackend.service.exception.InvalidFloorNumber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.management.InvalidAttributeValueException;
import java.util.List;

@Service("HotelService")
@Lazy
public class HotelService implements IHotelService {
    @Autowired
    HotelDAO hotelDAO;
    @Autowired
    HotelMapper hotelMapper;
    @Autowired
    RoomService roomService;

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
    public int updateHotel(HotelDTO hotelDTO) throws HotelDoesNotExistException, InvalidAttributeValueException, InvalidFloorNumber {

        if (hotelDAO.existsById(hotelDTO.getId())) {

            throw new HotelDoesNotExistException("The hotel does not exist");
        }
        //Validación nombre no nulo ni vacío
        if(hotelDTO.getName()!=null && hotelDTO.getName().isEmpty()){
            throw  new IllegalStateException ("Empty name");
        }
        //El número de plantas modificado debe estar entre 0 y 10
        if(hotelDTO.getNumberOfFloors()<0 && hotelDTO.getNumberOfFloors()>10){
            throw new IllegalStateException("Number of floors must be between 0-10");
        }
        Hotel hotel= hotelDAO.getReferenceById(hotelDTO.getId());
        if(hotelDTO.getNumberOfFloors()>0){
            hotel.setNumberOfFloors(hotelDTO.getNumberOfFloors());
        }
        //Si el nombre del Hotel es nulo, no actualizamos ese dato
        if (hotelDTO.getName()!=null){
           hotel.setName(hotelDTO.getName());
        }
        //Validación de habitaciones al borrar unha planta
        if(hotelDTO.getNumberOfFloors()<hotel.getNumberOfFloors()){
           if (hotel.getRooms().stream().anyMatch(room -> roomService.getFloorNumber(room.getRoomNumber())>hotelDTO.getNumberOfFloors())){
               throw new InvalidFloorNumber("Invalid number of floors");
            }
        }
        return hotelDAO.saveAndFlush(hotelMapper.toEntity(hotelDTO)).getId();
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
