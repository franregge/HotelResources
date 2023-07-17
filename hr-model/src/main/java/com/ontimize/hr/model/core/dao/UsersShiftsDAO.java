package com.ontimize.hr.model.core.dao;

import com.ontimize.jee.server.dao.common.ConfigurationFile;
import com.ontimize.jee.server.dao.jdbc.OntimizeJdbcDaoSupport;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;


@Lazy
@Repository(value = "UsersShiftsDAO")
@ConfigurationFile(
        configurationFile = "dao/UsersShiftsDAO.xml",
        configurationFilePlaceholder = "dao/placeholders.properties")

public class UsersShiftsDAO extends OntimizeJdbcDaoSupport {

    public static final String SHIFT_ID = "shift_id";
    public static final String LOGIN_NAME = "login_name";

    public static final String Q_USERS_SHIFTS = "userShift";

}
