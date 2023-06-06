package com.ontimize.hr.model.core.dao;


import com.ontimize.jee.server.dao.common.ConfigurationFile;
import com.ontimize.jee.server.dao.jdbc.OntimizeJdbcDaoSupport;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

@Lazy
@Repository(value = "UserRoleDAO")
@ConfigurationFile(
        configurationFile = "dao/UserRoleDAO.xml",
        configurationFilePlaceholder = "dao/placeholders.properties")

public class UserRoleDAO extends OntimizeJdbcDaoSupport {
    public static final String ID = "id";
    public static final String NAME ="name";
}
