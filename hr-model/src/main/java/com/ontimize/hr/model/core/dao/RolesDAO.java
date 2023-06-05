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

public class RolesDAO extends OntimizeJdbcDaoSupport {
    public static final String ID = "id";
    public static final String NAME ="name";
}
