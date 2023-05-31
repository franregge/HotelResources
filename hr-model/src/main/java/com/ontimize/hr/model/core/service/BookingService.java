package com.ontimize.hr.model.core.service;

import com.ontimize.hr.api.core.service.IBookingService;
import com.ontimize.hr.model.core.dao.BookingDAO;
import com.ontimize.hr.api.core.service.exception.InvalidBookingDateException;
import com.ontimize.hr.model.core.dao.RoomDAO;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.server.dao.DefaultOntimizeDaoHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
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

    Predicate<Map<?,?>> arrivalDateBeforeNow = (bookingMap) ->{
        LocalDate arrivalDate = (LocalDate) bookingMap.get(BookingDAO.ARRIVAL_DATE);
        return arrivalDate.isBefore(LocalDate.now());
    };


    Predicate<Map<?,?>> departureDateBeforeArrivalDate =(bookingMap)->{
        LocalDate departureDate = (LocalDate) bookingMap.get(BookingDAO.DEPARTURE_DATE);
        LocalDate arrivalDate = (LocalDate) bookingMap.get(BookingDAO.ARRIVAL_DATE);
        return departureDate.isBefore(arrivalDate);
    };
    BiPredicate<Map<?,?>, EntityResult> datesOverlapForRoom = (bookingMap, bookingEntityResult) ->{
        LocalDate departureDate = (LocalDate) bookingMap.get(BookingDAO.DEPARTURE_DATE);
        LocalDate arrivalDate = (LocalDate) bookingMap.get(BookingDAO.ARRIVAL_DATE);

        LocalDate departureDateCheck = (LocalDate) bookingEntityResult.get(BookingDAO.DEPARTURE_DATE);
        LocalDate arrivalDateCheck = (LocalDate) bookingEntityResult.get(BookingDAO.ARRIVAL_DATE);

        int roomID = (int) bookingMap.get(BookingDAO.ROOM_ID);
        int roomIDCheck = (int) bookingEntityResult.get(BookingDAO.ROOM_ID);

        return roomID == roomIDCheck &&
                (
                        arrivalDate
                                .isAfter(arrivalDateCheck) ||
                                arrivalDate
                                        .isEqual(arrivalDateCheck)
                ) &&
                (
                        departureDate
                                .isBefore(departureDateCheck) ||
                                departureDate
                                        .isEqual(departureDateCheck)
                );
    };





    @Override
    public EntityResult bookingQuery(Map<?, ?> keymap, List<?> attrList) {
        return this.daoHelper.query(this.bookingDAO, keymap, attrList);
    }

    @Override
    public EntityResult bookingInsert(Map<?, ?> attrMap) throws InvalidBookingDateException {
        Map<String, Integer> filter = new HashMap<>();
        filter.put("room_id", (Integer) attrMap.get("room_id"));
        List<String> queriedAtributeList = List.of(BookingDAO.ARRIVAL_DATE, BookingDAO.DEPARTURE_DATE);

        EntityResult entityResult = bookingQuery(filter, queriedAtributeList);

        if (datesOverlapForRoom.test(attrMap, entityResult)) {
            throw new InvalidBookingDateException("Occupied room in those dates");
        }
        if (arrivalDateBeforeNow.test(attrMap)){
            throw new InvalidBookingDateException("The arrival date must be after now");
        }
        if (departureDateBeforeArrivalDate.test(attrMap)){
            throw new InvalidBookingDateException("The departure date must be after arrival date");
        }

        return this.daoHelper.insert(this.bookingDAO, attrMap);
    }
    @Override
    public EntityResult bookingDelete(Map<?, ?> keyMap) throws Exception {
        Integer bookingId = (Integer) keyMap.get(BookingDAO.ID);

        //if (!this.daoHelper.query(hotelDAO, keyMap, List.of("hotel_id"),HotelDAO.QUERY_BOOKINGS_IN_HOTEL).isEmpty()){
        //    throw new Exception("This hotel has booked rooms");
        //}

        if(this.daoHelper.query(bookingDAO, keyMap, List.of(RoomDAO.ID)).isEmpty()){
            EntityResult result = this.daoHelper.delete(bookingDAO, keyMap);
            result.setMessage("No booking with this id");
            result.setCode(EntityResult.OPERATION_WRONG);
            return result;
        }

        EntityResult result = this.daoHelper.delete(bookingDAO, keyMap);
        result.setMessage("Booking deleted successfully");
        result.put("deleted_id", bookingId);
        return result;
    }

    @Override
    public EntityResult bookingUpdate(Map<?, ?> attrMap, Map<?, ?> keyMap) {


        return this.daoHelper.update(this.bookingDAO, attrMap, keyMap);
    }
}
