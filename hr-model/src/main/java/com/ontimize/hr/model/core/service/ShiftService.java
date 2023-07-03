package com.ontimize.hr.model.core.service;

import com.ontimize.hr.api.core.service.IShiftService;
import com.ontimize.hr.api.core.service.IUserService;
import com.ontimize.hr.api.core.service.exception.*;
import com.ontimize.hr.model.core.dao.*;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.common.dto.EntityResultMapImpl;
import com.ontimize.jee.common.security.PermissionsProviderSecured;
import com.ontimize.jee.server.dao.DefaultOntimizeDaoHelper;
import org.postgresql.util.PSQLException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import java.sql.SQLIntegrityConstraintViolationException;
import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Lazy
@Service("ShiftService")
public class ShiftService implements IShiftService {

    @Autowired
    private DefaultOntimizeDaoHelper daoHelper;
    @Autowired
    private ShiftDAO shiftDAO;
    @Autowired
    private UserRoleDAO userRoleDAO;
    @Autowired
    private UserService userService;

    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private UsersDaysOffDAO usersDaysOffDao;
    @Autowired
    private UserDAO userDAO;
    @Autowired
    private UsersShiftsDAO usersShiftsDAO;
    @Secured({PermissionsProviderSecured.SECURED})
    @Override
    public EntityResult shiftQuery(Map<?, ?> keyMap, List<?> attrList) {
        return null;
    }

    @SuppressWarnings("unchecked")
    @Secured({PermissionsProviderSecured.SECURED})
    @Override
    public EntityResult shiftInsert(Map<? super Object, ? super Object> attrMap) {
        EntityResult result;
        try {
            validateWeeklyHours(attrMap);
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
            String saturdayStart = (String) saturday.get("start");
            String saturdayEnd = (String) saturday.get("end");
            Map<?, ?> sunday = (Map) attrMap.get(shiftDAO.SUN);
            String sundayStart = (String) sunday.get("start");
            String sundayEnd = (String) sunday.get("end");

            roleMatcher(attrMap);
            String monShift = mondayStart + "-" + mondayEnd;
            String tueShift = tuesdayStart + "-" + tuesdayEnd;
            String wedShift = wednesdayStart + "-" + wednesdayEnd;
            String thurShift = thursdayStart + "-" + thursdayEnd;
            String fridShift = fridayStart + "-" + fridayEnd;
            String satShift = saturdayStart + "-" + saturdayEnd;
            String sunShift = sundayStart + "-" + sundayEnd;

            attrMap.put(ShiftDAO.MON, monShift);
            attrMap.put(ShiftDAO.TUE, tueShift);
            attrMap.put(ShiftDAO.WED, wedShift);
            attrMap.put(ShiftDAO.THU, thurShift);
            attrMap.put(ShiftDAO.FRI, fridShift);
            attrMap.put(ShiftDAO.SAT, satShift);
            attrMap.put(ShiftDAO.SUN, sunShift);

            List<String> shiftEmployees = (List<String>) attrMap.get(ShiftDAO.LOGIN_NAMES);

            if (shiftEmployees != null) {
                Map<String, String> roleEmployeesFilter = new HashMap<>();
                roleEmployeesFilter.put(UserDAO.ROLE_NAME, (String) attrMap.get(ShiftDAO.ROLE_NAME));

                List<String> roleEmployees = (List<String>) userService
                        .userIdentifiedQuery(
                                roleEmployeesFilter,
                                List.of(UserDAO.LOGIN_NAME),
                                UserDAO.Q_EMPLOYEE_QUERY)
                        .get(UserDAO.LOGIN_NAME);

                List<String> invalidEmployees = shiftEmployees
                        .stream()
                        .filter(employee -> !roleEmployees.contains(employee))
                        .collect(Collectors.toList());

                if (!invalidEmployees.isEmpty()) {
                    throw new EmployeeNotFoundException(
                            ShiftDAO.E_EMPLOYEES_NOT_FOUND + String.join(", ", invalidEmployees)
                    );
                }
            }

            Map<String, String> roleIdFilter = new HashMap<>();
            roleIdFilter.put(UserRoleDAO.NAME, (String) attrMap.get(ShiftDAO.ROLE_NAME));
            Integer roleId = (Integer) daoHelper
                    .query(
                            userRoleDAO,
                            roleIdFilter,
                            List.of(UserRoleDAO.ID)
                    ).getRecordValues(0)
                    .get(UserRoleDAO.ID);

            assert roleId != null;
            attrMap.put(ShiftDAO.ROLE_ID, roleId);
            result = this.daoHelper.insert(this.shiftDAO, attrMap);
            result.setCode(EntityResult.OPERATION_SUCCESSFUL_SHOW_MESSAGE);
            result.setMessage(IShiftService.INSERTION_SUCCESS);
        } catch (SQLIntegrityConstraintViolationException e) {
            result = new EntityResultMapImpl();
            result.setMessage(IShiftService.MULTIPLE_SHIFTS);
            result.setCode(EntityResult.OPERATION_WRONG);
        }  catch (Exception e){
            result = new EntityResultMapImpl();
            result.setMessage(e.getMessage());
            result.setCode(EntityResult.OPERATION_WRONG);
            e.printStackTrace();
        }

        return result;
    }

