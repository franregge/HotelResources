package com.ontimize.hr.model.core;

import com.ontimize.hr.api.core.service.IBookingService;
import com.ontimize.hr.model.core.dao.BookingDAO;
import com.ontimize.hr.model.core.service.BookingService;
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

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class insertBooking {
        Map<Object, Object> attrMap = new HashMap<>();

        @Test
        void insertBooking_validBooking_bookingIsSaved_Test() {
            attrMap.put(BookingDAO.ARRIVAL_DATE, LocalDate.now().plusDays(1));
            attrMap.put(BookingDAO.DEPARTURE_DATE, LocalDate.now().plusDays(5));
            attrMap.put(BookingDAO.DNI, "66955662V");
            attrMap.put(BookingDAO.ROOM_ID, 6);
            attrMap.put(BookingDAO.NAME, "Manolo");
            attrMap.put(BookingDAO.SURNAME1, "Garcia");

            assertDoesNotThrow(() -> bookingService.bookingInsert(attrMap));
        }

        @Test
        void insertBooking_invalidBooking_wrongDNI_Test() {
            attrMap.put(BookingDAO.ARRIVAL_DATE, LocalDate.now().plusDays(1));
            attrMap.put(BookingDAO.DEPARTURE_DATE, LocalDate.now().plusDays(5));
            attrMap.put(BookingDAO.DNI, "66955662W");
            attrMap.put(BookingDAO.ROOM_ID, 6);
            attrMap.put(BookingDAO.NAME, "Manolo");
            attrMap.put(BookingDAO.SURNAME1, "Garcia");

            EntityResult actualResult = bookingService.bookingInsert(attrMap);

            assertEquals(EntityResult.OPERATION_WRONG, actualResult.getCode());
        }

        @Test
        void insertBooking_invalidBooking_arrivalDateBeforeNow() {
            attrMap.put(BookingDAO.ARRIVAL_DATE, LocalDate.now().minusDays(1).toString());
            attrMap.put(BookingDAO.DEPARTURE_DATE, LocalDate.now().plusDays(5));
            attrMap.put(BookingDAO.DNI, "66955662V");
            attrMap.put(BookingDAO.ROOM_ID, 6);
            attrMap.put(BookingDAO.NAME, "Manolo");
            attrMap.put(BookingDAO.SURNAME1, "Garcia");

            EntityResult actualResult = bookingService.bookingInsert(attrMap);

            assertEquals(IBookingService.DATE_BEFORE_NOW_MESSAGE, actualResult.getMessage());
            assertEquals(EntityResult.OPERATION_WRONG, actualResult.getCode());
        }

        @Test
        void insertBooking_invalidBooking_arrivalDateAfterDepartureDate() {
            attrMap.put(BookingDAO.ARRIVAL_DATE, LocalDate.now().plusDays(3).toString());
            attrMap.put(BookingDAO.DEPARTURE_DATE, LocalDate.now().plusDays(1).toString());
            attrMap.put(BookingDAO.DNI, "66955662V");
            attrMap.put(BookingDAO.ROOM_ID, 6);
            attrMap.put(BookingDAO.NAME, "Manolo");
            attrMap.put(BookingDAO.SURNAME1, "Garcia");

            EntityResult actualResult = bookingService.bookingInsert(attrMap);

            assertEquals(IBookingService.ARRIVAL_DATE_AFTER_DEPARTURE_DATE_MESSAGE, actualResult.getMessage());
            assertEquals(EntityResult.OPERATION_WRONG, actualResult.getCode());
        }

        @Test
        void insertBooking_invalidBooking_datesOverlap() {
            EntityResult conflictingEntityResult = new EntityResultMapImpl();

            attrMap.put(BookingDAO.ARRIVAL_DATE, LocalDate.now().plusDays(1).toString());
            attrMap.put(BookingDAO.DEPARTURE_DATE, LocalDate.now().plusDays(5).toString());
            attrMap.put(BookingDAO.DNI, "66955662V");
            attrMap.put(BookingDAO.ROOM_ID, 6);
            attrMap.put(BookingDAO.NAME, "Manolo");
            attrMap.put(BookingDAO.SURNAME1, "Garcia");

            Instant conflictingArrivalDate = Instant.now().plus(1L, ChronoUnit.DAYS);
            Instant conflictingDepartureDate = Instant.now().plus(5L, ChronoUnit.DAYS);
            conflictingEntityResult.put(BookingDAO.ARRIVAL_DATE, List.of(new Date(conflictingArrivalDate.toEpochMilli())));
            conflictingEntityResult.put(BookingDAO.DEPARTURE_DATE, List.of(new Date(conflictingDepartureDate.toEpochMilli())));
            conflictingEntityResult.put(BookingDAO.DNI, List.of("66955662V"));
            conflictingEntityResult.put(BookingDAO.ROOM_ID, List.of(6));
            conflictingEntityResult.put(BookingDAO.NAME, List.of("Manolo"));
            conflictingEntityResult.put(BookingDAO.SURNAME1, List.of("Garcia"));

            when(daoHelper.query(any(), any(), any())).thenReturn(conflictingEntityResult);

            EntityResult actualResult = bookingService.bookingInsert(attrMap);

            assertEquals(IBookingService.DATES_OVERLAP, actualResult.getMessage());
            assertEquals(EntityResult.OPERATION_WRONG, actualResult.getCode());
        }

    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class updateBooking {

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
            queryResult.put("test", "test");

            when(daoHelper.query(any(), any(), any())).thenReturn(queryResult);

            EntityResult deleteResult = bookingService.bookingDelete(keyMap);

            assertEquals(BookingService.);
        }

        @Test
        void inexistentBooking_resultIsError() {

        }

    }

}
