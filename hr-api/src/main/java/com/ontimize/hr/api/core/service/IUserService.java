package com.ontimize.hr.api.core.service;


import java.util.List;
import java.util.Map;

import com.ontimize.jee.common.dto.EntityResult;


public interface IUserService {

    EntityResult userQuery(Map<?, ?> keyMap, List<?> attrList);

    EntityResult userInsert(Map<? super Object, ? super Object> attrMap);

    EntityResult userUpdate(Map<?, ?> attrMap, Map<?, ?> keyMap);

    EntityResult userDelete(Map<?, ?> keyMap);

    EntityResult userIdentifiedQuery(Map<?, ?> filter, List<?> attrList, String queryId);

    String ERR_INVALID_ID_DOCUMENT = "The id document is not valid";

    String NO_USER_WITH_ID = "No user with this id";

    String DELETION_SUCCESS = "User deleted successfully";

    String USER_NOT_FOUND = "No user with this id could be found";

    String PASS_LENGTH_TOO_SHORT = "The password length has to be over 7";

    String PASS_HAS_NO_LETTER = "The password must have at least one letter";

    String PASS_HAS_NO_NUMBER = "The password must have at least one number";

    String PASS_HAS_NO_CAPITAL_LETTER = "The password must have at least one capital letter";

    String PASS_HAS_NO_LOWER_CASE_LETTER = "The password must have at least one lower-case letter";
    String USER_INSERT_SUCCESS = "User inserted successfully";
    String PASS_INSTRUCTIONS = "The password must have at least one number,one Capital letter, one lower case letter and the password length has to be over 7";
    String ONLY_MANAGER_ADD_EMPLOYEES = "Cannot add employees with your role";
    String NO_USER_FOUND = "Can't find users with this login name";
    String WRONG_ROLE = "You cant update users with this role";

    String ERR_INVALID_EMAIL = "You must provide a valid email";

    String ERR_INVALID_PHONE_NUMBER = "You must provide a valid phone number";

    String ERR_INVALID_COUNTRY_ID = "You must provide a valid country id";
    Integer CLIENT_ROLE_ID = 4;
}
