package com.ontimize.hr.model.core.service;

import com.ontimize.hr.api.core.service.IShiftService;
import com.ontimize.hr.api.core.service.IUserService;
import com.ontimize.hr.api.core.service.exception.InvalidPasswordException;
import com.ontimize.hr.api.core.service.exception.InvalidWeeklyHoursException;
import com.ontimize.hr.model.core.dao.ShiftDAO;
import com.ontimize.hr.model.core.dao.UserDAO;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.common.security.PermissionsProviderSecured;
import com.ontimize.jee.server.dao.DefaultOntimizeDaoHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@Lazy
@Service("ShiftService")
public class ShiftService implements IShiftService {

    @Autowired
    private DefaultOntimizeDaoHelper daoHelper;

    @Autowired
    private ShiftDAO shiftDAO;

    @Secured({PermissionsProviderSecured.SECURED})
    @Override
    public EntityResult shiftQuery(Map<?, ?> keyMap, List<?> attrList) {
        return null;
    }

    @Override
    public EntityResult shiftInsert(Map<? super Object, ? super Object> attrMap) {
        return null;
    }

    @Override
    public EntityResult shiftUpdate(Map<?, ?> attrMap, Map<?, ?> keyMap) {
        return null;
    }

    @Override
    public EntityResult shiftDelete(Map<?, ?> keyMap) {
        return null;
    }

    private void validateWeeklyHours(Map<?, ?> attrMap) throws InvalidWeeklyHoursException {
        Map<?, ?> monday = (Map) attrMap.get(shiftDAO.MON);
        String mondayStart = (String) monday.get("start");
        String mondayEnd = (String) monday.get("end");
        Map<?, ?> tuesday = (Map) attrMap.get(shiftDAO.TUE);
        String tuesdayStart = (String) tuesday.get("start");
        String tuesdayEnd = (String) tuesday.get("end");
        Map<?, ?> wednesday = (Map) attrMap.get(shiftDAO.WED);
        String wednesdayStart = (String) wednesday.get("start");
        String wednesdayEnd = (String) wednesday.get("end");
        Map<?, ?> thursday = (Map) attrMap.get(shiftDAO.THU);
        String thursdayStart = (String) thursday.get("start");
        String thursdayEnd = (String) thursday.get("end");
        Map<?, ?> friday = (Map) attrMap.get(shiftDAO.FRI);
        String fridayStart = (String) friday.get("start");
        String fridayEnd = (String) friday.get("end");
        Map<?, ?> saturday = (Map) attrMap.get(shiftDAO.SAT);
        String SaturdayStart = (String) saturday.get("start");
        String saturdayEnd = (String) saturday.get("end");
        Map<?, ?> sunday = (Map) attrMap.get(shiftDAO.SUN);
        String sundayStart = (String) sunday.get("start");
        String sundayEnd = (String) sunday.get("end");

        LocalTime mondayStartTime = LocalTime.parse(mondayStart);
        LocalTime mondayEndTime = LocalTime.parse(mondayEnd);
        Integer mondayDuration = Integer.valueOf((int) Duration.between(mondayStartTime, mondayEndTime).toHours());

        LocalTime tuesdayStartTime = LocalTime.parse(tuesdayStart);
        LocalTime tuesdayEndTime = LocalTime.parse(tuesdayEnd);
        Integer tuesdayDuration = Integer.valueOf((int) Duration.between(tuesdayStartTime, tuesdayEndTime).toHours());

        LocalTime wednesdayStartTime = LocalTime.parse(wednesdayStart);
        LocalTime wednesdayEndTime = LocalTime.parse(wednesdayEnd);
        Integer wednesdayDuration = Integer.valueOf((int) Duration.between(wednesdayStartTime, wednesdayEndTime).toHours());

        LocalTime thursdayStartTime = LocalTime.parse(thursdayStart);
        LocalTime thursdayEndTime = LocalTime.parse(thursdayEnd);
        Integer thursdayDuration = Integer.valueOf((int) Duration.between(thursdayStartTime, thursdayEndTime).toHours());

        LocalTime fridayStartTime = LocalTime.parse(fridayStart);
        LocalTime fridayEndTime = LocalTime.parse(fridayEnd);
        Integer fridayDuration = Integer.valueOf((int) Duration.between(fridayStartTime, fridayEndTime).toHours());

        LocalTime saturdayStartTime = LocalTime.parse(SaturdayStart);
        LocalTime saturdayEndTime = LocalTime.parse(saturdayEnd);
        Integer saturdayDuration = Integer.valueOf((int) Duration.between(saturdayStartTime, saturdayEndTime).toHours());

        LocalTime sundayStartTime = LocalTime.parse(sundayStart);
        LocalTime sundayEndTime = LocalTime.parse(sundayEnd);
        Integer sundayDuration = Integer.valueOf((int) Duration.between(sundayStartTime, sundayEndTime).toHours());

        if(mondayDuration + tuesdayDuration + wednesdayDuration + thursdayDuration + fridayDuration + saturdayDuration + sundayDuration > 40){
            throw new InvalidWeeklyHoursException(IShiftService.MORE_THAN_40H);
        }
    }
}
