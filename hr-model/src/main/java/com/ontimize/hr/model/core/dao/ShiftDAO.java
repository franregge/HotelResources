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
    public static final String MON = "mon";
    public static final String TUE = "tue";
    public static final String WED = "wed";
    public static final String THU = "thu";
    public static final String FRI = "fri";
    public static final String SAT = "sat";
    public static final String SUN = "sun";
    public static final String LOGIN_NAMES = "login_names";
    public static final String ROLE_NAME = "role_name";

}
