package com.ontimize.hr.model.core;

import com.ontimize.hr.api.core.service.IUserService;
import com.ontimize.hr.api.core.service.exception.UserDoesNotExistException;
import com.ontimize.hr.model.core.dao.EmployeesEntryDepartureDAO;
import com.ontimize.hr.model.core.dao.RoomDAO;
import com.ontimize.hr.model.core.dao.UserDAO;
import com.ontimize.hr.model.core.dao.UserRoleDAO;
import com.ontimize.hr.model.core.service.UserService;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.common.dto.EntityResultMapImpl;
import com.ontimize.jee.server.dao.DefaultOntimizeDaoHelper;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

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
    class insertUser {

        Map<Object, Object> attrMap = new HashMap<>();

        @ParameterizedTest
        @ValueSource(strings = {"X0488209B", "66955662V", "Z1197337P", "Y3924252Y"})
        void insertUser_validUser_operationSuccessful(String idDocument) {
            attrMap.put(UserDAO.ROLE_IDS, List.of(6));
            attrMap.put(UserDAO.USER_NAME, "Manolo");
            attrMap.put(UserDAO.USER_PASSWORD, "Pass1234");
            attrMap.put(UserDAO.COUNTRY_ID, 1);
            attrMap.put(UserDAO.SURNAME1, "Garcia");
            attrMap.put(UserDAO.ID_DOCUMENT, idDocument);
            attrMap.put(UserDAO.PHONE_NUMBER, "666666666");
            attrMap.put(UserDAO.SURNAME2, "Martinez");
            attrMap.put(UserDAO.EMAIL, "manolo.martinez@mymail.com");
            attrMap.put(UserDAO.LOGIN_NAME, "SoyManolo");

            EntityResult insertResult = new EntityResultMapImpl();
            insertResult.setCode(EntityResult.OPERATION_SUCCESSFUL_SHOW_MESSAGE);
            insertResult.setMessage(IUserService.USER_INSERT_SUCCESS);

            when(daoHelper.insert(any(), any())).thenReturn(insertResult);

            EntityResult result = userService.userInsert(attrMap);

            assertEquals(EntityResult.OPERATION_SUCCESSFUL_SHOW_MESSAGE, result.getCode());
            assertEquals(IUserService.USER_INSERT_SUCCESS, result.getMessage());
        }

        @Test
        void insertUser_invalidIdDocument_operationFailure() {

            attrMap.put(UserDAO.USER_NAME, "Manolo");
            attrMap.put(UserDAO.USER_PASSWORD, "Pass1234");
            attrMap.put(UserDAO.COUNTRY_ID, 1);
            attrMap.put(UserDAO.SURNAME1, "Garcia");
            attrMap.put(UserDAO.ID_DOCUMENT, "66955662W");
            attrMap.put(UserDAO.PHONE_NUMBER, "666666666");
            attrMap.put(UserDAO.SURNAME2, "Martinez");
            attrMap.put(UserDAO.EMAIL, "manolo.martinez@mymail.com");
            attrMap.put(UserDAO.LOGIN_NAME, "SoyManolo");

            EntityResult actualResult = userService.userInsert(attrMap);

            assertEquals(EntityResult.OPERATION_WRONG, actualResult.getCode());
            assertEquals(IUserService.ERR_INVALID_ID_DOCUMENT, actualResult.getMessage());
        }

        @ParameterizedTest
        @ValueSource(strings = {"12341234", "holaquetal", "Hola1", "hola1234", "HOLA1234"})
        void insertUser_invalidPassword_resultIsError(String password) {
            attrMap.put(UserDAO.USER_NAME, "Manolo");
            attrMap.put(UserDAO.USER_PASSWORD, password);
            attrMap.put(UserDAO.COUNTRY_ID, 1);
            attrMap.put(UserDAO.SURNAME1, "Garcia");
            attrMap.put(UserDAO.ID_DOCUMENT, "66955662V");
            attrMap.put(UserDAO.PHONE_NUMBER, "666666666");
            attrMap.put(UserDAO.SURNAME2, "Martinez");
            attrMap.put(UserDAO.EMAIL, "manolo.martinez@mymail.com");
            attrMap.put(UserDAO.ROLE_NAME, RoleNames.CLIENT);
            attrMap.put(UserDAO.LOGIN_NAME, "SoyManolo");

            EntityResult actualResult = userService.userInsert(attrMap);

            assertEquals(EntityResult.OPERATION_WRONG, actualResult.getCode());
        }

        @Test
        void insertUser_invalidCountryId_operationFailure() {
            attrMap.put(UserDAO.USER_NAME, "Manolo");
            attrMap.put(UserDAO.USER_PASSWORD, "Pass1234");
            attrMap.put(UserDAO.COUNTRY_ID, "invalid");
            attrMap.put(UserDAO.SURNAME1, "Garcia");
            attrMap.put(UserDAO.ID_DOCUMENT, "66955662V");
            attrMap.put(UserDAO.PHONE_NUMBER, "666666666");
            attrMap.put(UserDAO.SURNAME2, "Martinez");
            attrMap.put(UserDAO.EMAIL, "manolo.martinez@mymail.com");
            attrMap.put(UserDAO.ROLE_NAME, RoleNames.CLIENT);
            attrMap.put(UserDAO.LOGIN_NAME, "SoyManolo");

            EntityResult result = userService.userInsert(attrMap);

            assertEquals(EntityResult.OPERATION_WRONG, result.getCode());
            assertEquals(IUserService.ERR_INVALID_COUNTRY_ID, result.getMessage());
        }

        @Test
        void insertUser_invalidPhoneNumber_operationFailure() {
            attrMap.put(UserDAO.USER_NAME, "Manolo");
            attrMap.put(UserDAO.USER_PASSWORD, "Pass1234");
            attrMap.put(UserDAO.COUNTRY_ID, 1);
            attrMap.put(UserDAO.SURNAME1, "Garcia");
            attrMap.put(UserDAO.ID_DOCUMENT, "66955662V");
            attrMap.put(UserDAO.PHONE_NUMBER, 0xFA1L);
            attrMap.put(UserDAO.SURNAME2, "Martinez");
            attrMap.put(UserDAO.EMAIL, "manolo.martinez@mymail.com");
            attrMap.put(UserDAO.ROLE_NAME, RoleNames.CLIENT);
            attrMap.put(UserDAO.LOGIN_NAME, "SoyManolo");

            EntityResult result = userService.userInsert(attrMap);

            assertEquals(EntityResult.OPERATION_WRONG, result.getCode());
            assertEquals(IUserService.ERR_INVALID_PHONE_NUMBER, result.getMessage());
        }

        @Test
        void insertUser_invalidEmailFormat_operationFailure() {
            attrMap.put(UserDAO.USER_NAME, "Manolo");
            attrMap.put(UserDAO.USER_PASSWORD, "Pass1234");
            attrMap.put(UserDAO.COUNTRY_ID, 1);
            attrMap.put(UserDAO.SURNAME1, "Garcia");
            attrMap.put(UserDAO.ID_DOCUMENT, "66955662V");
            attrMap.put(UserDAO.PHONE_NUMBER, "666555444");
            attrMap.put(UserDAO.SURNAME2, "Martinez");
            attrMap.put(UserDAO.EMAIL, "manolo.martinezmymail.com");
            attrMap.put(UserDAO.ROLE_NAME, RoleNames.CLIENT);
            attrMap.put(UserDAO.LOGIN_NAME, "SoyManolo");

            EntityResult result = userService.userInsert(attrMap);

            assertEquals(EntityResult.OPERATION_WRONG, result.getCode());
            assertEquals(IUserService.ERR_INVALID_EMAIL, result.getMessage());
        }

        @Test
        void insertUser_invalidEmailType_operationFailure() {
            attrMap.put(UserDAO.USER_NAME, "Manolo");
            attrMap.put(UserDAO.USER_PASSWORD, "Pass1234");
            attrMap.put(UserDAO.COUNTRY_ID, 1);
            attrMap.put(UserDAO.SURNAME1, "Garcia");
            attrMap.put(UserDAO.ID_DOCUMENT, "66955662V");
            attrMap.put(UserDAO.PHONE_NUMBER, "666555444");
            attrMap.put(UserDAO.SURNAME2, "Martinez");
            attrMap.put(UserDAO.EMAIL, 0xFA1L);
            attrMap.put(UserDAO.ROLE_NAME, RoleNames.CLIENT);
            attrMap.put(UserDAO.LOGIN_NAME, "SoyManolo");

            EntityResult result = userService.userInsert(attrMap);

            assertEquals(EntityResult.OPERATION_WRONG, result.getCode());
            assertEquals(IUserService.ERR_INVALID_EMAIL, result.getMessage());
        }

    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class deleteUser {

        Map<Object, Object> keyMap = new HashMap<>();

        @Test
        void deleteUser_validUser_userIsDeleted() {
            keyMap.put(UserDAO.LOGIN_NAME, "empleado1");

            EntityResult deleteEntityResult = new EntityResultMapImpl();
            deleteEntityResult.setMessage(IUserService.DELETION_SUCCESS);
            deleteEntityResult.setCode(EntityResult.OPERATION_SUCCESSFUL_SHOW_MESSAGE);

            EntityResult queryEntityResult = new EntityResultMapImpl();
            queryEntityResult.addRecord(keyMap);

            when(daoHelper.query(any(), any(), any())).thenReturn(queryEntityResult);
            when(daoHelper.delete(userDAO, keyMap)).thenReturn(deleteEntityResult);

            EntityResult result = userService.userDelete(keyMap);
            //assertEquals(EntityResult.OPERATION_SUCCESSFUL_SHOW_MESSAGE, result.getCode());
            assertEquals(IUserService.DELETION_SUCCESS, result.getMessage());
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
            attrMap.put(UserDAO.ID_DOCUMENT, "X0488209B");
            attrMap.put(UserDAO.LOGIN_NAME, "manager");

            userEntityResult.put(UserDAO.USER_PASSWORD, "Pass1234");
            userEntityResult.put(UserDAO.SURNAME1, "Garcia");
            userEntityResult.put(UserDAO.LOGIN_NAME, "manager");
            userEntityResult.put(UserDAO.ID_DOCUMENT, "66955662V");

            // TODO: correct this
            assertDoesNotThrow(() -> userService.userUpdate(attrMap, keyMap));
        }
    }

    @Nested
    class GetUserRoles {

        private final String loginName = "login name";

        @Test
        void existingUser_operationSuccess() {
            EntityResult queryResult = new EntityResultMapImpl();
            queryResult.put(UserRoleDAO.NAME, List.of(RoleNames.MANAGER));

            when(daoHelper.query(any(), any(), any(), anyString())).thenReturn(queryResult);

            assertDoesNotThrow(() -> userService.getUserRoles(loginName));
        }

        @Test
        void getUserRoles_nonExistingUser_operationFailure() {
            EntityResult queryResult = new EntityResultMapImpl();

            when(daoHelper.query(any(), any(), any(), anyString())).thenReturn(queryResult);

            assertThrows(UserDoesNotExistException.class, () -> userService.getUserRoles(loginName));
        }

    }

}
