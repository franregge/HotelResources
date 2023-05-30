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

    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String NUMBER_OF_FLOORS = "number_of_floors";
    public static final String QUERY_BOOKINGS_IN_HOTEL = "bookingsInHotel";


}