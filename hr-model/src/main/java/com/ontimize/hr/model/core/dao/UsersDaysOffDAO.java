package com.ontimize.hr.model.core.dao;

import com.ontimize.jee.server.dao.common.ConfigurationFile;
import com.ontimize.jee.server.dao.jdbc.OntimizeJdbcDaoSupport;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

@Lazy
@Repository(value = "UsersDaysOffDAO")
@ConfigurationFile(
        configurationFile = "dao/UsersDaysOffDAO.xml",
        configurationFilePlaceholder = "dao/placeholders.properties")
public class UsersDaysOffDAO extends OntimizeJdbcDaoSupport {

    public static final String ID = "id";
    public static final String LOGIN_NAME = "login_name";
    public static final String DAY = "day";
}
