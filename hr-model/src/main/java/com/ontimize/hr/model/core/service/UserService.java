package com.ontimize.hr.model.core.service;

import com.ontimize.hr.api.core.service.IBookingService;
import com.ontimize.hr.api.core.service.IUserService;
import com.ontimize.hr.api.core.service.exception.InvalidBookingDNIException;
import com.ontimize.hr.api.core.service.exception.InvalidPasswordException;
import com.ontimize.hr.model.core.dao.UserDAO;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.common.dto.EntityResultMapImpl;
import com.ontimize.jee.common.security.PermissionsProviderSecured;
import com.ontimize.jee.server.dao.DefaultOntimizeDaoHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

@Lazy
@Service("UserService")
public class UserService implements IUserService {

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private DefaultOntimizeDaoHelper daoHelper;


    Predicate<Map<?, ?>> passwordLengthOverEight = userMap -> {
        String password = (String) userMap.get(UserDAO.USER_PASSWORD);
        return password.length() >= 8;
    };
    Predicate<Map<?, ?>> notEmployeeRole = userMap -> {
        String role_id = String.valueOf(userMap.get(UserDAO.ROLE_ID)) ;
        return role_id.matches(UserDAO.EMPLOYEE_ROLE_ID) ;
    };

    Predicate<Map<?, ?>> passwordHasLetter = userMap -> {
        String password = (String) userMap.get(UserDAO.USER_PASSWORD);
        return password.matches(".*[a-zA-ZÑñ].*");
    };

    Predicate<Map<?, ?>> passwordHasNumber = userMap -> {
        String password = (String) userMap.get(UserDAO.USER_PASSWORD);
        return password.matches(".*[0-9].*");
    };

    Predicate<Map<?, ?>> passwordHasCapitalLetter = userMap -> {
        String password = (String) userMap.get(UserDAO.USER_PASSWORD);
        return password.matches(".*[A-ZÑ].*");
    };

    Predicate<Map<?, ?>> passwordHasLowerCaseLetter = userMap -> {
        String password = (String) userMap.get(UserDAO.USER_PASSWORD);
        return password.matches(".*[a-zñ].*");
    };
    @Secured({ PermissionsProviderSecured.SECURED })
    @Override
    public EntityResult userQuery(Map<?, ?> keyMap, List<?> attrList) {
        return this.daoHelper.query(userDAO, keyMap, attrList);
    }


    private void validateUser(Map<?, ?> attrMap) throws Exception {

        if (!validateDNI((String) attrMap.get(UserDAO.ID_DOCUMENT))) {
            throw new InvalidBookingDNIException(IBookingService.INVALID_ID_DOCUMENT);
        }

        if (!passwordLengthOverEight.test(attrMap)) {
            throw new InvalidPasswordException(IUserService.PASS_INSTRUCTIONS+". "+IUserService.PASS_LENGTH_TOO_SHORT);
        }

        if (!passwordHasLetter.test(attrMap)) {
            throw new InvalidPasswordException(IUserService.PASS_INSTRUCTIONS+". "+IUserService.PASS_HAS_NO_LETTER);
        }

        if (!passwordHasNumber.test(attrMap)) {
            throw new InvalidPasswordException(IUserService.PASS_INSTRUCTIONS+". "+IUserService.PASS_HAS_NO_NUMBER);
        }

        if (!passwordHasCapitalLetter.test(attrMap)) {
            throw new InvalidPasswordException(IUserService.PASS_INSTRUCTIONS+". "+IUserService.PASS_HAS_NO_CAPITAL_LETTER);
        }

        if (!passwordHasLowerCaseLetter.test(attrMap)) {
            throw new InvalidPasswordException(IUserService.PASS_INSTRUCTIONS+". "+IUserService.PASS_HAS_NO_LOWER_CASE_LETTER);
        }
    }


    private boolean validateDNI(String dni) {

        List<Character> letters = List.of(
                'T', 'R', 'W', 'A', 'G', 'M', 'Y', 'F', 'P', 'D', 'X', 'B', 'N', 'J', 'Z', 'S', 'Q', 'V', 'H',
                'L', 'C', 'K', 'E'
        );

        if (dni.length() != 9) {
            return false;
        }

        dni = dni.toUpperCase();

        if (!dni.matches("\\d{8}[A-HJ-NP-TV-Z]")) {
            return false;
        }

        int numberSegment = Integer.parseInt(dni.substring(0, 8));
        char letter = dni.charAt(8);

        return letters.get(numberSegment % 23) == letter;
    }

    @Secured({})
    @Override
    public EntityResult userInsert(Map<?, ?> attrMap) {
        EntityResult result;

        try {
            if (notEmployeeRole.test(attrMap)){
                throw new  Exception(IUserService.ONLY_MANAGER_ADD_EMPLOYEES);
            }
            validateUser(attrMap);

            result = this.daoHelper.insert(this.userDAO, attrMap);
            result.setCode(EntityResult.OPERATION_SUCCESSFUL_SHOW_MESSAGE);
            result.setMessage(IUserService.USER_INSERT_SUCCESS);


        } catch (Exception e) {
            result = new EntityResultMapImpl();
            result.setMessage(e.getMessage());
            result.setCode(EntityResult.OPERATION_WRONG);
            e.printStackTrace();
        }

        return result;
    }
    @Override
    @Secured({ PermissionsProviderSecured.SECURED })
    public EntityResult employeeInsert(Map<?, ?> attrMap) {

        Map<?,?> data = (Map)attrMap.get("data");
        EntityResult result;

        try {
            validateUser(data);

            result = this.daoHelper.insert(this.userDAO, data);
            result.setCode(EntityResult.OPERATION_SUCCESSFUL_SHOW_MESSAGE);
            result.setMessage(IUserService.USER_INSERT_SUCCESS);


        } catch (Exception e) {
            result = new EntityResultMapImpl();
            result.setMessage(e.getMessage());
            result.setCode(EntityResult.OPERATION_WRONG);
            e.printStackTrace();
        }

        return result;
    }
    @Secured({PermissionsProviderSecured.SECURED})
    @Override
    public EntityResult employeeDelete(Map<?, ?> keyMap) {
        Map<Object,Object> data = (Map)keyMap.get("data");

        String userLoginName = (String) data.get(UserDAO.LOGIN_NAME);


        if (this.daoHelper.query(userDAO, data, List.of(UserDAO.LOGIN_NAME)).isEmpty()) {
            EntityResult result = new EntityResultMapImpl();
            result.setMessage(IUserService.NO_USER_FOUND);
            result.setCode(EntityResult.OPERATION_WRONG);
            return result;
        }

        EntityResult result = this.daoHelper.delete(userDAO, data);
        result.setMessage(IUserService.DELETION_SUCCESS);
        result.setCode(EntityResult.OPERATION_SUCCESSFUL_SHOW_MESSAGE);
        result.put("deleted_user", userLoginName);
        return result;
    }
    @Secured({ PermissionsProviderSecured.SECURED })
    @Override
    public EntityResult userUpdate(Map<?, ?> attrMap, Map<?, ?> keyMap) {
        return null; // TODO check user can only update itself
    }

    @Secured({ PermissionsProviderSecured.SECURED })
    @Override
    public EntityResult userDelete(Map<?, ?> keyMap) {
        return null;
    }
}
