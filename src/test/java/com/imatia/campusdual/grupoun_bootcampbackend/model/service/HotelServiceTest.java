package com.imatia.campusdual.grupoun_bootcampbackend.model.service;

import com.imatia.campusdual.grupoun_bootcampbackend.model.dao.HotelDAO;
import com.imatia.campusdual.grupoun_bootcampbackend.model.dto.HotelDTO;
import com.imatia.campusdual.grupoun_bootcampbackend.model.dto.dtomapper.HotelMapper;
import com.imatia.campusdual.grupoun_bootcampbackend.model.entity.Hotel;
import com.imatia.campusdual.grupoun_bootcampbackend.service.HotelService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class HotelServiceTest {

    @MockBean
    HotelDAO hotelDAO;
    @Autowired
    ApplicationContext context;
    @Autowired
    HotelMapper hotelMapper;
    HotelService hotelService;

    @BeforeAll
    public void init() {
        hotelService = context.getBean(HotelService.class);
    }

    @Test
    public void insertHotel_hotelIsSaved() {
        int id = 1;
        String name = "Overlook Hotel";
        Hotel hotel = new Hotel(id, name);

        when(hotelDAO.getReferenceById(id)).thenReturn(new Hotel(id, name));

        assertEquals(hotelMapper.toDTO(hotel), hotelService.queryHotel(hotelMapper.toDTO(hotel)));
    }

    @Test
    public void deleteHotel_hotelIsDeleted(){

        HotelDTO hotelDTO = new HotelDTO();
        Hotel hotel = new Hotel();

        int id = 1;
        String name= "Overlook Hotel";

        hotel.setId(id);
        hotel.setName(name);

// TODO       when(hotelDAO.getReferenceById(id))


    }

}
