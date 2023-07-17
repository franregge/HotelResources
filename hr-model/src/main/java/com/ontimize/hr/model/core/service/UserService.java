package com.ontimize.hr.model.core.service;

import com.ontimize.hr.api.core.service.IUserService;
import com.ontimize.hr.api.core.service.exception.InvalidIdDocumentException;
import com.ontimize.hr.api.core.service.exception.InvalidPasswordException;
import com.ontimize.hr.api.core.service.exception.*;
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
        return password.matches(".*[\\d].*");
    };

    Predicate<Map<?, ?>> passwordHasCapitalLetter = userMap -> {
        String password = (String) userMap.get(UserDAO.USER_PASSWORD);
        return password.matches(".*[A-ZÑ].*");
    };

    Predicate<Map<?, ?>> passwordHasLowerCaseLetter = userMap -> {
        String password = (String) userMap.get(UserDAO.USER_PASSWORD);
        return password.matches(".*[a-zñ].*");
    };

    Predicate<Map<?, ?>> emailIsValid = userMap -> {
        Object mapContent = userMap.get(UserDAO.EMAIL);

        if (mapContent instanceof String) {
            String email = (String) mapContent;
            return email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
        }

        return false;
    };

    Predicate<Map<?, ?>> phoneNumberIsValid = userMap -> {
        Object mapValue = userMap.get(UserDAO.PHONE_NUMBER);
        if (mapValue instanceof String) {
            String phoneNumber = (String) mapValue;
            return !phoneNumber.isBlank() && !phoneNumber.isEmpty();
        }

        return false;
    };

    @Secured({PermissionsProviderSecured.SECURED})
    @Override
    public EntityResult userQuery(Map<?, ?> keyMap, List<?> attrList) {
        return this.daoHelper.query(userDAO, keyMap, attrList);
    }


    public EntityResult userIdentifiedQuery(Map<?, ?> filter, List<?> attrList, String queryId) {
        return this.daoHelper.query(userDAO, filter, attrList, queryId);
    }

    private void validateUser(Map<?, ?> attrMap) throws UserDataException {
        if (invalidDocument((String) attrMap.get(UserDAO.ID_DOCUMENT))) {
            throw new InvalidIdDocumentException(IUserService.ERR_INVALID_ID_DOCUMENT);
        }

        validatePassword(attrMap);

        if (!(attrMap.get(UserDAO.COUNTRY_ID) instanceof Integer)) {
            throw new InvalidCountryException(IUserService.ERR_INVALID_COUNTRY_ID);
        }

        if (!phoneNumberIsValid.test(attrMap)) {
            throw new InvalidPhoneNumberException(IUserService.ERR_INVALID_PHONE_NUMBER);
        }

        if (!emailIsValid.test(attrMap)) {
            throw new InvalidEmailException(IUserService.ERR_INVALID_EMAIL);
        }
    }

    private void validatePassword(Map<?, ?> attrMap) throws InvalidPasswordException {
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

    private void validateUserUpdate(Map<?, ?> attrMap) throws UserDataException {
        if (attrMap.get(UserDAO.USER_PASSWORD) != null) {
            validatePassword(attrMap);
        }

        if (attrMap.get(UserDAO.ID_DOCUMENT) != null && (invalidDocument((String) attrMap.get(UserDAO.ID_DOCUMENT)))) {
            throw new InvalidIdDocumentException(IUserService.ERR_INVALID_ID_DOCUMENT);

        }

        if (attrMap.get(UserDAO.EMAIL) != null && !emailIsValid.test(attrMap)) {
            throw new InvalidEmailException(IUserService.ERR_INVALID_EMAIL);

        }

        if (attrMap.get(UserDAO.PHONE_NUMBER) != null && (!phoneNumberIsValid.test(attrMap))) {
            throw new InvalidPhoneNumberException(IUserService.ERR_INVALID_PHONE_NUMBER);

        }
    }


    private boolean invalidDocument(String dni) {
        List<Character> letters = List.of(
                'T', 'R', 'W', 'A', 'G', 'M', 'Y', 'F', 'P', 'D', 'X', 'B', 'N', 'J', 'Z', 'S', 'Q', 'V', 'H',
                'L', 'C', 'K', 'E'
        );

        if (dni.length() != 9) {
            return true;
        }

        dni = dni.toUpperCase();

        if (!dni.matches("[XYZ\\d]?\\d{7}[A-HJ-NP-TV-Z]")) {
            return true;
        }

        if (dni.startsWith("X")) {
            dni = "0" + dni.substring(1);
        } else if (dni.startsWith("Y")) {
            dni = "1" + dni.substring(1);
        } else if (dni.startsWith("Z")) {
            dni = "2" + dni.substring(1);
        }

        int numberSegment = Integer.parseInt(dni.substring(0, 8));
        char letter = dni.charAt(8);

        return letters.get(numberSegment % 23) != letter;
    }


    @SuppressWarnings("unchecked")
    public List<String> getUserRoles(String loginName) throws UserDoesNotExistException {
        Map<String, String> filter = new HashMap<>();
        filter.put(UserDAO.LOGIN_NAME, loginName);
        List<String> queriedAtributeList = List.of(UserRoleDAO.NAME);
        EntityResult result = this.daoHelper.query(userDAO, filter, queriedAtributeList, UserDAO.Q_ROLES_INFO);

        if (result.isEmpty()) {
            throw new UserDoesNotExistException(IUserService.NO_USER_FOUND);
        }

        return (List<String>) result.get(UserRoleDAO.NAME);
    }

    @Secured({})
    @Override
    public EntityResult userInsert(Map<? super Object, ? super Object> attrMap) {
        EntityResult result;

        try {
            validateUser(attrMap);

            result = this.daoHelper.insert(this.userDAO, attrMap);


            List<Integer> roleIds = (List<Integer>) attrMap.get(UserDAO.ROLE_IDS);
            Map<String, ? super Object> roleInsertAttributes = new HashMap<>();
            roleInsertAttributes.put(UsersRolesDAO.LOGIN_NAME, attrMap.get(UserDAO.LOGIN_NAME));

            for (int roleId : roleIds) {
                roleInsertAttributes.put(UsersRolesDAO.ROLE_ID, roleId);

                this.daoHelper.insert(usersRolesDAO, roleInsertAttributes);
            }

            result.setCode(EntityResult.OPERATION_SUCCESSFUL_SHOW_MESSAGE);
            result.setMessage(IUserService.USER_INSERT_SUCCESS);
        } catch (Exception e) {
            result = new EntityResultMapImpl();
            result.setMessage(e.getMessage());
            result.setCode(EntityResult.OPERATION_WRONG);
        }

        return result;
    }

    @Secured({PermissionsProviderSecured.SECURED})
    @Override
    public EntityResult userDelete(Map<?, ?> keyMap) {
        EntityResult result;

        String loginName = (String) keyMap.get(UserDAO.LOGIN_NAME);
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

    @Secured({PermissionsProviderSecured.SECURED})
    @Override
    public EntityResult userUpdate(Map<?, ?> attrMap, Map<?, ?> keyMap) {

        EntityResult result;

        try {

            validateUserUpdate(attrMap);

            result = this.daoHelper.update(this.userDAO, attrMap, keyMap);
            result.put("updated_user", keyMap.get((UserDAO.LOGIN_NAME)));
            result.setMessage("User updated successfully");
            result.setCode(EntityResult.OPERATION_SUCCESSFUL_SHOW_MESSAGE);

        } catch (Exception e) {
            result = new EntityResultMapImpl();
            result.setCode(EntityResult.OPERATION_WRONG);
            result.setMessage(e.getMessage());
        }

        return result;
    }
}
