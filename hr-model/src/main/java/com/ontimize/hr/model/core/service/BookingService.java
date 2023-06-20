package com.ontimize.hr.model.core.service;

import com.ontimize.hr.api.core.service.IBookingService;
import com.ontimize.hr.api.core.service.exception.*;
import com.ontimize.hr.model.core.dao.BookingDAO;
import com.ontimize.hr.model.core.dao.RoomDAO;
import com.ontimize.hr.model.core.dao.UserDAO;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.common.dto.EntityResultMapImpl;
import com.ontimize.jee.common.security.PermissionsProviderSecured;
import com.ontimize.jee.server.dao.DefaultOntimizeDaoHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

@Lazy
@Service("BookingService")
public class BookingService implements IBookingService {

    @Autowired
    private DefaultOntimizeDaoHelper daoHelper;
    @Autowired
    private BookingDAO bookingDAO;
    @Autowired
    private RoomService roomService;
    @Autowired
    private UserService userService;
    private final BiPredicate<LocalDate, LocalDate> dateAfterOrEqual = (date1, date2) -> date1.isAfter(date2) || date1.isEqual(date2);
    private final BiPredicate<LocalDate, LocalDate> dateBeforeOrEqual = (date1, date2) -> date1.isBefore(date2) || date1.isEqual(date2);

    Predicate<Map<?, ?>> arrivalDateBeforeNow = bookingMap -> {
        LocalDate arrivalDate = LocalDate.parse((String) bookingMap.get(BookingDAO.ARRIVAL_DATE));
        return arrivalDate.isBefore(LocalDate.now());
    };

    Predicate<Map<?, ?>> departureDateBeforeArrivalDate = bookingMap -> {
        LocalDate departureDate = LocalDate.parse((String) bookingMap.get(BookingDAO.DEPARTURE_DATE));
        LocalDate arrivalDate = LocalDate.parse((String) bookingMap.get(BookingDAO.ARRIVAL_DATE));
        return departureDate.isBefore(arrivalDate);
    };

    BiPredicate<Map<?, ?>, EntityResult> datesOverlapForRoomExcludingThisBooking = (bookingMap, bookingsForThisRoomEntityResult) -> {
        Integer presentBookingId = (Integer) bookingMap.get(BookingDAO.ID);
        LocalDate departureDate = LocalDate.parse((String) bookingMap.get(BookingDAO.DEPARTURE_DATE));
        LocalDate arrivalDate = LocalDate.parse((String) bookingMap.get(BookingDAO.ARRIVAL_DATE));

        LocalDate departureDateToCheck;
        LocalDate arrivalDateToCheck;

        for (int i = 0; i < bookingsForThisRoomEntityResult.calculateRecordNumber(); i++) {
            Map<?, ?> bookingToCheck = bookingsForThisRoomEntityResult.getRecordValues(i);

            if (bookingToCheck.get(BookingDAO.ID) == presentBookingId) {
                continue;
            }

            departureDateToCheck = ((Date) bookingToCheck.get(BookingDAO.DEPARTURE_DATE)).toLocalDate();
            arrivalDateToCheck = ((Date) bookingToCheck.get(BookingDAO.ARRIVAL_DATE)).toLocalDate();

            if (
                    ((dateAfterOrEqual.test(arrivalDate, arrivalDateToCheck) && arrivalDate.isBefore(departureDateToCheck))
                            || (departureDate.isAfter(arrivalDateToCheck) && dateBeforeOrEqual.test(departureDate, departureDateToCheck)))
                            || ((dateAfterOrEqual.test(arrivalDateToCheck, arrivalDate) && (arrivalDateToCheck.isBefore(departureDate)))
                            || (departureDateToCheck.isAfter(arrivalDate) && dateBeforeOrEqual.test(departureDateToCheck, departureDate)))
            ) {
                return true;
            }
        }

        return false;
    };

    BiPredicate<Map<?, ?>, EntityResult> datesOverlapForRoom = (bookingMap, bookingsForThisRoomEntityResult) -> {
        LocalDate departureDate = LocalDate.parse((String) bookingMap.get(BookingDAO.DEPARTURE_DATE));
        LocalDate arrivalDate = LocalDate.parse((String) bookingMap.get(BookingDAO.ARRIVAL_DATE));

        LocalDate departureDateToCheck;
        LocalDate arrivalDateToCheck;

        for (int i = 0; i < bookingsForThisRoomEntityResult.calculateRecordNumber(); i++) {
            Map<?, ?> bookingToCheck = bookingsForThisRoomEntityResult.getRecordValues(i);

            departureDateToCheck = ((Date) bookingToCheck.get(BookingDAO.DEPARTURE_DATE)).toLocalDate();
            arrivalDateToCheck = ((Date) bookingToCheck.get(BookingDAO.ARRIVAL_DATE)).toLocalDate();

            if (
                    ((dateBeforeOrEqual.test(arrivalDate, arrivalDateToCheck) && arrivalDate.isBefore(departureDateToCheck))
                            || (departureDate.isAfter(arrivalDateToCheck) && dateBeforeOrEqual.test(departureDate, departureDateToCheck)))
                            || (dateAfterOrEqual.test(arrivalDateToCheck, arrivalDate) && (arrivalDateToCheck.isBefore(departureDate))
                            || (departureDateToCheck.isAfter(arrivalDate) && dateBeforeOrEqual.test(departureDateToCheck, departureDate)))
            ) {
                return true;
            }
        }

        return false;
    };

