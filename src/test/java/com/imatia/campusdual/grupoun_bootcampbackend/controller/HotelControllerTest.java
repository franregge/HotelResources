package com.imatia.campusdual.grupoun_bootcampbackend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.imatia.campusdual.grupoun_bootcampbackend.model.dto.HotelDTO;
import com.imatia.campusdual.grupoun_bootcampbackend.model.entity.Hotel;
import com.imatia.campusdual.grupoun_bootcampbackend.service.HotelService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class HotelControllerTest {

    private MockMvc mockMvc;
    @InjectMocks
    HotelController hotelController;
    @Mock
    HotelService hotelService;
    @BeforeEach
    public void setUp(){
        mockMvc = MockMvcBuilders.standaloneSetup(hotelController).build();
    }
    @Test
    void getAllHotelsTest() throws Exception{

        List<HotelDTO>hotelDTOList = new ArrayList<>();

        HotelDTO hotelDTO1 = new HotelDTO();
        HotelDTO hotelDTO2 = new HotelDTO();
        HotelDTO hotelDTO3 = new HotelDTO();

        hotelDTOList.add(hotelDTO1);
        hotelDTOList.add(hotelDTO2);
        hotelDTOList.add(hotelDTO3);
        when(this.hotelService.queryAll()).thenReturn(hotelDTOList);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/getAll").accept(MediaType.APPLICATION_JSON)).andReturn();


        assertEquals(200, mvcResult.getResponse().getStatus());
        List<HotelDTO>result = new ObjectMapper().readValue(mvcResult.getResponse().getContentAsString(), List.class);
        assertEquals(3, result.size());



    }


}
