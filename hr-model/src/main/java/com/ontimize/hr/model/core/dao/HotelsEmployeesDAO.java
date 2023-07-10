package com.ontimize.hr.model.core.dao;

import com.ontimize.jee.server.dao.common.ConfigurationFile;
import com.ontimize.jee.server.dao.jdbc.OntimizeJdbcDaoSupport;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

@Lazy
@Repository(value = "HotelsEmployeesDAO")
@ConfigurationFile(
        configurationFile = "dao/HotelsEmployeesDAO.xml",
        configurationFilePlaceholder = "dao/placeholders.properties")
public class HotelsEmployeesDAO extends OntimizeJdbcDaoSupport {

    public static final String ID = "id";
    public static final String HOTEL_ID = "hotel_id";
    public static final String LOGIN_NAME = "login_name";
}

