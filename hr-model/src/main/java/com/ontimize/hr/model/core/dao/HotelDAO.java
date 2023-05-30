package com.ontimize.hr.model.core.dao;

import com.ontimize.jee.server.dao.common.ConfigurationFile;
import com.ontimize.jee.server.dao.jdbc.OntimizeJdbcDaoSupport;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

@Lazy
@Repository(value = "HotelDAO")
@ConfigurationFile(
        configurationFile = "dao/HotelDAO.xml",
        configurationFilePlaceholder = "dao/placeholders.properties")
public class HotelDAO extends OntimizeJdbcDaoSupport {

    public static final String ID = "ID";
    public static final String NAME = "NAME";
    public static final String NUMBER_OF_FLOORS = "NUMBER_OF_FLOORS";


}