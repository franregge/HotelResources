package com.ontimize.hr.model.core;

import com.ontimize.hr.api.core.service.exception.InvalidBookingDNIException;
import com.ontimize.hr.api.core.service.exception.InvalidBookingDateException;
import com.ontimize.hr.model.core.dao.BookingDAO;
import com.ontimize.hr.model.core.service.BookingService;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.server.dao.DefaultOntimizeDaoHelper;
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
import java.util.function.BiPredicate;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.*;

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
    public class insertBooking_validBooking_bookingIsSaved{


    }

}
