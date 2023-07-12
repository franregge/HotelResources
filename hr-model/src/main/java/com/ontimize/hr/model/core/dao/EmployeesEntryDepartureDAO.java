package com.ontimize.hr.model.core.dao;

import com.ontimize.jee.server.dao.common.ConfigurationFile;
import com.ontimize.jee.server.dao.jdbc.OntimizeJdbcDaoSupport;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

@Lazy
@Repository(value = "EmployeesEntryDepartureDAO")
@ConfigurationFile(
        configurationFile = "dao/EmployeesEntryDepartureDAO.xml",
        configurationFilePlaceholder = "dao/placeholders.properties")
public class EmployeesEntryDepartureDAO extends OntimizeJdbcDaoSupport {
    public static final String ID = "id";
    public static final String LOGIN_NAME = "login_name";
    public static final String ENTRY = "entry";
    public static final String DEPARTURE = "departure";
    public static final String WORKING_DAY = "working_day";
    public static final String E_ENTRY_SAVED = "This employee has an entry saved for this day";
    public static final String E_NO_CLOCK_IN = "This employee has not clocked in for the day";
    public static final String OPERATION_SUCCESS = "Your entry has been saved";

    public static final String CLOCK_OUT_SUCCESS = "Clocked out successfully";
    public static final String E_ALREADY_CLOCKED_OUT = "This employee has already clocked out for the day";
    public static final String E_NOT_EMPLOYEE = "This user is not an employee";
    public static final String E_CANNOT_CLOCK_IN_OTHERS = "You can not clock in another user";
}
