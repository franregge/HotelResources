package com.ontimize.hr.model.core.dao;

import com.ontimize.jee.server.dao.common.ConfigurationFile;
import com.ontimize.jee.server.dao.jdbc.OntimizeJdbcDaoSupport;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

@Lazy
@Repository(value = "BookingDAO")
@ConfigurationFile(
        configurationFile = "dao/BookingDAO.xml",
        configurationFilePlaceholder = "dao/placeholders.properties")
public class BookingDAO extends OntimizeJdbcDaoSupport {

    public static final String ID = "id";
    public static final String ROOM_ID = "room_id";
    public static final String ARRIVAL_DATE = "arrival_date";
    public static final String DEPARTURE_DATE = "departure_date";

    public static final String USER_LOGIN_NAME = "user_login_name";

}