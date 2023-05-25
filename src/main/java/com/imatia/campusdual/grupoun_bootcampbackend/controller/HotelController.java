package com.imatia.campusdual.grupoun_bootcampbackend.controller;

import com.imatia.campusdual.grupoun_bootcampbackend.api.IHotelService;
import com.imatia.campusdual.grupoun_bootcampbackend.model.dto.HotelDTO;
import com.imatia.campusdual.grupoun_bootcampbackend.service.exception.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/hotels")
public class HotelController {
    @Autowired
    private IHotelService hotelService;

    @PostMapping(value = "/add")
    public ResponseEntity<Map<String, ?>> addHotel(@RequestBody HotelDTO hotelDTO) {
        int insertedId;

        try {
            insertedId = hotelService.insertHotel(hotelDTO);
        } catch (HotelAlreadyExistsException | InvalidNumberOfFloorsException | InvalidHotelNameException e) {
            HashMap<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        }

        HashMap<String, Integer> response = new HashMap<>();
        response.put("id", insertedId);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @DeleteMapping(value = "/remove")
    public ResponseEntity<Map<String, ?>> deleteHotel(@RequestBody HotelDTO hotelDTO) {
        int insertedId = 0;

        try {
            insertedId = hotelService.deleteHotel(hotelDTO);
        } catch (HotelDoesNotExistException e) {
            HashMap<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());

            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        HashMap<String, Integer> response = new HashMap<>();
        response.put("id", insertedId);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping(value="/update")
    public
    ResponseEntity<Map<String,?>>updateHotel(@RequestBody HotelDTO hotelDTO){
        int updatedHotelId;
        try {
            updatedHotelId = hotelService.updateHotel(hotelDTO);
        } catch (HotelDoesNotExistException e) {
            HashMap<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());

            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (InvalidFloorNumberException e) {
            HashMap<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());

            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        }
        HashMap<String,Integer>response = new HashMap<>();
        response.put("updatedHotelId",updatedHotelId);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
