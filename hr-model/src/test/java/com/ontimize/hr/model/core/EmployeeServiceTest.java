package com.ontimize.hr.model.core;

import com.ontimize.hr.api.core.service.IUserService;
import com.ontimize.hr.api.core.service.IEmployeeService;
import com.ontimize.hr.api.core.service.exception.InvalidShiftException;
import com.ontimize.hr.api.core.service.exception.UserDoesNotExistException;
import com.ontimize.hr.model.core.dao.EmployeesEntryDepartureDAO;
import com.ontimize.hr.model.core.dao.UserDAO;
import com.ontimize.hr.model.core.dao.UserRoleDAO;
import com.ontimize.hr.model.core.service.EmployeeService;

import com.ontimize.hr.model.core.service.UserService;
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

import java.sql.Time;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
public class EmployeeServiceTest {

    @InjectMocks
    EmployeeService employeeService;
    @Mock
    UserService userService;
    @Mock
    DefaultOntimizeDaoHelper daoHelper;
    @InjectMocks
    EmployeesEntryDepartureDAO employeesEntryDepartureDAO;

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class updateEmployee {


        Map<Object, Object> keyMap = new HashMap<>();
        Map<Object, Object> attrMap = new HashMap<>();

        @Test
        void updateEmployee_employeeIsUpdated() throws UserDoesNotExistException {

            EntityResult userEntityResult = new EntityResultMapImpl();

            keyMap.put(UserDAO.LOGIN_NAME, "empleado1");
            attrMap.put(UserDAO.USER_PASSWORD, "Hola1234");
            attrMap.put(UserDAO.SURNAME1, "ff");
            attrMap.put(UserDAO.ID_DOCUMENT, "35581834Y");

            userEntityResult.put(UserDAO.USER_PASSWORD, "Hola12345");
            userEntityResult.put(UserDAO.SURNAME1, "ff");
            userEntityResult.put(UserDAO.LOGIN_NAME, "empleado1");
            userEntityResult.put(UserDAO.ID_DOCUMENT, "35581834Y");

            EntityResult er = new EntityResultMapImpl();
            er.setCode(EntityResult.OPERATION_SUCCESSFUL_SHOW_MESSAGE);
            List<String> result = List.of(RoleNames.EMPLOYEE);
            when(userService.getUserRoles(any())).thenReturn(result);
            when(userService.userUpdate(attrMap, keyMap)).thenReturn(er);

            assertDoesNotThrow(() -> employeeService.employeeUpdate(keyMap, attrMap));
        }

        @Test
        void updateEmployee_employeeIsNotUpdated() throws UserDoesNotExistException {

            EntityResult userEntityResult = new EntityResultMapImpl();

            keyMap.put(UserDAO.LOGIN_NAME, "empleado1");
            attrMap.put(UserDAO.USER_PASSWORD, "Hola1234");
            attrMap.put(UserDAO.SURNAME1, "ff");
            attrMap.put(UserDAO.ID_DOCUMENT, "35581834Y");

            userEntityResult.put(UserDAO.USER_PASSWORD, "Hola12345");
            userEntityResult.put(UserDAO.SURNAME1, "ff");
            userEntityResult.put(UserDAO.LOGIN_NAME, "empleado1");
            userEntityResult.put(UserDAO.ID_DOCUMENT, "35581834Y");

            EntityResult er = new EntityResultMapImpl();
            er.setCode(EntityResult.OPERATION_WRONG);
            List<String> result = List.of(RoleNames.MANAGER);

            when(userService.getUserRoles(any())).thenReturn(result);

            assertEquals(IUserService.WRONG_ROLE, employeeService.employeeUpdate(keyMap, attrMap).getMessage());


        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class deleteEmployee {

        Map<Object, Object> keymap = new HashMap<>();

        @Test
        void deleteEmployee_employeeDeleted() throws UserDoesNotExistException {
            keymap.put(UserDAO.LOGIN_NAME, "empleado1");


            EntityResult er = new EntityResultMapImpl();
            er.setCode(EntityResult.OPERATION_SUCCESSFUL_SHOW_MESSAGE);
            List<String> result = List.of(RoleNames.EMPLOYEE);

            when(userService.getUserRoles(any())).thenReturn(result);

            when(userService.userDelete(keymap)).thenReturn(er);

            assertEquals(EntityResult.OPERATION_SUCCESSFUL_SHOW_MESSAGE, employeeService.employeeDelete(keymap).getCode());
        }

        @Test
        void deleteEmployee_notEmployee_notDeleted() throws UserDoesNotExistException {
            keymap.put(UserDAO.LOGIN_NAME, "empleado1");

            EntityResult er = new EntityResultMapImpl();
            er.setCode(EntityResult.OPERATION_SUCCESSFUL_SHOW_MESSAGE);
            List<String> userRoles = List.of(RoleNames.CLIENT);

            when(userService.getUserRoles(any())).thenReturn(userRoles);

            EntityResult result = employeeService.employeeDelete(keymap);

            assertEquals(EntityResult.OPERATION_WRONG, result.getCode());
            assertEquals(IEmployeeService.ERR_CANNOT_DELETE_USER, result.getMessage());
        }


    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class QueryEmployee {

        Map<Object, Object> keymap = new HashMap<>();

        @Test
        void queryEmployee_employeeIsReturned() {
            keymap.put(UserDAO.LOGIN_NAME, "empleado1");

            EntityResult er = new EntityResultMapImpl();
            er.setCode(EntityResult.OPERATION_SUCCESSFUL);

            er.put(UserDAO.LOGIN_NAME, List.of("empleado1"));
            when(userService.userIdentifiedQuery(any(), any(), any())).thenReturn(er);

            EntityResult result;
            result = employeeService.employeeQuery(new HashMap<>(), List.of("shift"));

            assertDoesNotThrow(() -> employeeService.employeeQuery(new HashMap<>(), List.of("shift")));
            assertEquals("empleado1", result.getRecordValues(0).get(UserDAO.LOGIN_NAME));
        }

    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class EmployeeInsert {

        private final Map<? super Object, ? super Object> attrMap = new HashMap<>();

        @Test
        void employeeInsert_validEmployee_operationSuccessful() throws InvalidShiftException {
            attrMap.put(UserDAO.LOGIN_NAME, "test");
            attrMap.put(UserDAO.USER_NAME, "test");
            attrMap.put(UserDAO.EMAIL, "test@example.org");
            attrMap.put(UserDAO.USER_PASSWORD, "Test1234");
            attrMap.put(UserDAO.ID_DOCUMENT, "35581834Y");
            attrMap.put(UserDAO.SURNAME1, "test");
            attrMap.put(UserDAO.PHONE_NUMBER, "666555444");
            attrMap.put(UserDAO.COUNTRY_ID, 1);
            attrMap.put(UserDAO.DAYS_OFF, List.of("monday"));

            EntityResult userInsertResult = new EntityResultMapImpl();
            userInsertResult.setMessage(IUserService.USER_INSERT_SUCCESS);
            userInsertResult.setCode(EntityResult.OPERATION_SUCCESSFUL_SHOW_MESSAGE);

            EntityResult roleQueryEntityResult = new EntityResultMapImpl();
            roleQueryEntityResult.put(UserRoleDAO.ID, List.of(1));

            when(userService.userInsert(any())).thenReturn(userInsertResult);
            when(daoHelper.query(any(), any(), any())).thenReturn(roleQueryEntityResult);

            EntityResult result = employeeService.employeeInsert(attrMap);

            assertEquals(EntityResult.OPERATION_SUCCESSFUL_SHOW_MESSAGE, result.getCode());
            assertEquals(IUserService.USER_INSERT_SUCCESS, result.getMessage());
        }

    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class ClockIn {
        Map<? super Object, ? super Object> attrMap = new HashMap<>();

        @Test
        void clockInInsert_operationSuccessful() {

            attrMap.put(EmployeesEntryDepartureDAO.LOGIN_NAME, "Test");

            EntityResult queryEntityResult = new EntityResultMapImpl();

            EntityResult inserEntityResult = new EntityResultMapImpl();
            inserEntityResult.setMessage(EmployeesEntryDepartureDAO.OPERATION_SUCCESS);
            inserEntityResult.setCode(EntityResult.OPERATION_SUCCESSFUL_SHOW_MESSAGE);

            when(daoHelper.query(any(), any(), any())).thenReturn(queryEntityResult);
            when(daoHelper.insert(any(), any())).thenReturn(inserEntityResult);

            EntityResult result = employeeService.clockInInsert(attrMap);

            assertEquals(EntityResult.OPERATION_SUCCESSFUL_SHOW_MESSAGE, result.getCode());
            assertEquals(EmployeesEntryDepartureDAO.OPERATION_SUCCESS, result.getMessage());

        }

        @Test
        void clockInInsert_OperationWrong_cockInAlreadySaved() {
            attrMap.put(EmployeesEntryDepartureDAO.LOGIN_NAME, "Test");

            EntityResult queryEntityResult = new EntityResultMapImpl();
            queryEntityResult.put(EmployeesEntryDepartureDAO.LOGIN_NAME, attrMap.get(EmployeesEntryDepartureDAO.LOGIN_NAME));
            queryEntityResult.put(EmployeesEntryDepartureDAO.WORKING_DAY, Date.from(Instant.now()));

            when(daoHelper.query(any(), any(), any())).thenReturn(queryEntityResult);

            EntityResult result = employeeService.clockInInsert(attrMap);

            assertEquals(EntityResult.OPERATION_WRONG, result.getCode());
            assertEquals(EmployeesEntryDepartureDAO.E_ENTRY_SAVED, result.getMessage());
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class ClockOut {
        Map<? super Object, ? super Object> attrMap = new HashMap<>();
        Map<? super Object, ? super Object> filter = new HashMap<>();

        @Test
        void clockOutUpdate_OperationSuccess() {

            filter.put(EmployeesEntryDepartureDAO.LOGIN_NAME,"ClockOutUpdateTest");

            EntityResult queryEntityResult = new EntityResultMapImpl();
            queryEntityResult.put(EmployeesEntryDepartureDAO.LOGIN_NAME, List.of(filter.get(EmployeesEntryDepartureDAO.LOGIN_NAME)));
            queryEntityResult.put(EmployeesEntryDepartureDAO.WORKING_DAY, List.of(Date.from(Instant.now())));
            queryEntityResult.put(EmployeesEntryDepartureDAO.ENTRY, List.of(Time.valueOf(LocalDateTime.now().toLocalTime())));

            EntityResult clockOutUpdateEntityResult = new EntityResultMapImpl();
            clockOutUpdateEntityResult.setMessage(EmployeesEntryDepartureDAO.CLOCK_OUT_SUCCESS);
            clockOutUpdateEntityResult.setCode(EntityResult.OPERATION_SUCCESSFUL_SHOW_MESSAGE);

            when(daoHelper.query(any(), any(), any())).thenReturn(queryEntityResult);
            when(daoHelper.update(any(),any(),any())).thenReturn(clockOutUpdateEntityResult);

            EntityResult result= employeeService.clockOutUpdate(attrMap,filter);

            assertEquals(EntityResult.OPERATION_SUCCESSFUL_SHOW_MESSAGE, result.getCode());
            assertEquals(EmployeesEntryDepartureDAO.CLOCK_OUT_SUCCESS, result.getMessage());


        }
    }


}
