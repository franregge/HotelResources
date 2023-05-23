package com.imatia.campusdual.grupoun_bootcampbackend.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.imatia.campusdual.grupoun_bootcampbackend.model.dto.RoomDTO;
import com.imatia.campusdual.grupoun_bootcampbackend.service.RoomService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class RoomControllerTest {

    private MockMvc mockMvc;
    @MockBean
    RoomService roomService;
    RoomController roomController;
    @Autowired
    WebApplicationContext context;

    @Autowired
    ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        roomController = context.getBean(RoomController.class);
    }

    @Test
    public void addRoomTest() throws Exception {
        MvcResult mvcResult = mockMvc.perform(
                MockMvcRequestBuilders.post("/rooms/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(new RoomDTO(12, 79))
                        )
        ).andReturn();

        assertEquals(HttpStatus.CREATED.value(), mvcResult.getResponse().getStatus());
    }

    @Test
    public void deleteRoom_existingId_RoomIsDeleted() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        int id = 1;
        int roomNumber = 124;

        when(roomService.deleteRoom(any())).thenReturn(id);

        RoomDTO roomDTO = new RoomDTO(id, roomNumber);

        MvcResult mvcResult = mockMvc
                .perform(
                        MockMvcRequestBuilders
                                .delete("/rooms/delete")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(
                                                roomDTO
                                        )
                                )
                )
                .andExpect(status().isOk())
                .andReturn();

        verify(roomService, times(1)).deleteRoom(any());

        HashMap<String, Integer> expectedResult = new HashMap<>();
        expectedResult.put("deletedId", id);

        assertEquals(objectMapper.writeValueAsString(expectedResult), mvcResult.getResponse().getContentAsString());
    }

    @Test
    public void updateRoom_validRoom_returnStatusOkandId() throws Exception {

        RoomDTO roomDTO = new RoomDTO(12, 101);
        when(roomService.updateRoom(any())).thenReturn(roomDTO.getId());
        MvcResult mvcResult = mockMvc.perform(
                MockMvcRequestBuilders.put("/rooms/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(roomDTO)
                        )
        ).andReturn();

        assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus());

        objectMapper.readValue(mvcResult.getResponse().getContentAsString(), HashMap.class);
        HashMap hashMap =  objectMapper.readValue(mvcResult.getResponse().getContentAsString(), HashMap.class);

        assertEquals(roomDTO.getId(),hashMap.get("updatedId"));
    }
}
