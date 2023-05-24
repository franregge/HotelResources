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
    public static final String CHECK_IN_DATE = "check_in_date";
    public static final String CHECK_OUT_DATE = "check_out_date";
    public static final String DNI = "dni";
    public static final String NAME = "name";
    public static final String SURNAME1 = "surname1";
    public static final String SURNAME2 = "surname2";



}