    private void validateBooking(Map<?, ?> attrMap, BiPredicate<Map<?, ?>, EntityResult> overlapTestPredicate) throws InvalidBookingDateException, UserDoesNotExistException {
        Map<String, ? super Object> userIDFilter = new HashMap<>();
        userIDFilter.put(UserDAO.LOGIN_NAME, attrMap.get(BookingDAO.USER_LOGIN_NAME));

        if (userService.userQuery(userIDFilter, List.of(UserDAO.LOGIN_NAME)).isEmpty()) {
            throw new UserDoesNotExistException(IBookingService.USER_NOT_FOUND);
        }
        if (arrivalDateBeforeNow.test(attrMap)) {
            throw new InvalidBookingDateException(IBookingService.DATE_BEFORE_NOW_MESSAGE);
        }

        if (departureDateBeforeArrivalDate.test(attrMap)) {
            throw new InvalidBookingDateException(IBookingService.ARRIVAL_DATE_AFTER_DEPARTURE_DATE_MESSAGE);
        }

        Map<String, Integer> filter = new HashMap<>();
        filter.put(BookingDAO.ROOM_ID, (Integer) attrMap.get(BookingDAO.ROOM_ID));
        List<String> queriedAtributeList = List.of(BookingDAO.ARRIVAL_DATE, BookingDAO.DEPARTURE_DATE, BookingDAO.ROOM_ID);

        EntityResult bookingsForThisRoomEntityResult = bookingQuery(filter, queriedAtributeList);

        if (overlapTestPredicate.test(attrMap, bookingsForThisRoomEntityResult)) {
            throw new InvalidBookingDateException(IBookingService.DATES_OVERLAP);
        }

    }

    private void validateBookingUpdate(Map<?, ?> attrMap, BiPredicate<Map<?, ?>, EntityResult> overlapTestPredicate) throws InvalidBookingDateException {


        if (arrivalDateBeforeNow.test(attrMap)) {
            throw new InvalidBookingDateException(IBookingService.DATE_BEFORE_NOW_MESSAGE);
        }

        if (departureDateBeforeArrivalDate.test(attrMap))
            throw new InvalidBookingDateException(IBookingService.ARRIVAL_DATE_AFTER_DEPARTURE_DATE_MESSAGE);

        Map<String, Integer> filter = new HashMap<>();
        filter.put(BookingDAO.ROOM_ID, (Integer) attrMap.get(BookingDAO.ROOM_ID));
        List<String> queriedAtributeList = List.of(BookingDAO.ID, BookingDAO.ARRIVAL_DATE, BookingDAO.DEPARTURE_DATE, BookingDAO.ROOM_ID);

        EntityResult bookingsForThisRoomEntityResult = bookingQuery(filter, queriedAtributeList);

        if (overlapTestPredicate.test(attrMap, bookingsForThisRoomEntityResult)) {
            throw new InvalidBookingDateException(IBookingService.DATES_OVERLAP);
        }
    }

    @Secured({PermissionsProviderSecured.SECURED})
    @Override
    public EntityResult bookingQuery(Map<?, ?> keymap, List<?> attrList) {
        return this.daoHelper.query(this.bookingDAO, keymap, attrList);
    }

    @Secured({PermissionsProviderSecured.SECURED})
    @Override
    public EntityResult bookingInsert(Map<?, ?> attrMap) {
        EntityResult result;

        try {
            validateBooking(attrMap, datesOverlapForRoom);

            result = this.daoHelper.insert(this.bookingDAO, attrMap);
            result.setCode(EntityResult.OPERATION_SUCCESSFUL_SHOW_MESSAGE);
            result.setMessage(IBookingService.BOOKING_INSERT_SUCCESS);

        } catch (Exception e) {
            result = new EntityResultMapImpl();
            result.setMessage(e.getMessage());
            result.setCode(EntityResult.OPERATION_WRONG);
        }

        return result;
    }

    @Secured({PermissionsProviderSecured.SECURED})
    @Override
    public EntityResult bookingDelete(Map<?, ?> keyMap) {
        Integer bookingId = (Integer) keyMap.get(BookingDAO.ID);

        //if (!this.daoHelper.query(hotelDAO, keyMap, List.of("hotel_id"),HotelDAO.QUERY_BOOKINGS_IN_HOTEL).isEmpty()){
        //    throw new Exception("This hotel has booked rooms");
        //}

        if (this.daoHelper.query(bookingDAO, keyMap, List.of(RoomDAO.ID)).isEmpty()) {
            EntityResult result = new EntityResultMapImpl();
            result.setMessage(IBookingService.NO_BOOKING_WITH_ID);
            result.setCode(EntityResult.OPERATION_WRONG);
            return result;
        }

        EntityResult result = this.daoHelper.delete(bookingDAO, keyMap);
        result.setMessage(IBookingService.DELETION_SUCCESS);
        result.setCode(EntityResult.OPERATION_SUCCESSFUL_SHOW_MESSAGE);
        result.put("deleted_id", bookingId);
        return result;
    }

