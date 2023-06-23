package com.ontimize.hr.model.core.dao;

import com.ontimize.jee.server.dao.common.ConfigurationFile;
import com.ontimize.jee.server.dao.jdbc.OntimizeJdbcDaoSupport;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

@Lazy
@Repository(value = "UsersRolesDAO")
@ConfigurationFile(
        configurationFile = "dao/UsersRolesDAO.xml",
        configurationFilePlaceholder = "dao/placeholders.properties")

public class UsersRolesDAO extends OntimizeJdbcDaoSupport {

    public static final String ID = "id";
    public static final String LOGIN_NAME = "login_name";
    public static final String ROLE_NAME = "rolename";

    public static final String ROLE_ID = "role_id";
}
