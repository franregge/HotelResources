package com.ontimize.hr.api.core.service;

import com.ontimize.hr.api.core.service.exception.*;
import com.ontimize.jee.common.dto.EntityResult;

import java.util.List;
import java.util.Map;

public interface IBookingService {

    public EntityResult bookingQuery(Map<?, ?> keymap, List<?> attrList);

    public EntityResult bookingInsert(Map<?, ?> attrMap) throws InvalidBookingDateException, InvalidBookingDNIException;

    EntityResult bookingDelete(Map<?, ?> keyMap) throws Exception;

    public EntityResult bookingUpdate(Map<?, ?> attrMap, Map<?, ?> keyMap) throws InvalidBookingDateException, InvalidBookingDNIException, BookingDoesNotExistException, BookingNotModifiableException, RoomDoesNotExistException, InvalidBookingRoomException;
    public static final String DATE_BEFORE_NOW_MESSAGE = "The arrival date must be after now";
    public static final String ARRIVAL_DATE_AFTER_DEPARTURE_DATE_MESSAGE = "The departure date must be after arrival date";
    public static final String DATES_OVERLAP ="Occupied room in those dates";
    public static  final String BOOKING_INSERT_SUCCESS= "Booking inserted successfully";
    public static  final String BOOKING_UPDATE_SUCCESS= "Booking updated successfully";
    public static final String INVALID_DNI= "The DNI is not valid";
    public static final String BOOKING_NOT_FOUND= "A booking with this ID could not be found";
    public static final String ONE_DAY_MARGIN_ERROR = "Bookings cannot be modified less than 24 prior to the arrival date";


}
