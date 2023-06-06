package com.ontimize.hr.model.core;

import com.ontimize.hr.model.core.dao.UserDAO;
import com.ontimize.hr.model.core.service.UserService;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.server.dao.DefaultOntimizeDaoHelper;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.*;

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
            attrMap.put(UserDAO.ROLE_ID,1);

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
            attrMap.put(UserDAO.ROLE_ID,1);

            EntityResult actualResult = userService.userInsert(attrMap);

            assertEquals(EntityResult.OPERATION_WRONG, actualResult.getCode());
        }
        @Test
        void insertUser_invalidUser_invalidPass_noLetters(){

            attrMap.put(UserDAO.USER_NAME,"Manolo");
            attrMap.put(UserDAO.USER_PASSWORD,"12341234");
            attrMap.put(UserDAO.COUNTRY_ID,1);
            attrMap.put(UserDAO.SURNAME1,"Garcia");
            attrMap.put(UserDAO.ID_DOCUMENT,"66955662V");
            attrMap.put(UserDAO.PHONE_NUMBER,"666666666");
            attrMap.put(UserDAO.SURNAME2,"Martinez");
            attrMap.put(UserDAO.EMAIL,"manolo.martinez@mymail.com");
            attrMap.put(UserDAO.ROLE_ID,1);

            EntityResult actualResult = userService.userInsert(attrMap);

            assertEquals(EntityResult.OPERATION_WRONG, actualResult.getCode());
        }
        @Test
        void insertUser_invalidUser_invalidPass_noNumbers(){

            attrMap.put(UserDAO.USER_NAME,"Manolo");
            attrMap.put(UserDAO.USER_PASSWORD,"holaquetal");
            attrMap.put(UserDAO.COUNTRY_ID,1);
            attrMap.put(UserDAO.SURNAME1,"Garcia");
            attrMap.put(UserDAO.ID_DOCUMENT,"66955662V");
            attrMap.put(UserDAO.PHONE_NUMBER,"666666666");
            attrMap.put(UserDAO.SURNAME2,"Martinez");
            attrMap.put(UserDAO.EMAIL,"manolo.martinez@mymail.com");
            attrMap.put(UserDAO.ROLE_ID,1);

            EntityResult actualResult = userService.userInsert(attrMap);

            assertEquals(EntityResult.OPERATION_WRONG, actualResult.getCode());
        }
        @Test
        void insertUser_invalidUser_invalidPass_invalidPassLength(){

            attrMap.put(UserDAO.USER_NAME,"Manolo");
            attrMap.put(UserDAO.USER_PASSWORD,"Hola1");
            attrMap.put(UserDAO.COUNTRY_ID,1);
            attrMap.put(UserDAO.SURNAME1,"Garcia");
            attrMap.put(UserDAO.ID_DOCUMENT,"66955662V");
            attrMap.put(UserDAO.PHONE_NUMBER,"666666666");
            attrMap.put(UserDAO.SURNAME2,"Martinez");
            attrMap.put(UserDAO.EMAIL,"manolo.martinez@mymail.com");
            attrMap.put(UserDAO.ROLE_ID,1);

            EntityResult actualResult = userService.userInsert(attrMap);

            assertEquals(EntityResult.OPERATION_WRONG, actualResult.getCode());
        }
        @Test
        void insertUser_invalidUser_invalidPass_pasWithoutCapitalLetter(){

            attrMap.put(UserDAO.USER_NAME,"Manolo");
            attrMap.put(UserDAO.USER_PASSWORD,"hola1234");
            attrMap.put(UserDAO.COUNTRY_ID,1);
            attrMap.put(UserDAO.SURNAME1,"Garcia");
            attrMap.put(UserDAO.ID_DOCUMENT,"66955662V");
            attrMap.put(UserDAO.PHONE_NUMBER,"666666666");
            attrMap.put(UserDAO.SURNAME2,"Martinez");
            attrMap.put(UserDAO.EMAIL,"manolo.martinez@mymail.com");
            attrMap.put(UserDAO.ROLE_ID,1);

            EntityResult actualResult = userService.userInsert(attrMap);

            assertEquals(EntityResult.OPERATION_WRONG, actualResult.getCode());
        }
        @Test
        void insertUser_invalidUser_invalidPass_pasWithoutLowerCase(){

            attrMap.put(UserDAO.USER_NAME,"Manolo");
            attrMap.put(UserDAO.USER_PASSWORD,"HOLA1234");
            attrMap.put(UserDAO.COUNTRY_ID,1);
            attrMap.put(UserDAO.SURNAME1,"Garcia");
            attrMap.put(UserDAO.ID_DOCUMENT,"66955662V");
            attrMap.put(UserDAO.PHONE_NUMBER,"666666666");
            attrMap.put(UserDAO.SURNAME2,"Martinez");
            attrMap.put(UserDAO.EMAIL,"manolo.martinez@mymail.com");
            attrMap.put(UserDAO.ROLE_ID,1);

            EntityResult actualResult = userService.userInsert(attrMap);

            assertEquals(EntityResult.OPERATION_WRONG, actualResult.getCode());
        }


    }
}
