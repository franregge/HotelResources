package com.ontimize.hr.ws.core.rest;

import com.ontimize.hr.api.core.service.IHotelService;
import com.ontimize.hr.api.core.service.IUserService;
import com.ontimize.hr.ws.core.rest.exception.HotelAlreadyExistsException;
import com.ontimize.hr.ws.core.rest.exception.HotelDoesNotExistException;
import com.ontimize.hr.ws.core.rest.exception.InvalidFloorNumberException;
import com.ontimize.hr.ws.core.rest.exception.InvalidNumberOfFloorsException;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.server.rest.ORestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/hotels")
public class HotelController extends ORestController<IHotelService> {

    @Autowired
    private IHotelService hotelService;

    @Override
    public IHotelService getService() {
        return this.hotelService;
    }


    //@DeleteMapping(value = "/remove")
    //public ResponseEntity<Map<String, ?>> deleteHotel(@RequestBody HotelDTO hotelDTO) {
    //    int insertedId = 0;
//
    //    try {
    //        insertedId = hotelService.deleteHotel(hotelDTO);
    //    } catch (HotelDoesNotExistException e) {
    //        HashMap<String, String> response = new HashMap<>();
    //        response.put("error", e.getMessage());
//
    //        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    //    }
//
    //    HashMap<String, Integer> response = new HashMap<>();
    //    response.put("id", insertedId);
//
    //    return new ResponseEntity<>(response, HttpStatus.OK);
    //}
    //@PutMapping(value="/update")
    //public
    //ResponseEntity<Map<String,?>>updateHotel(@RequestBody HotelDTO hotelDTO){
    //    int updatedHotelId;
    //    try {
    //        updatedHotelId = hotelService.updateHotel(hotelDTO);
    //    } catch (HotelDoesNotExistException e) {
    //        HashMap<String, String> response = new HashMap<>();
    //        response.put("error", e.getMessage());
//
    //        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    //    } catch (InvalidFloorNumberException e) {
    //        HashMap<String, String> response = new HashMap<>();
    //        response.put("error", e.getMessage());
//
    //        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    //    }
    //    HashMap<String,Integer>response = new HashMap<>();
    //    response.put("updatedHotelId",updatedHotelId);
//
    //    return new ResponseEntity<>(response, HttpStatus.OK);
    //}

}