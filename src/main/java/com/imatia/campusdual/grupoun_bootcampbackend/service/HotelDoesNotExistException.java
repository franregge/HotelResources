package com.imatia.campusdual.grupoun_bootcampbackend.service;

import com.imatia.campusdual.grupoun_bootcampbackend.model.entity.Hotel;

public class HotelDoesNotExistException extends Exception{
    public HotelDoesNotExistException(String message){
        super(message);
    }
}
