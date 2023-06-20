package com.ontimize.hr.model.core;

import com.ontimize.hr.model.core.dao.UserDAO;
import com.ontimize.hr.model.core.service.UserService;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.common.dto.EntityResultMapImpl;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
public class UserServiceTest {


    @InjectMocks
    UserService userService;

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class insertUser {

        Map<Object, Object> attrMap = new HashMap<>();

        @Test
        void insertUser_validUser_userIsSaved() {

            attrMap.put(UserDAO.USER_NAME, "Manolo");
            attrMap.put(UserDAO.USER_PASSWORD, "Pass123");
            attrMap.put(UserDAO.COUNTRY_ID, 1);
            attrMap.put(UserDAO.SURNAME1, "Garcia");
            attrMap.put(UserDAO.ID_DOCUMENT, "66955662V");
            attrMap.put(UserDAO.PHONE_NUMBER, "666666666");
            attrMap.put(UserDAO.SURNAME2, "Martinez");
            attrMap.put(UserDAO.EMAIL, "manolo.martinez@mymail.com");
            attrMap.put(UserDAO.LOGIN_NAME, "SoyManolo");

            assertDoesNotThrow(() -> userService.userInsert(attrMap));

        }

        @Test
        void insertUser_invalidUser_invalidDNI() {

            attrMap.put(UserDAO.USER_NAME, "Manolo");
            attrMap.put(UserDAO.USER_PASSWORD, "Pass123");
            attrMap.put(UserDAO.COUNTRY_ID, 1);
            attrMap.put(UserDAO.SURNAME1, "Garcia");
            attrMap.put(UserDAO.ID_DOCUMENT, "66955662W");
            attrMap.put(UserDAO.PHONE_NUMBER, "666666666");
            attrMap.put(UserDAO.SURNAME2, "Martinez");
            attrMap.put(UserDAO.EMAIL, "manolo.martinez@mymail.com");
            attrMap.put(UserDAO.LOGIN_NAME, "SoyManolo");

            EntityResult actualResult = userService.userInsert(attrMap);

            assertEquals(EntityResult.OPERATION_WRONG, actualResult.getCode());
        }

        @ParameterizedTest
        @ValueSource(strings = {"12341234", "holaquetal", "Hola1", "hola1234", "HOLA1234"})
        void insertUser_invalidPasswords_resultIsError(String password) {
            attrMap.put(UserDAO.USER_NAME, "Manolo");
            attrMap.put(UserDAO.USER_PASSWORD, password);
            attrMap.put(UserDAO.COUNTRY_ID, 1);
            attrMap.put(UserDAO.SURNAME1, "Garcia");
            attrMap.put(UserDAO.ID_DOCUMENT, "66955662V");
            attrMap.put(UserDAO.PHONE_NUMBER, "666666666");
            attrMap.put(UserDAO.SURNAME2, "Martinez");
            attrMap.put(UserDAO.EMAIL, "manolo.martinez@mymail.com");
            attrMap.put(UserDAO.ROLE_NAME, NameRoles.ClIENT);
            attrMap.put(UserDAO.LOGIN_NAME, "SoyManolo");

            EntityResult actualResult = userService.userInsert(attrMap);

            assertEquals(EntityResult.OPERATION_WRONG, actualResult.getCode());
        }

    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class updateUser {
        Map<Object, Object> attrMap = new HashMap<>();
        Map<Object, Object> keyMap = new HashMap<>();

        @Test
        void updateUser_validUser_userIsUpdated() {
            EntityResult userEntityResult = new EntityResultMapImpl();

            keyMap.put(UserDAO.LOGIN_NAME, "manager");
            attrMap.put(UserDAO.USER_PASSWORD, "Pass123");
            attrMap.put(UserDAO.SURNAME1, "Garcia");
            attrMap.put(UserDAO.ID_DOCUMENT, "66955662V");
            attrMap.put(UserDAO.LOGIN_NAME, "manager");

            userEntityResult.put(UserDAO.USER_PASSWORD, "Pass1234");
            userEntityResult.put(UserDAO.SURNAME1, "Garcia");
            userEntityResult.put(UserDAO.LOGIN_NAME, "manager");
            userEntityResult.put(UserDAO.ID_DOCUMENT, "66955662V");

            assertDoesNotThrow(() -> userService.userUpdate(attrMap, keyMap));

        }
    }
}
