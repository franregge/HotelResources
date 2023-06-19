package com.ontimize.hr.model.core;

import com.ontimize.hr.model.core.dao.UserDAO;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
public class EmployeeServiceTest {

    @InjectMocks
    EmployeeService employeeService;
    @Mock
    DefaultOntimizeDaoHelper daoHelper;
    @Mock
    UserDAO userDAO;
    UserService userService;

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class deleteEmployee{

        Map<Object,Object>keymap = new HashMap<>();

        @Test
        void deleteEmployee_employeeDeleted(){

            keymap.put(UserDAO.LOGIN_NAME,"empleado1");


            EntityResult er = new EntityResultMapImpl();
            er.setCode(EntityResult.OPERATION_SUCCESSFUL_SHOW_MESSAGE);
            List<String> result = List.of(NameRoles.EMPLOYEE);

            when(userService.getUserRoles(any())).thenReturn(result);

            when(userService.userDelete(keymap)).thenReturn(er);

            assertDoesNotThrow(()->employeeService.employeeDelete(keymap));
        }

        @Test
        void deleteEmployee_notEmployee_notDeleted(){
            keymap.put(UserDAO.LOGIN_NAME,"empleado1");


            EntityResult er = new EntityResultMapImpl();
            er.setCode(EntityResult.OPERATION_SUCCESSFUL_SHOW_MESSAGE);
            List<String> result = List.of(NameRoles.ClIENT);

            when(userService.getUserRoles(any())).thenReturn(result);

            when(userService.userDelete(keymap)).thenReturn(er);

            assertThrows( Exception.class,()->employeeService.employeeDelete(keymap), "Cannot delete this user");
        }


    }


}
