package com.imatia.campusdual.grupoun_bootcampbackend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.imatia.campusdual.grupoun_bootcampbackend.model.dao.RoomDAO;
import com.imatia.campusdual.grupoun_bootcampbackend.model.dto.RoomDTO;
import com.imatia.campusdual.grupoun_bootcampbackend.service.RoomService;
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
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class RoomControllerTest {

    private MockMvc mockMvc;
    @InjectMocks
    RoomController roomController;
    @Mock
    RoomService roomService;
    @Autowired
    private RoomDAO roomDAO;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(roomController).build();
    }

    @Test
    public void addRoomTest() throws Exception {
        MvcResult mvcResult = mockMvc.perform(
                MockMvcRequestBuilders.post("/rooms/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(new RoomDTO(12, 79))
                        )
        ).andReturn();

        assertEquals(201, mvcResult.getResponse().getStatus());
    }

    @Test
    public void deleteRoom_existingId_RoomIsDeleted() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        int id = 1;
        int roomNumber = 124;

        when(roomService.deleteRoom(any())).thenReturn(id);

        MvcResult mvcResult = mockMvc
                .perform(
                        MockMvcRequestBuilders
                                .delete("/rooms/delete")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(
                                                new RoomDTO(id, roomNumber)
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
