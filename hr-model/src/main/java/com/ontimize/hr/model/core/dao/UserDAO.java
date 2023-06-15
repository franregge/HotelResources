package com.ontimize.hr.model.core.dao;


import com.ontimize.jee.server.dao.common.ConfigurationFile;
import com.ontimize.jee.server.dao.jdbc.OntimizeJdbcDaoSupport;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

@Lazy
@Repository(value = "UserDAO")
@ConfigurationFile(
        configurationFile = "dao/UserDAO.xml",
        configurationFilePlaceholder = "dao/placeholders.properties")

public class UserDAO extends OntimizeJdbcDaoSupport {

    public static final String ID = "id";
    public static final String USER_NAME = "user_name";
    public static final String SURNAME1 = "surname1";
    public static final String SURNAME2 = "surname2";
    public static final String ID_DOCUMENT = "id_document";
    public static final String COUNTRY_ID = "country_id";
    public static final String PHONE_NUMBER = "phone_number";
    public static final String EMAIL = "email";
    public static final String USER_PASSWORD = "user_password";
    public static final String ROLE_ID = "role_id";
    public static final String EMPLOYEE_ROLE_ID = "6";

}
