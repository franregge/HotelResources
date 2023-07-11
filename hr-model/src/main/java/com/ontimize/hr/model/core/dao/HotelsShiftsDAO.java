package com.ontimize.hr.model.core.dao;

import com.ontimize.jee.server.dao.common.ConfigurationFile;
import com.ontimize.jee.server.dao.jdbc.OntimizeJdbcDaoSupport;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

@Lazy
@Repository(value = "HotelsShiftsDAO")
@ConfigurationFile(
        configurationFile = "dao/HotelsShiftsDAO.xml",
        configurationFilePlaceholder = "dao/placeholders.properties")
public class HotelsShiftsDAO extends OntimizeJdbcDaoSupport {

    public static final String ID = "id";
    public static final String SHIFT_ID = "shift_id";
    public static final String HOTEL_ID = "hotel_id";
}

