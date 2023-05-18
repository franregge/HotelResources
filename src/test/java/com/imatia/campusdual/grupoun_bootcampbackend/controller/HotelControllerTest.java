package com.imatia.campusdual.grupoun_bootcampbackend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.imatia.campusdual.grupoun_bootcampbackend.model.dao.HotelDAO;
import com.imatia.campusdual.grupoun_bootcampbackend.model.dto.HotelDTO;
import com.imatia.campusdual.grupoun_bootcampbackend.service.HotelService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class HotelControllerTest {

    private MockMvc mockMvc;
    @InjectMocks
    HotelController hotelController;
    @Mock
    HotelService hotelService;
    @Autowired
    private HotelDAO hotelDAO;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(hotelController).build();
    }

    /* TODO isto vai facer falta (probablemente)
    @Test
    void getAllHotelsTest() throws  Exception{

        List<HotelDTO> hotelDTOList = new ArrayList<>();

        HotelDTO hotelDTO1 = new HotelDTO();
        HotelDTO hotelDTO2 = new HotelDTO();
        HotelDTO hotelDTO3 = new HotelDTO();

        hotelDTOList.add(hotelDTO1);
        hotelDTOList.add(hotelDTO2);
        hotelDTOList.add(hotelDTO3);
        when(hotelService.queryAll()).thenReturn(hotelDTOList);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/getAll").accept(MediaType.APPLICATION_JSON)).andReturn();


        assertEquals(200, mvcResult.getResponse().getStatus());
        List<HotelDTO>result = new ObjectMapper().readValue(mvcResult.getResponse().getContentAsString(), List.class);
        assertEquals(3, result.size());
    }*/
    @Test
    public void addHotelTest() throws Exception {

        MvcResult mvcResult = mockMvc

                .perform(
                        MockMvcRequestBuilders.post("/hotels/add")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        new ObjectMapper().writeValueAsString(
                                                new HotelDTO(18, "Knarias", 0)
                                        )
                                )
                )
                .andReturn();

        assertEquals(201, mvcResult.getResponse().getStatus());
    }

    @Test
    public void deleteHotel_existingId_HotelIsDeleted() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        int id = 1;
        String name = "Overlook Hotel";
        Integer numberOfFloors = 8;

        when(hotelService.deleteHotel(any())).thenReturn(id);

        MvcResult mvcResult = mockMvc
                .perform(
                        MockMvcRequestBuilders
                                .delete("/hotels/remove")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(
                                                new HotelDTO(id, name, numberOfFloors)
                                        )
                                )
                )
                .andExpect(status().isOk())
                .andReturn();

        HashMap<String, Integer> expectedResult = new HashMap<>();
        expectedResult.put("id", id);

        assertEquals(objectMapper.writeValueAsString(expectedResult), mvcResult.getResponse().getContentAsString());
    }

}
