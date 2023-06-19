package com.ontimize.hr.model.core;

import com.ontimize.hr.api.core.service.IBookingService;
import com.ontimize.hr.api.core.service.IUserService;
import com.ontimize.hr.model.core.dao.BookingDAO;
import com.ontimize.hr.model.core.dao.HotelDAO;
import com.ontimize.hr.model.core.dao.UserDAO;
import com.ontimize.hr.model.core.service.UserService;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.common.dto.EntityResultMapImpl;
import com.ontimize.jee.server.dao.DefaultOntimizeDaoHelper;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
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

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
public class UserServiceTest {


    @InjectMocks
    UserService userService;
    @Mock
    DefaultOntimizeDaoHelper daoHelper;
    @Mock
    UserDAO userDAO;
    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class insertUser{

        Map<Object,Object>attrMap = new HashMap<>();
        @Test
        void insertUser_validUser_userIsSaved(){

            attrMap.put(UserDAO.USER_NAME,"Manolo");
            attrMap.put(UserDAO.USER_PASSWORD,"Pass123");
            attrMap.put(UserDAO.COUNTRY_ID,1);
            attrMap.put(UserDAO.SURNAME1,"Garcia");
            attrMap.put(UserDAO.ID_DOCUMENT,"66955662V");
            attrMap.put(UserDAO.PHONE_NUMBER,"666666666");
            attrMap.put(UserDAO.SURNAME2,"Martinez");
            attrMap.put(UserDAO.EMAIL,"manolo.martinez@mymail.com");
            attrMap.put(UserDAO.LOGIN_NAME,"manager");

            assertDoesNotThrow(()->userService.userInsert(attrMap));

        }

        @Test
        void insertUser_invalidUser_invalidDNI(){

            attrMap.put(UserDAO.USER_NAME,"Manolo");
            attrMap.put(UserDAO.USER_PASSWORD,"Pass123");
            attrMap.put(UserDAO.COUNTRY_ID,1);
            attrMap.put(UserDAO.SURNAME1,"Garcia");
            attrMap.put(UserDAO.ID_DOCUMENT,"66955662W");
            attrMap.put(UserDAO.PHONE_NUMBER,"666666666");
            attrMap.put(UserDAO.SURNAME2,"Martinez");
            attrMap.put(UserDAO.EMAIL,"manolo.martinez@mymail.com");
            attrMap.put(UserDAO.LOGIN_NAME,"manager");

            EntityResult actualResult = userService.userInsert(attrMap);

            assertEquals(EntityResult.OPERATION_WRONG, actualResult.getCode());
        }

        @ParameterizedTest
        @ValueSource(strings = {"12341234", "holaquetal", "Hola1", "hola1234", "HOLA1234"})
        void insertUser_invalidPasswords_resultIsError(String password) {
            attrMap.put(UserDAO.USER_NAME,"Manolo");
            attrMap.put(UserDAO.USER_PASSWORD, password);
            attrMap.put(UserDAO.COUNTRY_ID,1);
            attrMap.put(UserDAO.SURNAME1,"Garcia");
            attrMap.put(UserDAO.ID_DOCUMENT,"66955662V");
            attrMap.put(UserDAO.PHONE_NUMBER,"666666666");
            attrMap.put(UserDAO.SURNAME2,"Martinez");
            attrMap.put(UserDAO.EMAIL,"manolo.martinez@mymail.com");
            attrMap.put(UserDAO.LOGIN_NAME,"manager");

            EntityResult actualResult = userService.userInsert(attrMap);

            assertEquals(EntityResult.OPERATION_WRONG, actualResult.getCode());
        }

    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class updateUser{
        Map<Object, Object> attrMap = new HashMap<>();
        Map<Object,Object> keyMap= new HashMap<>();
        @Test
        void updateUser_validUser_userIsUpdated(){
            EntityResult userEntityResult = new EntityResultMapImpl();

            keyMap.put(UserDAO.LOGIN_NAME,"manager");
            attrMap.put(UserDAO.USER_PASSWORD,"Pass123");
            attrMap.put(UserDAO.SURNAME1,"Garcia");
            attrMap.put(UserDAO.ID_DOCUMENT,"66955662V");
            attrMap.put(UserDAO.LOGIN_NAME, "manager");

            userEntityResult.put(UserDAO.USER_PASSWORD,"Pass1234");
            userEntityResult.put(UserDAO.SURNAME1,"Garcia");
            userEntityResult.put(UserDAO.LOGIN_NAME, "manager");
            userEntityResult.put(UserDAO.ID_DOCUMENT,"66955662V");

            assertDoesNotThrow(() -> userService.userUpdate(attrMap,keyMap));

        }

        @Test
        void updateUser_invalidPass_errorUser(){
            EntityResult userEntityResult = new EntityResultMapImpl();

            keyMap.put(UserDAO.LOGIN_NAME,"manager");
            attrMap.put(UserDAO.USER_PASSWORD,"Pass123");
            attrMap.put(UserDAO.SURNAME1,"Garcia");
            attrMap.put(UserDAO.ID_DOCUMENT,"66955662V");
            attrMap.put(UserDAO.LOGIN_NAME, "manager");

            userEntityResult.put(UserDAO.USER_PASSWORD,"pass1234");
            userEntityResult.put(UserDAO.SURNAME1,"Garcia");
            userEntityResult.put(UserDAO.LOGIN_NAME, "manager");
            userEntityResult.put(UserDAO.ID_DOCUMENT,"66955662V");

            assertThrows(Exception.class()->userService.userUpdate(attrMap,keyMap));
        }
    }
}