    @Secured({PermissionsProviderSecured.SECURED})
    @Override
    public EntityResult bookingUpdate(Map<?, ?> attrMap, Map<?, ?> keyMap) {
        EntityResult result;

        try {
            Map<String, Integer> bookingIdFilter = new HashMap<>();
            Map<String, Integer> bookingUserIdFilter = new HashMap<>();
            bookingIdFilter.put(BookingDAO.ID, (Integer) keyMap.get(BookingDAO.ID));
            bookingUserIdFilter.put(BookingDAO.USER_LOGIN_NAME, (Integer) keyMap.get(BookingDAO.USER_LOGIN_NAME));
            EntityResult userEntityResult = userService.userQuery(bookingUserIdFilter, List.of(UserDAO.LOGIN_NAME));

            if (userEntityResult.isEmpty()) {
                throw new UserDoesNotExistException(IBookingService.USER_NOT_FOUND);

            }

            EntityResult originalBookingEntityResult =
                    daoHelper.query(bookingDAO, bookingIdFilter, List.of(BookingDAO.ARRIVAL_DATE, BookingDAO.ROOM_ID, BookingDAO.DEPARTURE_DATE));


            if (originalBookingEntityResult.isEmpty()) {
                throw new BookingDoesNotExistException(IBookingService.BOOKING_NOT_FOUND);
            }

            Map<?, ?> originalBooking = originalBookingEntityResult.getRecordValues(0);
            LocalDate originalBookingArrivalDate = ((Date) originalBooking.get(BookingDAO.ARRIVAL_DATE)).toLocalDate();

            Map<String, Integer> originalRoomIdFilter = new HashMap<>();
            originalRoomIdFilter.put(RoomDAO.ID, (Integer) originalBooking.get(BookingDAO.ROOM_ID));
            EntityResult destinationRoomEntityResult = roomService.roomQuery(originalRoomIdFilter, List.of(RoomDAO.HOTEL_ID));

            if (destinationRoomEntityResult.isEmpty()) {
                throw new RoomDoesNotExistException(IBookingService.BOOKING_NOT_FOUND);
            }

            Map<? super Object, ? super Object> bookingWithDates = new HashMap<>(attrMap);
            bookingWithDates.put(BookingDAO.ID, keyMap.get(BookingDAO.ID));
            bookingWithDates.put(BookingDAO.ROOM_ID, originalBooking.get(BookingDAO.ROOM_ID));

            if (attrMap.get(BookingDAO.ARRIVAL_DATE) == null) {
                bookingWithDates.put(BookingDAO.ARRIVAL_DATE, (((Date) originalBooking.get(BookingDAO.ARRIVAL_DATE)).toLocalDate().toString()));
            }

            if (attrMap.get(BookingDAO.DEPARTURE_DATE) == null) {
                bookingWithDates.put(BookingDAO.DEPARTURE_DATE, (((Date) originalBooking.get(BookingDAO.DEPARTURE_DATE)).toLocalDate().toString()));
            }

            validateBookingUpdate(bookingWithDates, datesOverlapForRoomExcludingThisBooking);

            if (LocalDate.now().until(originalBookingArrivalDate, ChronoUnit.DAYS) < 1) {
                throw new BookingNotModifiableException(IBookingService.ONE_DAY_MARGIN_ERROR);
            }

            Map<String, Integer> newRoomIdFilter = new HashMap<>();
            newRoomIdFilter.put(RoomDAO.ID, (Integer) originalBooking.get(BookingDAO.ROOM_ID));
            Map<?, ?> originalRoom = roomService.roomQuery(newRoomIdFilter, List.of(RoomDAO.HOTEL_ID)).getRecordValues(0);

            if (
                    originalRoom.get(RoomDAO.HOTEL_ID) != destinationRoomEntityResult.getRecordValues(0).get(RoomDAO.HOTEL_ID)
            ) {
                throw new InvalidBookingRoomException("The new room must be in the same hotel as the original one");
            }

            bookingWithDates.put(BookingDAO.ARRIVAL_DATE, LocalDate.parse((String) bookingWithDates.get(BookingDAO.ARRIVAL_DATE)));
            bookingWithDates.put(BookingDAO.DEPARTURE_DATE, LocalDate.parse((String) bookingWithDates.get(BookingDAO.DEPARTURE_DATE)));

            result = this.daoHelper.update(this.bookingDAO, bookingWithDates, keyMap);
            result.put("updated_id", keyMap.get(BookingDAO.ID));
            result.setMessage("Booking updated successfully");
            result.setCode(EntityResult.OPERATION_SUCCESSFUL_SHOW_MESSAGE);
        } catch (Exception e) {

            result = new EntityResultMapImpl();
            result.setMessage(e.getMessage());
            result.setCode(EntityResult.OPERATION_WRONG);
        }

        return result;
    }
}