    @Secured({PermissionsProviderSecured.SECURED})
    @Override
    public EntityResult shiftUpdate(Map<? super Object, ? super Object> attrMap, Map<?, ?> keyMap) throws UserDoesNotExistException {

        //TODO: el campo de empleados en el turno puede ser nulo

        EntityResult result;
        try {

            List<String> attrbuteQueriedList = List.of(ShiftDAO.MON, ShiftDAO.SUN, ShiftDAO.SAT, ShiftDAO.FRI, ShiftDAO.THU, ShiftDAO.WED, ShiftDAO.TUE, ShiftDAO.LOGIN_NAMES, ShiftDAO.ROLE_ID);

            Map<?, ?> originalShiftDays = new HashMap<>();
            originalShiftDays = daoHelper.query(shiftDAO, keyMap, attrbuteQueriedList).getRecordValues(0);

            //Map<? super Object,? super Object> roleEmployeesFilterValidateExistence = new HashMap<>();
            //roleEmployeesFilterValidateExistence.put(ShiftDAO.ROLE_ID,originalShiftDays.get(ShiftDAO.ROLE_ID));
//
            //EntityResult userQueryResult = daoHelper.query(userDAO, roleEmployeesFilterValidateExistence,List.of(UserDAO.LOGIN_NAME));
            //userQueryResult.getRecordValues(0).get(ShiftDAO.LOGIN_NAMES);
            //if(!(userQueryResult).contains(attrMap.get(ShiftDAO.LOGIN_NAMES))){
            //    throw new UserDoesNotExistException(IUserService.NO_USER_FOUND);
            //}
//
//
            Map<String, String> monday = (Map<String, String>) attrMap.get(shiftDAO.MON);
            if (monday == null) {
                monday = new HashMap<>();
                String[] arrayHours = ((String) (originalShiftDays.get(ShiftDAO.MON))).split("-");
                monday.put("start", arrayHours[0]);
                monday.put("end", arrayHours[1]);
                attrMap.put(ShiftDAO.MON, monday);

            }
            String mondayStart = monday.get("start");
            String mondayEnd = monday.get("end");

            Map<String, String> tuesday = (Map<String, String>) attrMap.get(shiftDAO.TUE);
            if (tuesday == null) {
                tuesday = new HashMap<>();
                String[] arrayHours = ((String) (originalShiftDays.get(ShiftDAO.TUE))).split("-");
                tuesday.put("start", arrayHours[0]);
                tuesday.put("end", arrayHours[1]);
                attrMap.put(ShiftDAO.TUE, tuesday);
            }
            String tuesdayStart = (String) tuesday.get("start");
            String tuesdayEnd = (String) tuesday.get("end");

            Map<String, String> wednesday = (Map<String, String>) attrMap.get(shiftDAO.WED);
            if (wednesday == null) {
                wednesday = new HashMap<>();
                String[] arrayHours = ((String) (originalShiftDays.get(ShiftDAO.WED))).split("-");
                wednesday.put("start", arrayHours[0]);
                wednesday.put("end", arrayHours[1]);
                attrMap.put(ShiftDAO.WED, wednesday);
            }
            String wednesdayStart = (String) wednesday.get("start");
            String wednesdayEnd = (String) wednesday.get("end");

            Map<String, String> thursday = (Map) attrMap.get(shiftDAO.THU);
            if (thursday == null) {
                thursday = new HashMap<>();
                String[] arrayHours = ((String) (originalShiftDays.get(ShiftDAO.THU))).split("-");
                thursday.put("start", arrayHours[0]);
                thursday.put("end", arrayHours[1]);
                attrMap.put(ShiftDAO.THU, thursday);
            }
            String thursdayStart = (String) thursday.get("start");
            String thursdayEnd = (String) thursday.get("end");

            Map<String, String> friday = (Map) attrMap.get(shiftDAO.FRI);
            if (friday == null) {
                friday = new HashMap<>();
                String[] arrayHours = ((String) (originalShiftDays.get(ShiftDAO.FRI))).split("-");
                friday.put("start", arrayHours[0]);
                friday.put("end", arrayHours[1]);
                attrMap.put(ShiftDAO.FRI, friday);
            }
            String fridayStart = (String) friday.get("start");
            String fridayEnd = (String) friday.get("end");

            Map<String, String> saturday = (Map) attrMap.get(shiftDAO.SAT);
            if (saturday == null) {
                saturday = new HashMap<>();
                String[] arrayHours = ((String) (originalShiftDays.get(ShiftDAO.SAT))).split("-");
                saturday.put("start", arrayHours[0]);
                saturday.put("end", arrayHours[1]);
                attrMap.put(ShiftDAO.SAT, saturday);
            }
            String saturdayStart = (String) saturday.get("start");
            String saturdayEnd = (String) saturday.get("end");

            Map<String, String> sunday = (Map) attrMap.get(shiftDAO.SUN);
            if (sunday == null) {
                sunday = new HashMap<>();
                String[] arrayHours = ((String) (originalShiftDays.get(ShiftDAO.SUN))).split("-");
                sunday.put("start", arrayHours[0]);
                sunday.put("end", arrayHours[1]);
                attrMap.put(ShiftDAO.SUN, sunday);
            }
            String sundayStart = (String) sunday.get("start");
            String sundayEnd = (String) sunday.get("end");

            if(attrMap.get(ShiftDAO.ROLE_ID)==null){
                attrMap.put(ShiftDAO.ROLE_ID,originalShiftDays.get(ShiftDAO.ROLE_ID));
            }

            validateUpdateShift(attrMap, keyMap);

            updateRoleMatcher(attrMap);

            String monShift = mondayStart + "-" + mondayEnd;
            String tueShift = tuesdayStart + "-" + tuesdayEnd;
            String wedShift = wednesdayStart + "-" + wednesdayEnd;
            String thurShift = thursdayStart + "-" + thursdayEnd;
            String fridShift = fridayStart + "-" + fridayEnd;
            String satShift = saturdayStart + "-" + saturdayEnd;
            String sunShift = sundayStart + "-" + sundayEnd;

            attrMap.put(ShiftDAO.MON, monShift);
            attrMap.put(ShiftDAO.TUE, tueShift);
            attrMap.put(ShiftDAO.WED, wedShift);
            attrMap.put(ShiftDAO.THU, thurShift);
            attrMap.put(ShiftDAO.FRI, fridShift);
            attrMap.put(ShiftDAO.SAT, satShift);
            attrMap.put(ShiftDAO.SUN, sunShift);


            List<String> shiftEmployees = (List<String>) attrMap.get(ShiftDAO.LOGIN_NAMES);

            if (shiftEmployees != null) {
                Map<String, String> roleEmployeesFilter = new HashMap<>();
                roleEmployeesFilter.put(UserDAO.ROLE_NAME, (String) attrMap.get(ShiftDAO.ROLENAME));

                List<String> roleEmployees = (List<String>) userService
                        .userIdentifiedQuery(
                                roleEmployeesFilter,
                                List.of(UserDAO.LOGIN_NAME),
                                UserDAO.Q_EMPLOYEE_QUERY)
                        .get(UserDAO.LOGIN_NAME);

                List<String> invalidEmployees = shiftEmployees
                        .stream()
                        .filter(employee -> !roleEmployees.contains(employee))
                        .collect(Collectors.toList());

                if (!invalidEmployees.isEmpty()) {
                    throw new EmployeeNotFoundException(
                            ShiftDAO.E_EMPLOYEES_NOT_FOUND + String.join(", ", invalidEmployees)
                    );
                }
            }


            result = this.daoHelper.update(this.shiftDAO, attrMap, keyMap);
            result.setCode(EntityResult.OPERATION_SUCCESSFUL_SHOW_MESSAGE);
            result.setMessage(IShiftService.UPDATE_SUCCESS);
        } catch (SQLIntegrityConstraintViolationException e) {
            result = new EntityResultMapImpl();
            result.setMessage(IShiftService.MULTIPLE_SHIFTS);
            result.setCode(EntityResult.OPERATION_WRONG);

        } catch (Exception e) {
            result = new EntityResultMapImpl();
            result.setMessage(e.getMessage());
            result.setCode(EntityResult.OPERATION_WRONG);
            e.printStackTrace();

        }

        return result;
    }

