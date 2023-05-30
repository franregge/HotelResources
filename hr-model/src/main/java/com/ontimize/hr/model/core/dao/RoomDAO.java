package com.ontimize.hr.model.core.dao;

import com.ontimize.jee.server.dao.common.ConfigurationFile;
import com.ontimize.jee.server.dao.jdbc.OntimizeJdbcDaoSupport;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

@Lazy
@Repository(value = "RoomDAO")
@ConfigurationFile(
        configurationFile = "dao/RoomDAO.xml",
        configurationFilePlaceholder = "dao/placeholders.properties")
public class RoomDAO extends OntimizeJdbcDaoSupport {

    public static final String ID = "ID";
    public static final String ROOM_NUMBER = "ROOM_NUMBER";
    public static final String HOTEL_ID = "HOTEL_ID";


}