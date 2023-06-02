package com.ontimize.hr.model.core;

import com.ontimize.hr.api.core.service.IBookingService;
import com.ontimize.hr.model.core.dao.BookingDAO;
import com.ontimize.hr.model.core.service.BookingService;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.common.dto.EntityResultMapImpl;
import com.ontimize.jee.server.dao.DefaultOntimizeDaoHelper;
import org.apache.poi.xwpf.usermodel.XWPFHeader;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class bookingServiceTest {

    @InjectMocks
    BookingService bookingService;

    @Mock
    DefaultOntimizeDaoHelper daoHelper;

    @Mock
    BookingDAO bookingDAO;

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    public class insertBooking_Test{
        Map<Object,Object>attrMap = new HashMap<>();
        @Test
        public void insertBooking_validBooking_bookingIsSaved_Test(){
            attrMap.put(BookingDAO.ARRIVAL_DATE,LocalDate.now().plusDays(1));
            attrMap.put(BookingDAO.DEPARTURE_DATE,LocalDate.now().plusDays(5));
            attrMap.put(BookingDAO.DNI,"66955662V");
            attrMap.put(BookingDAO.ROOM_ID,6);
            attrMap.put(BookingDAO.NAME,"Manolo");
            attrMap.put(BookingDAO.SURNAME1,"Garcia");



            assertDoesNotThrow(()->bookingService.bookingInsert(attrMap));

        }
        @Test
        public void insertBooking_invalidBooking_wrongDNI_Test(){
            attrMap.put(BookingDAO.ARRIVAL_DATE,LocalDate.now().plusDays(1));
            attrMap.put(BookingDAO.DEPARTURE_DATE,LocalDate.now().plusDays(5));
            attrMap.put(BookingDAO.DNI,"66955662W");
            attrMap.put(BookingDAO.ROOM_ID,6);
            attrMap.put(BookingDAO.NAME,"Manolo");
            attrMap.put(BookingDAO.SURNAME1,"Garcia");


            EntityResult actualResult= bookingService.bookingInsert(attrMap);



            assertEquals(EntityResult.OPERATION_WRONG,actualResult.getCode());

        }

        @Test
        public void insertBooking_invalidBooking_arrivalDateBeforeNow(){
            attrMap.put(BookingDAO.ARRIVAL_DATE,LocalDate.now().minusDays(1).toString());
            attrMap.put(BookingDAO.DEPARTURE_DATE,LocalDate.now().plusDays(5));
            attrMap.put(BookingDAO.DNI,"66955662V");
            attrMap.put(BookingDAO.ROOM_ID,6);
            attrMap.put(BookingDAO.NAME,"Manolo");
            attrMap.put(BookingDAO.SURNAME1,"Garcia");

            EntityResult actualResult= bookingService.bookingInsert(attrMap);

            assertEquals(IBookingService.DATE_BEFORE_NOW_MESSAGE,actualResult.getMessage());
            assertEquals(EntityResult.OPERATION_WRONG,actualResult.getCode());
        }
        @Test
        public void insertBooking_invalidBooking_arrivalDateAfterDepartureDate(){
            attrMap.put(BookingDAO.ARRIVAL_DATE,LocalDate.now().plusDays(3).toString());
            attrMap.put(BookingDAO.DEPARTURE_DATE,LocalDate.now().plusDays(1).toString());
            attrMap.put(BookingDAO.DNI,"66955662V");
            attrMap.put(BookingDAO.ROOM_ID,6);
            attrMap.put(BookingDAO.NAME,"Manolo");
            attrMap.put(BookingDAO.SURNAME1,"Garcia");

            EntityResult actualResult= bookingService.bookingInsert(attrMap);

            assertEquals(IBookingService.ARRIVAL_DATE_AFTER_DEPARTURE_DATE_MESSAGE,actualResult.getMessage());
            assertEquals(EntityResult.OPERATION_WRONG,actualResult.getCode());
        }
        @Test
        public void insertBooking_invalidBooking_datesOverlap(){
            Map<Object,Object>attrMap2 = new HashMap<>();


            attrMap.put(BookingDAO.ARRIVAL_DATE,LocalDate.now().plusDays(1));
            attrMap.put(BookingDAO.DEPARTURE_DATE,LocalDate.now().plusDays(5));
            attrMap.put(BookingDAO.DNI,"66955662V");
            attrMap.put(BookingDAO.ROOM_ID,6);
            attrMap.put(BookingDAO.NAME,"Manolo");
            attrMap.put(BookingDAO.SURNAME1,"Garcia");

            attrMap2.put(BookingDAO.ARRIVAL_DATE,LocalDate.now().plusDays(1));
            attrMap2.put(BookingDAO.DEPARTURE_DATE,LocalDate.now().plusDays(5));
            attrMap2.put(BookingDAO.DNI,"66955662V");
            attrMap2.put(BookingDAO.ROOM_ID,6);
            attrMap2.put(BookingDAO.NAME,"Manolo");
            attrMap2.put(BookingDAO.SURNAME1,"Garcia");

             when(daoHelper.query())

        }

    }
    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    public class updateBooking_Test{


    }

}
