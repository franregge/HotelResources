package com.ontimize.hr.model.core.service;

import com.ontimize.hr.api.core.service.IBookingService;
import com.ontimize.hr.api.core.service.IUserService;
import com.ontimize.hr.api.core.service.exception.InvalidBookingDNIException;
import com.ontimize.hr.api.core.service.exception.InvalidPasswordException;
import com.ontimize.hr.api.core.service.exception.UserAlreadyExistsException;
import com.ontimize.hr.model.core.dao.BookingDAO;
import com.ontimize.hr.model.core.dao.UserDAO;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.common.dto.EntityResultMapImpl;
import com.ontimize.jee.server.dao.DefaultOntimizeDaoHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
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

    @Override
    public EntityResult userQuery(Map<?, ?> keyMap, List<?> attrList) {
        return this.daoHelper.query(userDAO, keyMap, attrList);
    }


    private void validateUser(Map<?, ?> attrMap) throws InvalidBookingDNIException, InvalidPasswordException {
        if (!validateDNI((String) attrMap.get(UserDAO.ID_DOCUMENT))) {
            throw new InvalidBookingDNIException(IUserService.INVALID_ID_DOCUMENT);
        }

        if (!passwordLengthOverEight.test(attrMap)) {
            throw new InvalidPasswordException(IUserService.INVALID_PASSWORD);
        }

        if (!passwordHasLetter.test(attrMap)) {
            throw new InvalidPasswordException(IUserService.INVALID_PASSWORD);
        }

        if (!passwordHasNumber.test(attrMap)) {
            throw new InvalidPasswordException(IUserService.INVALID_PASSWORD);
        }

        if (!passwordHasCapitalLetter.test(attrMap)) {
            throw new InvalidPasswordException(IUserService.INVALID_PASSWORD);
        }

        if (!passwordHasLowerCaseLetter.test(attrMap)) {
            throw new InvalidPasswordException(IUserService.INVALID_PASSWORD);
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

    @Override
    public EntityResult userInsert(Map<?, ?> attrMap) {
        EntityResult result;

        try {
            Map<String, ? super Object> userNameFilter = new HashMap<>();
            userNameFilter.put(UserDAO.EMAIL, attrMap.get(UserDAO.EMAIL));

            if (!userQuery(userNameFilter, List.of(UserDAO.ID)).isEmpty()) {
                throw new UserAlreadyExistsException("A user with this email address already exists");
            }

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

    @Override
    public EntityResult userUpdate(Map<?, ?> attrMap, Map<?, ?> keyMap) {
        return null;
    }

    @Override
    public EntityResult userDelete(Map<?, ?> keyMap) {
        return null;
    }
}
