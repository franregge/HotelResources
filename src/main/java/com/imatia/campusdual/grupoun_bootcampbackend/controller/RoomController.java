package com.imatia.campusdual.grupoun_bootcampbackend.controller;

import com.imatia.campusdual.grupoun_bootcampbackend.model.dto.RoomDTO;
import com.imatia.campusdual.grupoun_bootcampbackend.model.dto.SimpleRoomDTO;
import com.imatia.campusdual.grupoun_bootcampbackend.service.RoomService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/rooms")
public class RoomController {
    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @PostMapping("/add")
    public ResponseEntity<Map<String, ?>> addRoom(@RequestBody SimpleRoomDTO simpleRoomDTO) {
        try {
            HashMap<String, Integer> responseBody = new HashMap<>();
            responseBody.put("insertedId", roomService.insertRoom(simpleRoomDTO));

            return new ResponseEntity<>(responseBody, HttpStatus.CREATED);
        } catch (Exception e) {
            Map<String, String> responseBody = new HashMap<>();
            responseBody.put("error", e.getMessage());

            return new ResponseEntity<>(responseBody, HttpStatus.CONFLICT);
        }
    }

@DeleteMapping("/delete")
    public ResponseEntity<Integer> deleteRoom(RoomDTO roomDTO) {
        try {
            return new ResponseEntity<>(roomService.deleteRoom(roomDTO), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
