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

    Predicate<Map<?, ?>> datesAreImpossible = (map) -> {

        LocalDateTime checkindate = (LocalDateTime) map.get("check_in_date");
        LocalDate checkoutdate = (LocalDate) map.get("check_out_date");

        return checkindate
                .isBefore(LocalDateTime.now()) || // TODO: incluír intervalo de tolerancia?
                checkoutdate
                        .isBefore(checkindate.toLocalDate());
    };

    BiPredicate<Map<?, ?>, EntityResult> datesOverlapForRoom = (bookingMap, bookingEntityResult) -> {
        List<LocalDateTime> checkInDateList = (List<LocalDateTime>) bookingEntityResult.get("check_in_date");
        List<LocalDate> checkOutDateList = (List<LocalDate>) bookingEntityResult.get("checkOutDate");
        LocalDateTime insertedCheckInDate = (LocalDateTime) bookingMap.get("check_in_date");
        LocalDate insertedCheckOutDate = (LocalDate) bookingMap.get("check_out_date");


        for (int i = 0; i < checkInDateList.size(); i++) {


            if (
                    insertedCheckInDate
                            .isAfter(
                                    checkInDateList
                                            .get(i)) ||
                            insertedCheckInDate
                                    .toLocalDate()
                                    .isEqual(
                                            checkInDateList
                                                    .get(i)
                                                    .toLocalDate())
                            ||
                            (
                                    insertedCheckOutDate
                                            .isBefore(checkOutDateList.get(i)) ||
                                            insertedCheckOutDate
                                                    .isEqual(checkOutDateList.get(i)))) {
                return true;
            }
        }

        return false;
    };


    @Override
    public EntityResult bookingQuery(Map<?, ?> keymap, List<?> attrList) {
        return this.daoHelper.query(this.bookingDAO, keymap, attrList);
    }

    @Override
    public EntityResult bookingInsert(Map<?, ?> attrMap) throws InvalidBookingDateException {
        Map<String, Integer> filter = new HashMap<>();
        filter.put("room_id", (Integer) attrMap.get("room_id"));
        List<String> queriedAtributeList = List.of("check_in_date", "check_out_date");

        EntityResult entityResult = bookingQuery(filter, queriedAtributeList);

        if (datesOverlapForRoom.test(attrMap, entityResult)) {
            throw new InvalidBookingDateException("Occupied room in those dates");
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