    @Override
    public EntityResult shiftDelete(Map<?, ?> keyMap) {
        return null;
    }


    private void validateWeeklyHours(Map<?, ?> attrMap) throws Exception {
        //TODO: separar resta de días libres
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
        Integer mondayDuration = Integer.valueOf((int) Duration.between(mondayStartTime, mondayEndTime).toMinutes());

        LocalTime tuesdayStartTime = LocalTime.parse(tuesdayStart);
        LocalTime tuesdayEndTime = LocalTime.parse(tuesdayEnd);
        Integer tuesdayDuration = Integer.valueOf((int) Duration.between(tuesdayStartTime, tuesdayEndTime).toMinutes());

        LocalTime wednesdayStartTime = LocalTime.parse(wednesdayStart);
        LocalTime wednesdayEndTime = LocalTime.parse(wednesdayEnd);
        Integer wednesdayDuration = Integer.valueOf((int) Duration.between(wednesdayStartTime, wednesdayEndTime).toMinutes());

        LocalTime thursdayStartTime = LocalTime.parse(thursdayStart);
        LocalTime thursdayEndTime = LocalTime.parse(thursdayEnd);
        Integer thursdayDuration = Integer.valueOf((int) Duration.between(thursdayStartTime, thursdayEndTime).toMinutes());

        LocalTime fridayStartTime = LocalTime.parse(fridayStart);
        LocalTime fridayEndTime = LocalTime.parse(fridayEnd);
        Integer fridayDuration = Integer.valueOf((int) Duration.between(fridayStartTime, fridayEndTime).toMinutes());

        LocalTime saturdayStartTime = LocalTime.parse(SaturdayStart);
        LocalTime saturdayEndTime = LocalTime.parse(saturdayEnd);
        Integer saturdayDuration = Integer.valueOf((int) Duration.between(saturdayStartTime, saturdayEndTime).toMinutes());

        LocalTime sundayStartTime = LocalTime.parse(sundayStart);
        LocalTime sundayEndTime = LocalTime.parse(sundayEnd);
        Integer sundayDuration = Integer.valueOf((int) Duration.between(sundayStartTime, sundayEndTime).toMinutes());

        if (mondayEndTime.isBefore(mondayStartTime) || tuesdayEndTime.isBefore(tuesdayStartTime) || wednesdayEndTime.isBefore(wednesdayStartTime) ||
                thursdayEndTime.isBefore(thursdayStartTime) || fridayEndTime.isBefore(fridayStartTime) || saturdayEndTime.isBefore(saturdayStartTime) ||
                sundayEndTime.isBefore(sundayStartTime)) {
            throw new InvalidShiftException(END_BEFORE_START);
        }
        Map<? super Object, ? super Object> dayDurations = new HashMap<>();
        dayDurations.put(ShiftDAO.MON, mondayDuration);
        dayDurations.put(ShiftDAO.TUE, tuesdayDuration);
        dayDurations.put(ShiftDAO.WED, wednesdayDuration);
        dayDurations.put(ShiftDAO.THU, thursdayDuration);
        dayDurations.put(ShiftDAO.FRI, fridayDuration);
        dayDurations.put(ShiftDAO.SAT, saturdayDuration);
        dayDurations.put(ShiftDAO.SUN, sundayDuration);


        List<String> employeeLoginNames = (List) attrMap.get(ShiftDAO.LOGIN_NAMES);
        int weeklyMinutes = mondayDuration + tuesdayDuration + wednesdayDuration + thursdayDuration + fridayDuration + saturdayDuration + sundayDuration;
        if (employeeLoginNames == null) {
            throw new Exception("No employees assigned");
        }
        for (String employeeLoginName : employeeLoginNames) {
            int employeeWeeklyMinutes = weeklyMinutes;

            List<String> queriedAtributeList = List.of(UsersDaysOffDAO.DAY);
            Map<String, String> employeeLoginNameFilter = new HashMap<>();
            employeeLoginNameFilter.put(UserDAO.LOGIN_NAME, employeeLoginName);

            EntityResult daysOffPerEmployee = this.daoHelper.query(usersDaysOffDao, employeeLoginNameFilter, queriedAtributeList);

            List<String> daysOffListPerEmployee = (List<String>) daysOffPerEmployee.get(UsersDaysOffDAO.DAY);
            for (String day : daysOffListPerEmployee) {
                switch (day) {
                    case (ShiftDAO.MON):
                        employeeWeeklyMinutes = employeeWeeklyMinutes - mondayDuration;
                        break;
                    case (ShiftDAO.TUE):
                        employeeWeeklyMinutes = employeeWeeklyMinutes - tuesdayDuration;
                        break;
                    case (ShiftDAO.THU):
                        employeeWeeklyMinutes = employeeWeeklyMinutes - thursdayDuration;
                        break;
                    case (ShiftDAO.WED):
                        employeeWeeklyMinutes = employeeWeeklyMinutes - wednesdayDuration;
                        break;
                    case (ShiftDAO.FRI):
                        employeeWeeklyMinutes = employeeWeeklyMinutes - fridayDuration;
                        break;
                    case (ShiftDAO.SAT):
                        employeeWeeklyMinutes = employeeWeeklyMinutes - saturdayDuration;
                        break;
                    case (ShiftDAO.SUN):
                        employeeWeeklyMinutes = employeeWeeklyMinutes - sundayDuration;
                        break;
                    default:
                        throw new Exception(IShiftService.E_INVALID_DAY_OFF_SAVED);

                }

            }
            if (weeklyMinutes > 2400) {
                throw new InvalidShiftException(IShiftService.E_MORE_THAN_40H + " " + employeeLoginName);
            }
        }
    }

