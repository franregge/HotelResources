package com.ontimize.hr.model.core.service;

import com.ontimize.hr.api.core.service.IBookingService;
import com.ontimize.hr.api.core.service.IUserService;
import com.ontimize.hr.api.core.service.exception.*;
import com.ontimize.hr.model.core.NameRoles;
import com.ontimize.hr.model.core.dao.BookingDAO;
import com.ontimize.hr.model.core.dao.RoomDAO;
import com.ontimize.hr.model.core.dao.UserDAO;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.common.dto.EntityResultMapImpl;
import com.ontimize.jee.common.security.PermissionsProviderSecured;
import com.ontimize.jee.server.dao.DefaultOntimizeDaoHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
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


    private void validateUser(Map<?, ?> attrMap) throws InvalidBookingDNIException, InvalidPasswordException {
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

    @Secured({PermissionsProviderSecured.SECURED})
    @Override
    public EntityResult userInsert(Map<?, ?> attrMap) {
        EntityResult result;

        try {
            validateUser(attrMap);

            result = this.daoHelper.insert(this.userDAO, attrMap);
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
    public EntityResult userUpdate(Map<?, ?> attrMap, Map<?, ?> keyMap) {
        EntityResult result;

        try {
            Map<String, Integer> bookingIdFilter = new HashMap<>();
            Map<String, Integer> userIdFilter = new HashMap<>();
            bookingIdFilter.put(BookingDAO.ID, (Integer) keyMap.get(BookingDAO.ID));
            userIdFilter.put(UserDAO.ID, (Integer) keyMap.get(UserDAO.ID));
            EntityResult userEntityResult = userQuery(bookingIdFilter, List.of(BookingDAO.ID));

            if (userEntityResult.isEmpty()) {
                throw new UserDoesNotExistException(IUserService.USER_NOT_FOUND);
            }

            EntityResult originalUserEntityResult =
                    daoHelper.query(userDAO, userIdFilter, List.of(UserDAO.ID, UserDAO.USER_NAME, UserDAO.SURNAME1, UserDAO.SURNAME2, UserDAO.ID_DOCUMENT, UserDAO.COUNTRY_ID, UserDAO.EMAIL, UserDAO.PHONE_NUMBER, UserDAO.ROLE_ID, UserDAO.USER_PASSWORD));


            if (originalUserEntityResult.isEmpty()) {
                throw new BookingDoesNotExistException(IUserService.USER_NOT_FOUND);
            }
            Map<?, ?> originalUser = originalUserEntityResult.getRecordValues(0);
            Map<Object, Object> userUpdated = new HashMap<>(attrMap);
            userUpdated.put(UserDAO.ID, keyMap.get(UserDAO.ID));
            userUpdated.put(UserDAO.USER_NAME, originalUser.get(UserDAO.USER_NAME));
            userUpdated.put(UserDAO.SURNAME1, originalUser.get(UserDAO.SURNAME1));
            userUpdated.put(UserDAO.SURNAME2, originalUser.get(UserDAO.SURNAME2));
            userUpdated.put(UserDAO.ID_DOCUMENT, originalUser.get(UserDAO.ID_DOCUMENT));
            userUpdated.put(UserDAO.COUNTRY_ID, originalUser.get(UserDAO.COUNTRY_ID));
            userUpdated.put(UserDAO.PHONE_NUMBER, originalUser.get(UserDAO.PHONE_NUMBER));
            userUpdated.put(UserDAO.EMAIL, originalUser.get(UserDAO.EMAIL));
            userUpdated.put(UserDAO.USER_PASSWORD, originalUser.get(UserDAO.USER_PASSWORD));
            userUpdated.put(UserDAO.ROLE_ID, originalUser.get(UserDAO.ROLE_ID));



            result = this.daoHelper.update(this.userDAO, userUpdated, keyMap);
            result.put("updated_id", keyMap.get(UserDAO.ID));
            result.setMessage("User updated successfully");
            result.setCode(EntityResult.OPERATION_SUCCESSFUL_SHOW_MESSAGE);
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
        Integer userId = (Integer) keyMap.get(UserDAO.ID);

        if (this.daoHelper.query(userDAO, keyMap, List.of(UserDAO.ID)).isEmpty()) {
            EntityResult result = new EntityResultMapImpl();
            result.setMessage(IUserService.NO_USER_WITH_ID);
            result.setCode(EntityResult.OPERATION_WRONG);
            return result;
        }

        EntityResult result = this.daoHelper.delete(userDAO, keyMap);
        result.setMessage(IUserService.DELETION_SUCCESS);
        result.setCode(EntityResult.OPERATION_SUCCESSFUL_SHOW_MESSAGE);
        result.put("deleted_id", userId);
        return result;
    }
}