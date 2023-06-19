package com.ontimize.hr.model.core.service;

import com.ontimize.hr.api.core.service.IBookingService;
import com.ontimize.hr.api.core.service.IUserService;
import com.ontimize.hr.api.core.service.exception.InvalidBookingDNIException;
import com.ontimize.hr.api.core.service.exception.InvalidPasswordException;
import com.ontimize.hr.model.core.dao.UserDAO;
import com.ontimize.hr.model.core.dao.UserRoleDAO;
import com.ontimize.hr.model.core.dao.UsersRolesDAO;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.common.dto.EntityResultMapImpl;
import com.ontimize.jee.common.security.PermissionsProviderSecured;
import com.ontimize.jee.server.dao.DefaultOntimizeDaoHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

@Lazy
@Service("UserService")
public class UserService implements IUserService {

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private UserRoleDAO userRoleDAO;

    @Autowired
    private UsersRolesDAO usersRolesDAO;

    @Autowired
    private DefaultOntimizeDaoHelper daoHelper;


    Predicate<Map<?, ?>> passwordLengthOverEight = userMap -> {
        String password = (String) userMap.get(UserDAO.USER_PASSWORD);
        return password.length() >= 8;
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

    @Secured({PermissionsProviderSecured.SECURED})
    @Override
    public EntityResult userQuery(Map<?, ?> keyMap, List<?> attrList) {
        return this.daoHelper.query(userDAO, keyMap, attrList);
    }


    private void validateUser(Map<?, ?> attrMap) throws Exception {

        if (!validateDNI((String) attrMap.get(UserDAO.ID_DOCUMENT))) {
            throw new InvalidBookingDNIException(IBookingService.INVALID_ID_DOCUMENT);
        }

        if (!passwordLengthOverEight.test(attrMap)) {
            throw new InvalidPasswordException(IUserService.PASS_INSTRUCTIONS + ". " + IUserService.PASS_LENGTH_TOO_SHORT);
        }

        if (!passwordHasLetter.test(attrMap)) {
            throw new InvalidPasswordException(IUserService.PASS_INSTRUCTIONS + ". " + IUserService.PASS_HAS_NO_LETTER);
        }

        if (!passwordHasNumber.test(attrMap)) {
            throw new InvalidPasswordException(IUserService.PASS_INSTRUCTIONS + ". " + IUserService.PASS_HAS_NO_NUMBER);
        }

        if (!passwordHasCapitalLetter.test(attrMap)) {
            throw new InvalidPasswordException(IUserService.PASS_INSTRUCTIONS + ". " + IUserService.PASS_HAS_NO_CAPITAL_LETTER);
        }

        if (!passwordHasLowerCaseLetter.test(attrMap)) {
            throw new InvalidPasswordException(IUserService.PASS_INSTRUCTIONS + ". " + IUserService.PASS_HAS_NO_LOWER_CASE_LETTER);
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
    public EntityResult userInsert(Map<? super Object, ? super Object> attrMap) {
        EntityResult result;

        try {
            // TODO error checking
            validateUser(attrMap);

            result = this.daoHelper.insert(this.userDAO, attrMap);

            Map<String, ? super Object> roleInsertAttributes = new HashMap<>();
            roleInsertAttributes.put(UsersRolesDAO.LOGIN_NAME, attrMap.get(UserDAO.LOGIN_NAME));
            roleInsertAttributes.put(UsersRolesDAO.ROLE_NAME, attrMap.get(UserDAO.ROLE_NAME));

            this.daoHelper.insert(usersRolesDAO, roleInsertAttributes);
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
    public EntityResult userUpdate(Map<?, ?> attrMap, Map<?, ?> keyMap) {
        return null; // TODO check user can only update itself
    }

    @Secured({PermissionsProviderSecured.SECURED})
    @Override
    public EntityResult userDelete(Map<?, ?> keyMap) {
        String loginName = (String) keyMap.get(UserDAO.LOGIN_NAME);
        EntityResult result;

        if (this.daoHelper.query(userDAO, keyMap, List.of(UserDAO.LOGIN_NAME)).isEmpty()) {
            result = new EntityResultMapImpl();
            result.setMessage(IUserService.NO_USER_WITH_ID);
            result.setCode(EntityResult.OPERATION_WRONG);
            return result;
        }

        result = this.daoHelper.delete(userDAO, keyMap);
        result.setMessage(IUserService.DELETION_SUCCESS);
        result.setCode(EntityResult.OPERATION_SUCCESSFUL_SHOW_MESSAGE);
        result.put("deleted_id", loginName);

        return result;
    }
}
