package com.ontimize.hr.model.core;

import com.ontimize.hr.api.core.service.IUserService;
import com.ontimize.hr.api.core.service.exception.UserDoesNotExistException;
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

import static org.junit.jupiter.api.Assertions.*;
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
    @Mock
    UserService userService;

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class updateEmployee{


        Map<Object,Object> keyMap = new HashMap<>();
        Map<Object, Object> attrMap = new HashMap<>();

        @Test
        void updateEmployee_employeeIsUpdated() throws UserDoesNotExistException {

            EntityResult userEntityResult = new EntityResultMapImpl();

            keyMap.put(UserDAO.LOGIN_NAME,"empleado1");
            attrMap.put(UserDAO.USER_PASSWORD,"Hola1234");
            attrMap.put(UserDAO.SURNAME1,"ff");
            attrMap.put(UserDAO.ID_DOCUMENT,"35581834Y");

            userEntityResult.put(UserDAO.USER_PASSWORD,"Hola12345");
            userEntityResult.put(UserDAO.SURNAME1,"ff");
            userEntityResult.put(UserDAO.LOGIN_NAME, "empleado1");
            userEntityResult.put(UserDAO.ID_DOCUMENT,"35581834Y");

            EntityResult er = new EntityResultMapImpl();
            er.setCode(EntityResult.OPERATION_SUCCESSFUL_SHOW_MESSAGE);
            List<String> result = List.of(NameRoles.EMPLOYEE);
            when(userService.getUserRoles(any())).thenReturn(result);
            when(userService.userUpdate(attrMap,keyMap)).thenReturn(er);

            assertDoesNotThrow(()->employeeService.employeeUpdate(keyMap,attrMap));
        }

    @Test
    void updateEmployee_employeeIsNotUpdated() throws UserDoesNotExistException {

        EntityResult userEntityResult = new EntityResultMapImpl();

        keyMap.put(UserDAO.LOGIN_NAME,"empleado1");
        attrMap.put(UserDAO.USER_PASSWORD,"Hola1234");
        attrMap.put(UserDAO.SURNAME1,"ff");
        attrMap.put(UserDAO.ID_DOCUMENT,"35581834Y");

        userEntityResult.put(UserDAO.USER_PASSWORD,"Hola12345");
        userEntityResult.put(UserDAO.SURNAME1,"ff");
        userEntityResult.put(UserDAO.LOGIN_NAME, "empleado1");
        userEntityResult.put(UserDAO.ID_DOCUMENT,"35581834Y");

        EntityResult er = new EntityResultMapImpl();
        er.setCode(EntityResult.OPERATION_WRONG);
        List<String> result = List.of(NameRoles.MANAGER);

        when(userService.getUserRoles(any())).thenReturn(result);

        assertEquals(IUserService.WRONG_ROLE,employeeService.employeeUpdate(keyMap,attrMap).getMessage());


    }
    }
}
