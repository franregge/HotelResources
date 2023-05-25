package com.ontimize.hr.model.core.service;

import com.ontimize.hr.api.core.service.IBookingService;
import com.ontimize.hr.model.core.dao.BookingDAO;
import com.ontimize.hr.model.core.service.exception.InvalidBookingDateException;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.server.dao.DefaultOntimizeDaoHelper;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public class BookingService implements IBookingService {

    @Autowired
    private DefaultOntimizeDaoHelper daoHelper;
    @Autowired
    private BookingDAO bookingDAO;

    Predicate<Map<?, ?>> datesAreImpossible = (map) ->{

        LocalDateTime checkindate = (LocalDateTime)map.get("check_in_date");
        LocalDate checkoutdate = (LocalDate) map.get("check_out_date");

        return checkindate
                .isBefore(LocalDateTime.now()) || // TODO: incluír intervalo de tolerancia?
                checkoutdate
                        .isBefore(checkindate.toLocalDate());
    };

    BiPredicate<Map<?, ?>, EntityResult> datesOverlapForRoom = (bookingMap, bookingEntityResult) ->
            bookingMap.getRoomId() == bookingEntityResult.getRoomId() &&
                    (
                            bookingMap
                                    .getCheckInDate()
                                    .isAfter(bookingEntityResult.getCheckInDate()) ||
                                    bookingMap
                                            .getCheckInDate()
                                            .toLocalDate()
                                            .isEqual(bookingEntityResult.getCheckInDate().toLocalDate())
                    ) &&
                    (
                            bookingMap
                                    .getCheckOutDate()
                                    .isBefore(bookingEntityResult.getCheckOutDate()) ||
                                    bookingMap
                                            .getCheckOutDate()
                                            .isEqual(bookingEntityResult.getCheckOutDate())
                    );

    @Override
    public EntityResult bookingQuery(Map<?, ?> keymap, List<?> attrList) {
        return this.daoHelper.query(this.bookingDAO, keymap, attrList);
    }

    @Override
    public EntityResult bookingInsert(Map<?, ?> attrMap) {




        return this.daoHelper.insert(this.bookingDAO, attrMap);
    }

    @Override
    public EntityResult bookingUpdate(Map<?, ?> attrMap, Map<?, ?> keyMap) {
        return this.daoHelper.update(this.bookingDAO,attrMap, keyMap);
    }
}
