package com.ontimize.hr.model.core.service;

import com.ontimize.hr.api.core.service.IShiftService;
import com.ontimize.hr.api.core.service.exception.EmployeeNotFoundException;
import com.ontimize.hr.api.core.service.exception.InvalidShiftException;
import com.ontimize.hr.model.core.dao.*;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.common.dto.EntityResultMapImpl;
import com.ontimize.jee.common.security.PermissionsProviderSecured;
import com.ontimize.jee.server.dao.DefaultOntimizeDaoHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

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
    private UsersRolesDAO usersRolesDAO;
    @Autowired
    private UsersDaysOffDAO usersDaysOffDao;
    @Autowired
    private UsersShiftsDAO usersShiftsDAO;
    @Autowired
    private UserDAO userDAO;
    @Autowired
    private EmployeeService employeeService;
    private static final String WORK_DAY_START = "start";
    private static final String WORK_DAY_END = "end";

    @SuppressWarnings("unchecked")
    @Secured({PermissionsProviderSecured.SECURED})
    @Override
    public EntityResult shiftQuery(Map<? super Object, ? super Object> filter, List<? super Object> attrList) {
        Map<? super Object, ? super Object> employeeFilter = (Map<? super Object, ? super Object>) filter.remove(EMPLOYEE_FILTER);

        EntityResult result;

        if (filter.get(ShiftDAO.ID) != null) {
            try {
                result = daoHelper.query(shiftDAO, filter, attrList, ShiftDAO.Q_SHIFT_WITH_ROLE);
                if (result.isEmpty()) {
                    throw new InvalidShiftException(E_NOT_FOUND);
                }

                if (employeeFilter != null) {
                    List<String> employeeAttrList = (List<String>) employeeFilter.remove(EMPLOYEE_COLUMNS);
                    employeeFilter.put(UserDAO.SHIFT_ID, filter.get(ShiftDAO.ID));

                    EntityResult employeeResult = userService.userQuery(employeeFilter, employeeAttrList);

                    List<Object> employeeList = new ArrayList<>();

                    for (int i = 0; i < employeeResult.calculateRecordNumber(); i++) {
                        employeeList.add(employeeResult.getRecordValues(i));
                    }

                    result.put(SHIFT_EMPLOYEES, List.of(employeeList));
                }
            } catch (Exception e) {
                result = new EntityResultMapImpl();
                result.setMessage(e.getMessage());
                result.setCode(EntityResult.OPERATION_WRONG);
                e.printStackTrace();
            }
        } else {
            attrList.add(ShiftDAO.ID);
            result = daoHelper.query(shiftDAO, new HashMap<>(), attrList, ShiftDAO.Q_SHIFT_WITH_ROLE);

            if (employeeFilter != null) {
                List<List<Object>> employeesByShift = new ArrayList<>();
                List<String> employeeAttrList = (List<String>) employeeFilter.remove(EMPLOYEE_COLUMNS);
                for (int i = 0; i < result.calculateRecordNumber(); i++) {
                    Map<Object, Object> currentShift = result.getRecordValues(i);
                    employeeFilter.put(UserDAO.SHIFT_ID, currentShift.get(ShiftDAO.ID));
                    EntityResult employeeResult = userService.userQuery(employeeFilter, employeeAttrList);

                    List<Object> employeeList = new ArrayList<>();

                    for (int j = 0; j < employeeResult.calculateRecordNumber(); j++) {
                        employeeList.add(employeeResult.getRecordValues(j));
                    }

                    employeesByShift.add(employeeList);
                }

                result.put(SHIFT_EMPLOYEES, employeesByShift);
            }
        }

        return result;
    }

    @SuppressWarnings("unchecked")
    @Secured({PermissionsProviderSecured.SECURED})
    @Override
    public EntityResult shiftInsert(Map<? super Object, ? super Object> attrMap) {
        EntityResult result;
        try {
            validateWeeklyHours(attrMap);
            Map<?, ?> monday = (Map<?, ?>) attrMap.get(ShiftDAO.MON);
            String mondayStart = (String) monday.get(WORK_DAY_START);
            String mondayEnd = (String) monday.get(WORK_DAY_END);
            Map<?, ?> tuesday = (Map<?, ?>) attrMap.get(ShiftDAO.TUE);
            String tuesdayStart = (String) tuesday.get(WORK_DAY_START);
            String tuesdayEnd = (String) tuesday.get(WORK_DAY_END);
            Map<?, ?> wednesday = (Map<?, ?>) attrMap.get(ShiftDAO.WED);
            String wednesdayStart = (String) wednesday.get(WORK_DAY_START);
            String wednesdayEnd = (String) wednesday.get(WORK_DAY_END);
            Map<?, ?> thursday = (Map<?, ?>) attrMap.get(ShiftDAO.THU);
            String thursdayStart = (String) thursday.get(WORK_DAY_START);
            String thursdayEnd = (String) thursday.get(WORK_DAY_END);
            Map<?, ?> friday = (Map<?, ?>) attrMap.get(ShiftDAO.FRI);
            String fridayStart = (String) friday.get(WORK_DAY_START);
            String fridayEnd = (String) friday.get(WORK_DAY_END);
            Map<?, ?> saturday = (Map<?, ?>) attrMap.get(ShiftDAO.SAT);
            String saturdayStart = (String) saturday.get(WORK_DAY_START);
            String saturdayEnd = (String) saturday.get(WORK_DAY_END);
            Map<?, ?> sunday = (Map<?, ?>) attrMap.get(ShiftDAO.SUN);
            String sundayStart = (String) sunday.get(WORK_DAY_START);
            String sundayEnd = (String) sunday.get(WORK_DAY_END);

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

                List<String> roleEmployees = (List<String>) daoHelper
                        .query(
                                usersRolesDAO,
                                roleEmployeesFilter,
                                List.of(UserDAO.LOGIN_NAME)
                        ).get(UserDAO.LOGIN_NAME);

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

            if (shiftEmployees != null) {
                insertShiftEmployees(result, shiftEmployees);
            }

            result.setCode(EntityResult.OPERATION_SUCCESSFUL_SHOW_MESSAGE);
            result.setMessage(IShiftService.INSERTION_SUCCESS);
        } catch (DuplicateKeyException e) {
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

    private void insertShiftEmployees(EntityResult result, List<String> shiftEmployees) {
        Map<String, Object> employeesShiftsRelationship = new HashMap<>();
        employeesShiftsRelationship.put(UserDAO.SHIFT_ID, result.get(ShiftDAO.ID));

        for (String loginName : shiftEmployees) {
            Map<String, String> filter = new HashMap<>();
            filter.put(UsersShiftsDAO.LOGIN_NAME, loginName);
            daoHelper.update(userDAO, employeesShiftsRelationship, filter);
        }
    }

    @SuppressWarnings("unchecked")
    @Secured({PermissionsProviderSecured.SECURED})
    @Override
    public EntityResult shiftUpdate(Map<? super Object, ? super Object> attrMap, Map<?, ?> keyMap) {
        EntityResult result;
        try {

            List<String> attrbuteQueriedList = List.of(ShiftDAO.MON, ShiftDAO.SUN, ShiftDAO.SAT, ShiftDAO.FRI, ShiftDAO.THU, ShiftDAO.WED, ShiftDAO.TUE, ShiftDAO.LOGIN_NAMES, ShiftDAO.ROLE_ID);

            Map<?, ?> originalShiftDays;
            originalShiftDays = daoHelper.query(shiftDAO, keyMap, attrbuteQueriedList).getRecordValues(0);

            Map<String, String> monday = (Map<String, String>) attrMap.get(ShiftDAO.MON);
            if (monday == null) {
                monday = new HashMap<>();
                String[] arrayHours = ((String) (originalShiftDays.get(ShiftDAO.MON))).split("-");
                monday.put(WORK_DAY_START, arrayHours[0]);
                monday.put(WORK_DAY_END, arrayHours[1]);
                attrMap.put(ShiftDAO.MON, monday);

            }
            String mondayStart = monday.get(WORK_DAY_START);
            String mondayEnd = monday.get(WORK_DAY_END);

            Map<String, String> tuesday = (Map<String, String>) attrMap.get(ShiftDAO.TUE);
            if (tuesday == null) {
                tuesday = new HashMap<>();
                String[] arrayHours = ((String) (originalShiftDays.get(ShiftDAO.TUE))).split("-");
                tuesday.put(WORK_DAY_START, arrayHours[0]);
                tuesday.put(WORK_DAY_END, arrayHours[1]);
                attrMap.put(ShiftDAO.TUE, tuesday);
            }
            String tuesdayStart = tuesday.get(WORK_DAY_START);
            String tuesdayEnd = tuesday.get(WORK_DAY_END);

            Map<String, String> wednesday = (Map<String, String>) attrMap.get(ShiftDAO.WED);
            if (wednesday == null) {
                wednesday = new HashMap<>();
                String[] arrayHours = ((String) (originalShiftDays.get(ShiftDAO.WED))).split("-");
                wednesday.put(WORK_DAY_START, arrayHours[0]);
                wednesday.put(WORK_DAY_END, arrayHours[1]);
                attrMap.put(ShiftDAO.WED, wednesday);
            }
            String wednesdayStart = wednesday.get(WORK_DAY_START);
            String wednesdayEnd = wednesday.get(WORK_DAY_END);

            Map<String, String> thursday = (Map<String, String>) attrMap.get(ShiftDAO.THU);
            if (thursday == null) {
                thursday = new HashMap<>();
                String[] arrayHours = ((String) (originalShiftDays.get(ShiftDAO.THU))).split("-");
                thursday.put(WORK_DAY_START, arrayHours[0]);
                thursday.put(WORK_DAY_END, arrayHours[1]);
                attrMap.put(ShiftDAO.THU, thursday);
            }
            String thursdayStart = thursday.get(WORK_DAY_START);
            String thursdayEnd = thursday.get(WORK_DAY_END);

            Map<String, String> friday = (Map<String, String>) attrMap.get(ShiftDAO.FRI);
            if (friday == null) {
                friday = new HashMap<>();
                String[] arrayHours = ((String) (originalShiftDays.get(ShiftDAO.FRI))).split("-");
                friday.put(WORK_DAY_START, arrayHours[0]);
                friday.put(WORK_DAY_END, arrayHours[1]);
                attrMap.put(ShiftDAO.FRI, friday);
            }
            String fridayStart = friday.get(WORK_DAY_START);
            String fridayEnd = friday.get(WORK_DAY_END);

            Map<String, String> saturday = (Map<String, String>) attrMap.get(ShiftDAO.SAT);
            if (saturday == null) {
                saturday = new HashMap<>();
                String[] arrayHours = ((String) (originalShiftDays.get(ShiftDAO.SAT))).split("-");
                saturday.put(WORK_DAY_START, arrayHours[0]);
                saturday.put(WORK_DAY_END, arrayHours[1]);
                attrMap.put(ShiftDAO.SAT, saturday);
            }
            String saturdayStart = saturday.get(WORK_DAY_START);
            String saturdayEnd = saturday.get(WORK_DAY_END);

            Map<String, String> sunday = (Map<String, String>) attrMap.get(ShiftDAO.SUN);
            if (sunday == null) {
                sunday = new HashMap<>();
                String[] arrayHours = ((String) (originalShiftDays.get(ShiftDAO.SUN))).split("-");
                sunday.put(WORK_DAY_START, arrayHours[0]);
                sunday.put(WORK_DAY_END, arrayHours[1]);
                attrMap.put(ShiftDAO.SUN, sunday);
            }
            String sundayStart = sunday.get(WORK_DAY_START);
            String sundayEnd = sunday.get(WORK_DAY_END);

            if (attrMap.get(ShiftDAO.ROLE_ID) == null) {
                attrMap.put(ShiftDAO.ROLE_ID, originalShiftDays.get(ShiftDAO.ROLE_ID));
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
                roleEmployeesFilter.put(UsersRolesDAO.ROLE_NAME, (String) attrMap.get(ShiftDAO.ROLENAME));

                List<String> roleEmployees = (List<String>) daoHelper
                        .query(
                                usersRolesDAO,
                                roleEmployeesFilter,
                                List.of(UsersRolesDAO.LOGIN_NAME)
                        ).get(UsersRolesDAO.LOGIN_NAME);

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
            result.put(ShiftDAO.ID, keyMap.get(ShiftDAO.ID));

            List<String> deleteShiftEmployees = (List<String>) attrMap.get(ShiftDAO.DELETE_SHIFT_EMPLOYEES);
            boolean shiftEmployeesIsNull = shiftEmployees == null;
            boolean deleteShiftEmployeesIsNull = deleteShiftEmployees == null;

            if (!shiftEmployeesIsNull || !deleteShiftEmployeesIsNull) {
                shiftEmployees = shiftEmployeesIsNull ? new ArrayList<>() : shiftEmployees;
                deleteShiftEmployees = deleteShiftEmployeesIsNull ? new ArrayList<>() : deleteShiftEmployees;
                updateShiftEmployees(result, shiftEmployees, deleteShiftEmployees);
            }

            result.setCode(EntityResult.OPERATION_SUCCESSFUL_SHOW_MESSAGE);
            result.setMessage(IShiftService.UPDATE_SUCCESS);
        } catch (DuplicateKeyException e) {
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

    private void updateShiftEmployees(EntityResult result, List<String> shiftEmployees, List<String> shiftDeleteEmployees) {
        Map<String, Integer> employeesShiftsRelationship = new HashMap<>();
        employeesShiftsRelationship.put(UserDAO.SHIFT_ID, (Integer) result.get(ShiftDAO.ID));
        Map<String, String> employeesShiftsFilter = new HashMap<>();

        for (String loginName : shiftEmployees) {
            employeesShiftsFilter.put(UserDAO.LOGIN_NAME, loginName);
            daoHelper.update(userDAO, employeesShiftsRelationship, employeesShiftsFilter);
        }

        for (String loginName : shiftDeleteEmployees) {
            employeesShiftsFilter.put(UsersShiftsDAO.LOGIN_NAME, loginName);
            Map<String, ?> attrsValuesToUpdate = new HashMap<>();
            attrsValuesToUpdate.put(UserDAO.SHIFT_ID, null);
            daoHelper.update(usersShiftsDAO, attrsValuesToUpdate, employeesShiftsFilter);
        }
    }

    @Override
    public EntityResult shiftDelete(Map<?, ?> keyMap) {
        return null;
    }

    @SuppressWarnings("unchecked")
    private void validateWeeklyHours(Map<?, ?> attrMap) throws Exception {
        //TODO: separar resta de días libres
        Map<?, ?> monday = (Map<?, ?>) attrMap.get(ShiftDAO.MON);
        String mondayStart = (String) monday.get(WORK_DAY_START);
        String mondayEnd = (String) monday.get(WORK_DAY_END);
        Map<?, ?> tuesday = (Map<?, ?>) attrMap.get(ShiftDAO.TUE);
        String tuesdayStart = (String) tuesday.get(WORK_DAY_START);
        String tuesdayEnd = (String) tuesday.get(WORK_DAY_END);
        Map<?, ?> wednesday = (Map<?, ?>) attrMap.get(ShiftDAO.WED);
        String wednesdayStart = (String) wednesday.get(WORK_DAY_START);
        String wednesdayEnd = (String) wednesday.get(WORK_DAY_END);
        Map<?, ?> thursday = (Map<?, ?>) attrMap.get(ShiftDAO.THU);
        String thursdayStart = (String) thursday.get(WORK_DAY_START);
        String thursdayEnd = (String) thursday.get(WORK_DAY_END);
        Map<?, ?> friday = (Map<?, ?>) attrMap.get(ShiftDAO.FRI);
        String fridayStart = (String) friday.get(WORK_DAY_START);
        String fridayEnd = (String) friday.get(WORK_DAY_END);
        Map<?, ?> saturday = (Map<?, ?>) attrMap.get(ShiftDAO.SAT);
        String saturdayStart = (String) saturday.get(WORK_DAY_START);
        String saturdayEnd = (String) saturday.get(WORK_DAY_END);
        Map<?, ?> sunday = (Map<?, ?>) attrMap.get(ShiftDAO.SUN);
        String sundayStart = (String) sunday.get(WORK_DAY_START);
        String sundayEnd = (String) sunday.get(WORK_DAY_END);

        LocalTime mondayStartTime = LocalTime.parse(mondayStart);
        LocalTime mondayEndTime = LocalTime.parse(mondayEnd);
        int mondayDuration = (int) Duration.between(mondayStartTime, mondayEndTime).toMinutes();

        LocalTime tuesdayStartTime = LocalTime.parse(tuesdayStart);
        LocalTime tuesdayEndTime = LocalTime.parse(tuesdayEnd);
        int tuesdayDuration = (int) Duration.between(tuesdayStartTime, tuesdayEndTime).toMinutes();

        LocalTime wednesdayStartTime = LocalTime.parse(wednesdayStart);
        LocalTime wednesdayEndTime = LocalTime.parse(wednesdayEnd);
        int wednesdayDuration = (int) Duration.between(wednesdayStartTime, wednesdayEndTime).toMinutes();

        LocalTime thursdayStartTime = LocalTime.parse(thursdayStart);
        LocalTime thursdayEndTime = LocalTime.parse(thursdayEnd);
        int thursdayDuration = (int) Duration.between(thursdayStartTime, thursdayEndTime).toMinutes();

        LocalTime fridayStartTime = LocalTime.parse(fridayStart);
        LocalTime fridayEndTime = LocalTime.parse(fridayEnd);
        int fridayDuration = (int) Duration.between(fridayStartTime, fridayEndTime).toMinutes();

        LocalTime saturdayStartTime = LocalTime.parse(saturdayStart);
        LocalTime saturdayEndTime = LocalTime.parse(saturdayEnd);
        int saturdayDuration = (int) Duration.between(saturdayStartTime, saturdayEndTime).toMinutes();

        LocalTime sundayStartTime = LocalTime.parse(sundayStart);
        LocalTime sundayEndTime = LocalTime.parse(sundayEnd);
        int sundayDuration = (int) Duration.between(sundayStartTime, sundayEndTime).toMinutes();

        if (mondayEndTime.isBefore(mondayStartTime) || tuesdayEndTime.isBefore(tuesdayStartTime) || wednesdayEndTime.isBefore(wednesdayStartTime) ||
                thursdayEndTime.isBefore(thursdayStartTime) || fridayEndTime.isBefore(fridayStartTime) || saturdayEndTime.isBefore(saturdayStartTime) ||
                sundayEndTime.isBefore(sundayStartTime)) {
            throw new InvalidShiftException(END_BEFORE_START);
        }

        List<String> employeeLoginNames = (List<String>) attrMap.get(ShiftDAO.LOGIN_NAMES);
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

    @SuppressWarnings("unchecked")
    private void validateUpdateShift(Map<? super Object, ? super Object> attrMap, Map<?, ?> keyMap) throws Exception {
        Map<?, ?> monday = (Map<?, ?>) attrMap.get(ShiftDAO.MON);
        String mondayStart = (String) monday.get(WORK_DAY_START);
        String mondayEnd = (String) monday.get(WORK_DAY_END);
        Map<?, ?> tuesday = (Map<?, ?>) attrMap.get(ShiftDAO.TUE);
        String tuesdayStart = (String) tuesday.get(WORK_DAY_START);
        String tuesdayEnd = (String) tuesday.get(WORK_DAY_END);
        Map<?, ?> wednesday = (Map<?, ?>) attrMap.get(ShiftDAO.WED);
        String wednesdayStart = (String) wednesday.get(WORK_DAY_START);
        String wednesdayEnd = (String) wednesday.get(WORK_DAY_END);
        Map<?, ?> thursday = (Map<?, ?>) attrMap.get(ShiftDAO.THU);
        String thursdayStart = (String) thursday.get(WORK_DAY_START);
        String thursdayEnd = (String) thursday.get(WORK_DAY_END);
        Map<?, ?> friday = (Map<?, ?>) attrMap.get(ShiftDAO.FRI);
        String fridayStart = (String) friday.get(WORK_DAY_START);
        String fridayEnd = (String) friday.get(WORK_DAY_END);
        Map<?, ?> saturday = (Map<?, ?>) attrMap.get(ShiftDAO.SAT);
        String saturdayStart = (String) saturday.get(WORK_DAY_START);
        String saturdayEnd = (String) saturday.get(WORK_DAY_END);
        Map<?, ?> sunday = (Map<?, ?>) attrMap.get(ShiftDAO.SUN);
        String sundayStart = (String) sunday.get(WORK_DAY_START);
        String sundayEnd = (String) sunday.get(WORK_DAY_END);

        LocalTime mondayStartTime = LocalTime.parse(mondayStart);
        LocalTime mondayEndTime = LocalTime.parse(mondayEnd);
        int mondayDuration = (int) Duration.between(mondayStartTime, mondayEndTime).toMinutes();

        LocalTime tuesdayStartTime = LocalTime.parse(tuesdayStart);
        LocalTime tuesdayEndTime = LocalTime.parse(tuesdayEnd);
        int tuesdayDuration = (int) Duration.between(tuesdayStartTime, tuesdayEndTime).toMinutes();

        LocalTime wednesdayStartTime = LocalTime.parse(wednesdayStart);
        LocalTime wednesdayEndTime = LocalTime.parse(wednesdayEnd);
        int wednesdayDuration = (int) Duration.between(wednesdayStartTime, wednesdayEndTime).toMinutes();

        LocalTime thursdayStartTime = LocalTime.parse(thursdayStart);
        LocalTime thursdayEndTime = LocalTime.parse(thursdayEnd);
        int thursdayDuration = (int) Duration.between(thursdayStartTime, thursdayEndTime).toMinutes();

        LocalTime fridayStartTime = LocalTime.parse(fridayStart);
        LocalTime fridayEndTime = LocalTime.parse(fridayEnd);
        int fridayDuration = (int) Duration.between(fridayStartTime, fridayEndTime).toMinutes();

        LocalTime saturdayStartTime = LocalTime.parse(saturdayStart);
        LocalTime saturdayEndTime = LocalTime.parse(saturdayEnd);
        int saturdayDuration = (int) Duration.between(saturdayStartTime, saturdayEndTime).toMinutes();

        LocalTime sundayStartTime = LocalTime.parse(sundayStart);
        LocalTime sundayEndTime = LocalTime.parse(sundayEnd);
        int sundayDuration = (int) Duration.between(sundayStartTime, sundayEndTime).toMinutes();

        if (mondayEndTime.isBefore(mondayStartTime) || tuesdayEndTime.isBefore(tuesdayStartTime) || wednesdayEndTime.isBefore(wednesdayStartTime) ||
                thursdayEndTime.isBefore(thursdayStartTime) || fridayEndTime.isBefore(fridayStartTime) || saturdayEndTime.isBefore(saturdayStartTime) ||
                sundayEndTime.isBefore(sundayStartTime)) {
            throw new InvalidShiftException(END_BEFORE_START);
        }

        List<String> employeeLoginNames = (List<String>) attrMap.get(ShiftDAO.LOGIN_NAMES);
        int weeklyMinutes = mondayDuration + tuesdayDuration + wednesdayDuration + thursdayDuration + fridayDuration + saturdayDuration + sundayDuration;
        if (employeeLoginNames == null) {
            List<String> queriedAtributeList = List.of(UserDAO.LOGIN_NAME, ShiftDAO.ROLE_ID);
            Map<? super Object, ? super Object> originalEmployeeLoginNames = new HashMap<>();
            EntityResult usersInOriginalShiftResult;
            usersInOriginalShiftResult = this.daoHelper.query(usersShiftsDAO, keyMap, queriedAtributeList, UsersShiftsDAO.Q_USERS_SHIFTS);
            originalEmployeeLoginNames.put(ShiftDAO.LOGIN_NAMES, (usersInOriginalShiftResult.get(UsersShiftsDAO.LOGIN_NAME)));

            List<String> loginNames = (List<String>) originalEmployeeLoginNames.get(ShiftDAO.LOGIN_NAMES);

            employeeLoginNames = new ArrayList<>(loginNames);
            attrMap.put(ShiftDAO.LOGIN_NAMES, employeeLoginNames);
            attrMap.put(ShiftDAO.ROLE_ID, ((List<String>) usersInOriginalShiftResult.get(ShiftDAO.ROLE_ID)).get(0));
        }

        for (String employeeLoginName : employeeLoginNames) {
            int employeeWeeklyMinutes = weeklyMinutes;

            List<String> queriedAtributeList = List.of(UsersDaysOffDAO.DAY);
            Map<String, String> employeeLoginNameFilter = new HashMap<>();
            employeeLoginNameFilter.put(UserDAO.LOGIN_NAME, employeeLoginName);

            EntityResult daysOffPerEmployee = this.daoHelper.query(usersDaysOffDao, employeeLoginNameFilter, queriedAtributeList);

            if (daysOffPerEmployee.isEmpty()) {
                throw new EmployeeNotFoundException(ShiftDAO.E_EMPLOYEES_NOT_FOUND + employeeLoginNameFilter.get(UserDAO.LOGIN_NAME));
            }

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


    @SuppressWarnings("unchecked")
    private void roleMatcher(Map<?, ?> attrMap) throws Exception {
        List<String> employeeLoginNames = (List<String>) attrMap.get(ShiftDAO.LOGIN_NAMES);
        for (String employeeLoginName : employeeLoginNames) {
            if (!((userService.getUserRoles(employeeLoginName)).contains(attrMap.get(ShiftDAO.ROLE_NAME)))) {

                throw new Exception(IShiftService.E_EMPLOYEE_ROLE_MISMATCH + ", " + employeeLoginName + " does not match");
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void updateRoleMatcher(Map<? super Object, ? super Object> attrMap) throws Exception {
        List<String> employeeLoginNames = (List<String>) attrMap.get(ShiftDAO.LOGIN_NAMES);

        Map<? super Object, ? super Object> keymapRoleId = new HashMap<>();
        keymapRoleId.put(ShiftDAO.ROLE_ID, attrMap.get(ShiftDAO.ROLE_ID));
        EntityResult employeeRolename = this.daoHelper.query(userRoleDAO, keymapRoleId, List.of(UserRoleDAO.NAME));
        attrMap.put(ShiftDAO.ROLENAME, (employeeRolename.getRecordValues(0)).get(ShiftDAO.ROLENAME));

        for (String employeeLoginName : employeeLoginNames) {
            if (!(userService.getUserRoles(employeeLoginName)).contains(attrMap.get(ShiftDAO.ROLENAME))) {
                throw new Exception(IShiftService.E_EMPLOYEE_ROLE_MISMATCH + ", " + employeeLoginName + " does not match");
            }
        }
    }
}
