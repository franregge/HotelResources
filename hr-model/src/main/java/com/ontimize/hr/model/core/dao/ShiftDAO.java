package com.ontimize.hr.model.core.dao;

import com.ontimize.jee.server.dao.common.ConfigurationFile;
import com.ontimize.jee.server.dao.jdbc.OntimizeJdbcDaoSupport;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

@Lazy
@Repository(value = "ShiftDAO")
@ConfigurationFile(
        configurationFile = "dao/ShiftDAO.xml",
        configurationFilePlaceholder = "dao/placeholders.properties")
public class ShiftDAO extends OntimizeJdbcDaoSupport {

    public final String ID = "id";
    public final String ROLE_ID = "role_id";
    public final String MON = "mon";
    public final String TUE = "tue";
    public final String WED = "wed";
    public final String THU = "thu";
    public final String FRI = "fri";
    public final String SAT = "sat";
    public final String SUN = "sun";

}
