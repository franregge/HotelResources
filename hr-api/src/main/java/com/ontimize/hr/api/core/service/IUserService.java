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

    String USER_INSERT_SUCCESS= "User inserted successfully";

    String INVALID_PASSWORD = "The password length has to be over 7, and contain one capital letter, one lower-case letter, and one number";

}
