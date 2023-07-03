package com.ontimize.hr.model.core.dao;

import com.ontimize.jee.server.dao.common.ConfigurationFile;
import com.ontimize.jee.server.dao.jdbc.OntimizeJdbcDaoSupport;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

@Lazy
@Repository(value = "ShiftDAO")
@ConfigurationFile(
        configurationFile = "dao/ShiftDAO.xml",
        configurationFilePlaceholder = "dao/placeholders.properties")
public class ShiftDAO extends OntimizeJdbcDaoSupport {

    public static final String ID = "id";
    public static final String ROLE_ID = "role_id";
    public static final String MON = "monday";
    public static final String TUE = "tuesday";
    public static final String WED = "wednesday";
    public static final String THU = "thursday";
    public static final String FRI = "friday";
    public static final String SAT = "saturday";
    public static final String SUN = "sunday";
    public static final String LOGIN_NAMES = "login_names";

    public static final String LOGIN_NAME = "login_name";
    public static final String ROLE_NAME = "role_name";

    public static final String ROLENAME = "rolename";
    public static final String E_EMPLOYEES_NOT_FOUND = "The following employees were not found: ";

}
