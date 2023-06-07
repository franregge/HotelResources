package com.ontimize.hr.model.core;

import com.ontimize.hr.api.core.service.IBookingService;
import com.ontimize.hr.model.core.dao.BookingDAO;
import com.ontimize.hr.model.core.dao.UserDAO;
import com.ontimize.hr.model.core.service.BookingService;
import com.ontimize.hr.model.core.service.UserService;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.common.dto.EntityResultMapImpl;
import com.ontimize.jee.server.dao.DefaultOntimizeDaoHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Date;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {

    @InjectMocks
    BookingService bookingService;

    @Mock
    DefaultOntimizeDaoHelper daoHelper;

    @Mock
    BookingDAO bookingDAO;
    @Mock
    UserService userService;

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class insertBooking {
        Map<Object, Object> attrMap = new HashMap<>();

        @Test
        void insertBooking_validBooking_bookingIsSaved_Test() {
            attrMap.put(BookingDAO.ARRIVAL_DATE, LocalDate.now().plusDays(1));
            attrMap.put(BookingDAO.DEPARTURE_DATE, LocalDate.now().plusDays(5));
            attrMap.put(BookingDAO.USER_ID,2);
            attrMap.put(BookingDAO.ROOM_ID, 6);

            assertDoesNotThrow(() -> bookingService.bookingInsert(attrMap));
        }


        @Test
        void insertBooking_invalidBooking_arrivalDateBeforeNow() {
            attrMap.put(BookingDAO.ARRIVAL_DATE, LocalDate.now().minusDays(1).toString());
            attrMap.put(BookingDAO.DEPARTURE_DATE, LocalDate.now().plusDays(5));
            attrMap.put(BookingDAO.USER_ID,1);
            attrMap.put(BookingDAO.ROOM_ID, 6);

            EntityResult er =new EntityResultMapImpl();
            er.put("",List.of(""));

            when(userService.userQuery(any(),any())).thenReturn(er);

            EntityResult actualResult = bookingService.bookingInsert(attrMap);

            assertEquals(IBookingService.DATE_BEFORE_NOW_MESSAGE, actualResult.getMessage());
            assertEquals(EntityResult.OPERATION_WRONG, actualResult.getCode());
        }

        @Test
        void insertBooking_invalidBooking_arrivalDateAfterDepartureDate() {
            attrMap.put(BookingDAO.ARRIVAL_DATE, LocalDate.now().plusDays(3).toString());
            attrMap.put(BookingDAO.DEPARTURE_DATE, LocalDate.now().plusDays(1).toString());
            attrMap.put(BookingDAO.USER_ID,2);
            attrMap.put(BookingDAO.ROOM_ID, 6);

            EntityResult er =new EntityResultMapImpl();
            er.put("",List.of(""));

            when(userService.userQuery(any(),any())).thenReturn(er);

            EntityResult actualResult = bookingService.bookingInsert(attrMap);

            assertEquals(IBookingService.ARRIVAL_DATE_AFTER_DEPARTURE_DATE_MESSAGE, actualResult.getMessage());
            assertEquals(EntityResult.OPERATION_WRONG, actualResult.getCode());
        }

        @Test
        void insertBooking_invalidBooking_datesOverlap() {
            EntityResult conflictingEntityResult = new EntityResultMapImpl();

            attrMap.put(BookingDAO.ARRIVAL_DATE, LocalDate.now().plusDays(1).toString());
            attrMap.put(BookingDAO.DEPARTURE_DATE, LocalDate.now().plusDays(5).toString());
            attrMap.put(BookingDAO.USER_ID,2);
            attrMap.put(BookingDAO.ROOM_ID, 6);

            Instant conflictingArrivalDate = Instant.now().plus(1L, ChronoUnit.DAYS);
            Instant conflictingDepartureDate = Instant.now().plus(5L, ChronoUnit.DAYS);
            conflictingEntityResult.put(BookingDAO.ARRIVAL_DATE, List.of(new Date(conflictingArrivalDate.toEpochMilli())));
            conflictingEntityResult.put(BookingDAO.DEPARTURE_DATE, List.of(new Date(conflictingDepartureDate.toEpochMilli())));
            conflictingEntityResult.put(BookingDAO.USER_ID, List.of(2));
            conflictingEntityResult.put(BookingDAO.ROOM_ID, List.of(6));

            EntityResult er = new EntityResultMapImpl();
            er.put("",List.of(""));

            when(daoHelper.query(any(), any(), any())).thenReturn(conflictingEntityResult);
            when(userService.userQuery(any(),any())).thenReturn(er);

            EntityResult actualResult = bookingService.bookingInsert(attrMap);

            assertEquals(IBookingService.DATES_OVERLAP, actualResult.getMessage());
            assertEquals(EntityResult.OPERATION_WRONG, actualResult.getCode());
        }
        @Test
        void insertBooking_invalidBooking_userDoesNotExist(){
            EntityResult conflictingEntityResult = new EntityResultMapImpl();

            attrMap.put(BookingDAO.ARRIVAL_DATE, LocalDate.now().plusDays(1).toString());
            attrMap.put(BookingDAO.DEPARTURE_DATE, LocalDate.now().plusDays(5).toString());
            attrMap.put(BookingDAO.USER_ID,2);
            attrMap.put(BookingDAO.ROOM_ID, 6);

            when(userService.userQuery(any(),any())).thenReturn(conflictingEntityResult);


            EntityResult actualResult = bookingService.bookingInsert(attrMap);

            assertEquals(IBookingService.USER_NOT_FOUND, actualResult.getMessage());
            assertEquals(EntityResult.OPERATION_WRONG, actualResult.getCode());

        }

    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class updateBooking {
        Map<Object, Object> attrMap = new HashMap<>();
        Map<Object,Object> keyMap= new HashMap<>();

        @Test
        void updateBooking_validBooking_bookingIsUpdated_Test() {

            EntityResult bookingEntityResult = new EntityResultMapImpl();

            keyMap.put(BookingDAO.ID,1);
            attrMap.put(BookingDAO.ARRIVAL_DATE, LocalDate.now().plusDays(1));
            attrMap.put(BookingDAO.DEPARTURE_DATE, LocalDate.now().plusDays(5));
            attrMap.put(BookingDAO.USER_ID,2);
            attrMap.put(BookingDAO.ROOM_ID, 6);

            Instant conflictingArrivalDate = Instant.now().plus(1L, ChronoUnit.DAYS);
            Instant conflictingDepartureDate = Instant.now().plus(5L, ChronoUnit.DAYS);
            bookingEntityResult.put(BookingDAO.ARRIVAL_DATE, List.of(new Date(conflictingArrivalDate.toEpochMilli())));
            bookingEntityResult.put(BookingDAO.DEPARTURE_DATE, List.of(new Date(conflictingDepartureDate.toEpochMilli())));
            bookingEntityResult.put(BookingDAO.USER_ID, List.of(2));
            bookingEntityResult.put(BookingDAO.ROOM_ID, List.of(6));



            assertDoesNotThrow(() -> bookingService.bookingUpdate(attrMap,keyMap));

    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class deleteBooking {

        private Map<? super Object, ? super Object> keyMap;

        @BeforeEach
        void init() {
            keyMap = new HashMap<>();
        }

        @Test
        void existingBooking_resultIsSuccess() {
            keyMap.put(BookingDAO.ID, 1);
            EntityResult queryResult = new EntityResultMapImpl();
            queryResult.put("test", List.of("test"));
            EntityResult deleteResult = new EntityResultMapImpl();
            deleteResult.setCode(EntityResult.OPERATION_SUCCESSFUL);

            when(daoHelper.query(any(), any(), any())).thenReturn(queryResult);
            when(daoHelper.delete(any(), any())).thenReturn(deleteResult);

            EntityResult result = bookingService.bookingDelete(keyMap);

            assertEquals(IBookingService.DELETION_SUCCESS, result.getMessage());
            assertEquals(EntityResult.OPERATION_SUCCESSFUL_SHOW_MESSAGE, result.getCode());
        }

        @Test
        void nonexistentBooking_resultIsError() {
            keyMap.put(BookingDAO.ID, 1);
            EntityResult queryResult = new EntityResultMapImpl();
            EntityResult deleteResult = new EntityResultMapImpl();
            deleteResult.setCode(EntityResult.OPERATION_SUCCESSFUL);

            when(daoHelper.query(any(),any(),any())).thenReturn(queryResult);

            EntityResult result = bookingService.bookingDelete(keyMap);

            assertEquals(BookingService.NO_BOOKING_WITH_ID, result.getMessage());
            assertEquals(EntityResult.OPERATION_WRONG, result.getCode());
        }

    }

}
}
