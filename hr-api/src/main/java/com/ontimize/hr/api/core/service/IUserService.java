package com.ontimize.hr.api.core.service;


import java.util.List;
import java.util.Map;

import com.ontimize.jee.common.dto.EntityResult;


public interface IUserService {

    public EntityResult userQuery(Map<?, ?> keyMap, List<?> attrList);

    public EntityResult userInsert(Map<?, ?> attrMap);

    public EntityResult userUpdate(Map<?, ?> attrMap, Map<?, ?> keyMap);

    public EntityResult userDelete(Map<?, ?> keyMap);

    String INVALID_ID_DOCUMENT = "The id document number is not valid";

    String PASS_LENGTH_TOO_SHORT = "The password length has to be over 7";

    String PASS_HAS_NO_LETTER = "The password must have at least one letter";

    String PASS_HAS_NO_NUMBER = "The password must have at least one number";

    String PASS_HAS_NO_CAPITAL_LETTER = "The password must have at least one capital letter";

    String PASS_HAS_NO_LOWER_CASE_LETTER = "The password must have at least one lower-case letter";

    String USER_INSERT_SUCCESS= "User inserted successfully";
}
