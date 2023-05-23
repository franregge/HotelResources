package com.imatia.campusdual.grupoun_bootcampbackend.controller;

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
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
    @Test
    public void  deleteBookingTest_existingId_BookingIsDeleted() throws Exception {
        int bookingId=2;
        int roomId= 1;
        String clientDNI= "12345678A";
        String clientName= "Paco";
        String clientSurname1="Perez";

       when(bookingService.deleteBooking(any())).thenReturn(bookingId);

       MvcResult mvcResult = mockMvc
               .perform(
                       MockMvcRequestBuilders
                               .delete("/bookings/delete")
                               .contentType(MediaType.APPLICATION_JSON)
                               .accept(MediaType.APPLICATION_JSON)
                               .content(
                                       objectMapper.writeValueAsString(
                                               new BookingDTO(bookingId,roomId,LocalDateTime.now().plusMonths(1), LocalDate.now().plusMonths(2).plusDays(2),clientDNI,clientName,clientSurname1)
                                       )
                               )
               )
               .andExpect(status().isOk())
               .andReturn();

        HashMap<String,Integer>expectedResult = new HashMap<>();
        expectedResult.put("id",bookingId);

        assertEquals(objectMapper.writeValueAsString(expectedResult),mvcResult.getResponse().getContentAsString());

    }
}