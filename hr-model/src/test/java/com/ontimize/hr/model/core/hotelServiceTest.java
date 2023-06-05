package com.ontimize.hr.model.core;

import com.ontimize.hr.api.core.service.IHotelService;
import com.ontimize.hr.model.core.dao.HotelDAO;
import com.ontimize.hr.model.core.service.HotelService;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.common.dto.EntityResultMapImpl;
import com.ontimize.jee.server.dao.DefaultOntimizeDaoHelper;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
@ExtendWith(MockitoExtension.class)
public class hotelServiceTest {
    @InjectMocks
    HotelService hotelService;
    @Mock
    DefaultOntimizeDaoHelper daoHelper;
    @Mock
    HotelDAO hotelDAO;

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class insertHotel {
        Map<Object, Object> attrMap = new HashMap<>();

        @Test
        void insertHotel_validHotel_hotelIsSaved_Test() {
            attrMap.put(HotelDAO.NUMBER_OF_FLOORS, 6);
            attrMap.put(HotelDAO.NAME, "Hotel Estrella");

            assertDoesNotThrow(() -> hotelService.hotelInsert(attrMap));
        }

        @Test
        void insertHotel_invalidHotel_numberOfFloorsOverMaximum_Test() {
            attrMap.put(HotelDAO.NUMBER_OF_FLOORS, 110);
            attrMap.put(HotelDAO.NAME, "Hotel Estrella");

            EntityResult actualResult = null;

            actualResult = hotelService.hotelInsert(attrMap);


            assertEquals(EntityResult.OPERATION_WRONG, actualResult.getCode());
        }

        @Test
        void insertHotel_invalidHotel_numberOfFloorsUnderMinimum() {
            attrMap.put(HotelDAO.NUMBER_OF_FLOORS, -5);
            attrMap.put(HotelDAO.NAME, "Hotel Estrella");

            EntityResult actualResult = null;

            actualResult = hotelService.hotelInsert(attrMap);


            assertEquals(EntityResult.OPERATION_WRONG, actualResult.getCode());
        }


        @Test
        void insertBooking_invalidBooking_datesOverlap() {
            EntityResult conflictingEntityResult = new EntityResultMapImpl();

            attrMap.put(HotelDAO.NUMBER_OF_FLOORS, 5);
            attrMap.put(HotelDAO.NAME, "Hotel Estrella");

            conflictingEntityResult.put(HotelDAO.NUMBER_OF_FLOORS, List.of(5));
            conflictingEntityResult.put(HotelDAO.NAME, List.of("Hotel Estrella"));

            when(daoHelper.query(any(), any(), any())).thenReturn(conflictingEntityResult);

            EntityResult actualResult = hotelService.hotelInsert(attrMap);

            assertEquals(IHotelService.HOTEL_ALREADY_EXISTS_ERROR, actualResult.getMessage());
            assertEquals(EntityResult.OPERATION_WRONG, actualResult.getCode());
        }

    }
    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class updateHotel {
        Map<Object, Object> attrMap = new HashMap<>();


    }
}