    private void validateUpdateShift(Map<? super Object, ?super Object> attrMap, Map<?, ?> keyMap) throws Exception {
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
        Integer mondayDuration = Integer.valueOf((int) Duration.between(mondayStartTime, mondayEndTime).toMinutes());

        LocalTime tuesdayStartTime = LocalTime.parse(tuesdayStart);
        LocalTime tuesdayEndTime = LocalTime.parse(tuesdayEnd);
        Integer tuesdayDuration = Integer.valueOf((int) Duration.between(tuesdayStartTime, tuesdayEndTime).toMinutes());

        LocalTime wednesdayStartTime = LocalTime.parse(wednesdayStart);
        LocalTime wednesdayEndTime = LocalTime.parse(wednesdayEnd);
        Integer wednesdayDuration = Integer.valueOf((int) Duration.between(wednesdayStartTime, wednesdayEndTime).toMinutes());

        LocalTime thursdayStartTime = LocalTime.parse(thursdayStart);
        LocalTime thursdayEndTime = LocalTime.parse(thursdayEnd);
        Integer thursdayDuration = Integer.valueOf((int) Duration.between(thursdayStartTime, thursdayEndTime).toMinutes());

        LocalTime fridayStartTime = LocalTime.parse(fridayStart);
        LocalTime fridayEndTime = LocalTime.parse(fridayEnd);
        Integer fridayDuration = Integer.valueOf((int) Duration.between(fridayStartTime, fridayEndTime).toMinutes());

        LocalTime saturdayStartTime = LocalTime.parse(SaturdayStart);
        LocalTime saturdayEndTime = LocalTime.parse(saturdayEnd);
        Integer saturdayDuration = Integer.valueOf((int) Duration.between(saturdayStartTime, saturdayEndTime).toMinutes());

        LocalTime sundayStartTime = LocalTime.parse(sundayStart);
        LocalTime sundayEndTime = LocalTime.parse(sundayEnd);
        Integer sundayDuration = Integer.valueOf((int) Duration.between(sundayStartTime, sundayEndTime).toMinutes());

        if (mondayEndTime.isBefore(mondayStartTime) || tuesdayEndTime.isBefore(tuesdayStartTime) || wednesdayEndTime.isBefore(wednesdayStartTime) ||
                thursdayEndTime.isBefore(thursdayStartTime) || fridayEndTime.isBefore(fridayStartTime) || saturdayEndTime.isBefore(saturdayStartTime) ||
                sundayEndTime.isBefore(sundayStartTime)) {
            throw new InvalidShiftException(END_BEFORE_START);
        }
        Map<? super Object, ? super Object> dayDurations = new HashMap<>();
        dayDurations.put(ShiftDAO.MON, mondayDuration);
        dayDurations.put(ShiftDAO.TUE, tuesdayDuration);
        dayDurations.put(ShiftDAO.WED, wednesdayDuration);
        dayDurations.put(ShiftDAO.THU, thursdayDuration);
        dayDurations.put(ShiftDAO.FRI, fridayDuration);
        dayDurations.put(ShiftDAO.SAT, saturdayDuration);
        dayDurations.put(ShiftDAO.SUN, sundayDuration);


        List<String> employeeLoginNames = (List) attrMap.get(ShiftDAO.LOGIN_NAMES);
        int weeklyMinutes = mondayDuration + tuesdayDuration + wednesdayDuration + thursdayDuration + fridayDuration + saturdayDuration + sundayDuration;
        if (employeeLoginNames == null) {
            employeeLoginNames = new ArrayList<>();
            List<String> queriedAtributeList = List.of(UserDAO.LOGIN_NAME,ShiftDAO.ROLE_ID);
            Map<? super Object, ? super Object> originalEmployeeLoginNames = new HashMap<>();
            EntityResult usersInOriginalShiftResult = new EntityResultMapImpl();
            usersInOriginalShiftResult = this.daoHelper.query(usersShiftsDAO, keyMap, queriedAtributeList, UsersShiftsDAO.Q_USERS_SHIFTS);
            originalEmployeeLoginNames.put(ShiftDAO.LOGIN_NAMES, (usersInOriginalShiftResult.get(UsersShiftsDAO.LOGIN_NAME)));

            List<String> loginNames = (List<String>) originalEmployeeLoginNames.get(ShiftDAO.LOGIN_NAMES);

            employeeLoginNames.addAll(loginNames);
            attrMap.put(ShiftDAO.LOGIN_NAMES,employeeLoginNames);
            attrMap.put(ShiftDAO.ROLE_ID,((List)usersInOriginalShiftResult.get(ShiftDAO.ROLE_ID)).get(0));
        }
        for (int i=0;i<employeeLoginNames.size();i++) {

            int employeeWeeklyMinutes = weeklyMinutes;

            List<String> queriedAtributeList = List.of(UsersDaysOffDAO.DAY);
            Map<String, String> employeeLoginNameFilter = new HashMap<>();
            employeeLoginNameFilter.put(UserDAO.LOGIN_NAME, employeeLoginNames.get(i));

            EntityResult daysOffPerEmployee = this.daoHelper.query(usersDaysOffDao, employeeLoginNameFilter, queriedAtributeList);

            List<String> daysOffListPerEmployee = (List<String>) daysOffPerEmployee.get(UsersDaysOffDAO.DAY);
            for (String day : daysOffListPerEmployee) {
                switch (day) {
                    case (ShiftDAO.MON):
                        employeeWeeklyMinutes = employeeWeeklyMinutes - mondayDuration;
                        break;
                    case (ShiftDAO.TUE):
                        employeeWeeklyMinutes = employeeWeeklyMinutes - tuesdayDuration;
                        break;
                    case (ShiftDAO.THU):
                        employeeWeeklyMinutes = employeeWeeklyMinutes - thursdayDuration;
                        break;
                    case (ShiftDAO.WED):
                        employeeWeeklyMinutes = employeeWeeklyMinutes - wednesdayDuration;
                        break;
                    case (ShiftDAO.FRI):
                        employeeWeeklyMinutes = employeeWeeklyMinutes - fridayDuration;
                        break;
                    case (ShiftDAO.SAT):
                        employeeWeeklyMinutes = employeeWeeklyMinutes - saturdayDuration;
                        break;
                    case (ShiftDAO.SUN):
                        employeeWeeklyMinutes = employeeWeeklyMinutes - sundayDuration;
                        break;
                    default:
                        throw new Exception(IShiftService.E_INVALID_DAY_OFF_SAVED);
                }
            }
            if (weeklyMinutes > 2400) {
                throw new InvalidShiftException(IShiftService.E_MORE_THAN_40H + " " + employeeLoginNames.get(i));
            }
        }
    }


