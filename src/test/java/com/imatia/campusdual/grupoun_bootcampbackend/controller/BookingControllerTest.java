package com.imatia.campusdual.grupoun_bootcampbackend.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.imatia.campusdual.grupoun_bootcampbackend.model.dao.BookingDAO;
import com.imatia.campusdual.grupoun_bootcampbackend.model.dto.BookingDTO;
import com.imatia.campusdual.grupoun_bootcampbackend.service.BookingService;
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

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class BookingControllerTest {

    private MockMvc mockMvc;
    @InjectMocks
    BookingController bookingController;
    @Mock
    BookingService bookingService;
    @Autowired
    BookingDAO bookingDAO;
    @Autowired
    ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() throws Exception { mockMvc = MockMvcBuilders.standaloneSetup(bookingController).build();}
    @Test
    public void addBookingTest() throws Exception {
        MvcResult mvcResult = mockMvc
                .perform(
                        MockMvcRequestBuilders.post("/bookings/add")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(
                                                new BookingDTO(1,2, LocalDateTime.now().plusMonths(1), LocalDate.now().plusMonths(2).plusDays(2),"12345678A","Paco","Perez")
                                        )
                                )
                )
                .andReturn();
        assertEquals(201,mvcResult.getResponse().getStatus());
    }
}