package com.imatia.campusdual.grupoun_bootcampbackend.controller;

import com.imatia.campusdual.grupoun_bootcampbackend.api.IBookingService;
import com.imatia.campusdual.grupoun_bootcampbackend.model.dto.BookingDTO;
import com.imatia.campusdual.grupoun_bootcampbackend.service.exception.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/bookings")
public class BookingController {
    @Autowired
    private IBookingService bookingService;
    @PostMapping(value = "/add")
    public ResponseEntity<Map<String,?>>addBooking(@RequestBody BookingDTO bookingDTO) throws RoomDoesNotExistException, RoomNotAvailableException, InvalidBookingDNIException {
        int insertedId;
        try {
            insertedId= bookingService.insertBooking(bookingDTO);
        }catch (BookingAlreadyExistsException e) {
            HashMap<String, String>response = new HashMap<>();
            response.put("error",e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        }catch (InvalidBookingDateException e){
            HashMap<String, String>response = new HashMap<>();
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        }
        HashMap<String,Integer>response = new HashMap<>();
        response.put("id",insertedId);
        return new ResponseEntity<>(response,HttpStatus.CREATED);
    }
    @DeleteMapping(value = "/delete")
    public ResponseEntity<Map<String,?>>deleteBooking (@RequestBody BookingDTO bookingDTO){
        int insertedId =0;
        try {
            insertedId = bookingService.deleteBooking(bookingDTO);
        }catch (BookingDoesNotExistsException e){
        HashMap<String,String>response= new HashMap<>();
        response.put("error",e.getMessage());

        return new ResponseEntity<>(response,HttpStatus.NOT_FOUND);

        }
        HashMap<String,Integer>response = new HashMap<>();
        response.put("id",insertedId);

        return new ResponseEntity<>(response,HttpStatus.OK);
    }
}