    private void roleMatcher(Map<?, ?> attrMap) throws Exception {
        List<String> employeeLoginNames = (List) attrMap.get(ShiftDAO.LOGIN_NAMES);
        for (String employeeLoginName : employeeLoginNames) {
            if (!((userService.getUserRoles(employeeLoginName)).contains(attrMap.get(ShiftDAO.ROLE_NAME)))) {

                throw new Exception(IShiftService.E_EMPLOYEE_ROLE_MISMATCH + ", " + employeeLoginName + " does not match");
            }
        }
    }
    private void updateRoleMatcher(Map<?super Object,?super Object>attrMap) throws Exception {
        List<String> employeeLoginNames = (List) attrMap.get(ShiftDAO.LOGIN_NAMES);

        Map<?super Object,?super Object>keymapRoleId= new HashMap<>();
        keymapRoleId.put(ShiftDAO.ROLE_ID,attrMap.get(ShiftDAO.ROLE_ID));
        EntityResult employeeRolename = this.daoHelper.query(userRoleDAO,keymapRoleId, List.of(UserRoleDAO.NAME));
        attrMap.put(ShiftDAO.ROLENAME,(employeeRolename.getRecordValues(0)).get(ShiftDAO.ROLENAME));

        for (String employeeLoginName : employeeLoginNames) {
            if (!((userService.getUserRoles(employeeLoginName)).contains(attrMap.get(ShiftDAO.ROLENAME)))) {
                throw new Exception(IShiftService.E_EMPLOYEE_ROLE_MISMATCH + ", " + employeeLoginName + " does not match");
            }
        }
    }
}
