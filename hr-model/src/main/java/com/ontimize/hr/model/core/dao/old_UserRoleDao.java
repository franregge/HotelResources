package com.ontimize.hr.model.core.dao;


import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

import com.ontimize.jee.server.dao.common.ConfigurationFile;
import com.ontimize.jee.server.dao.jdbc.OntimizeJdbcDaoSupport;


@Repository(value = "old_UserRoleDao")
@Lazy
@ConfigurationFile(
        configurationFile = "dao/old_UserRoleDao.xml",
        configurationFilePlaceholder = "dao/placeholders.properties")
public class old_UserRoleDao extends OntimizeJdbcDaoSupport {

}
