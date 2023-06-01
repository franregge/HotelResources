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
}
