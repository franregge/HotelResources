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



}