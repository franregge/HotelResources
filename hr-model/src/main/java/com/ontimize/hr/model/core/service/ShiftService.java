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
import org.springframework.jdbc.SQLWarningException;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalTime;
import java.util.*;
import java.util.function.BiPredicate;
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
    private static final int NO_SHIFT_ID_YET = -1;

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
            if (monday != null) {
                String mondayStart = (String) monday.get(WORK_DAY_START);
                String mondayEnd = (String) monday.get(WORK_DAY_END);
                attrMap.put(ShiftDAO.MON, mondayStart + "-" + mondayEnd);
            }

            Map<?, ?> tuesday = (Map<?, ?>) attrMap.get(ShiftDAO.TUE);
            if (tuesday != null) {
                String tuesdayStart = (String) tuesday.get(WORK_DAY_START);
                String tuesdayEnd = (String) tuesday.get(WORK_DAY_END);
                attrMap.put(ShiftDAO.TUE, tuesdayStart + "-" + tuesdayEnd);
            }

            Map<?, ?> wednesday = (Map<?, ?>) attrMap.get(ShiftDAO.WED);
            if (tuesday != null) {
                String wednesdayStart = (String) wednesday.get(WORK_DAY_START);
                String wednesdayEnd = (String) wednesday.get(WORK_DAY_END);
                attrMap.put(ShiftDAO.WED, wednesdayStart + "-" + wednesdayEnd);
            }

            Map<?, ?> thursday = (Map<?, ?>) attrMap.get(ShiftDAO.THU);
            if (thursday != null) {
                String thursdayStart = (String) thursday.get(WORK_DAY_START);
                String thursdayEnd = (String) thursday.get(WORK_DAY_END);
                attrMap.put(ShiftDAO.THU, thursdayStart + "-" + thursdayEnd);
            }

            Map<?, ?> friday = (Map<?, ?>) attrMap.get(ShiftDAO.FRI);
            if (friday != null) {
                String fridayStart = (String) friday.get(WORK_DAY_START);
                String fridayEnd = (String) friday.get(WORK_DAY_END);
                attrMap.put(ShiftDAO.FRI, fridayStart + "-" + fridayEnd);
            }

            Map<?, ?> saturday = (Map<?, ?>) attrMap.get(ShiftDAO.SAT);
            if (saturday != null) {
                String saturdayStart = (String) saturday.get(WORK_DAY_START);
                String saturdayEnd = (String) saturday.get(WORK_DAY_END);
                attrMap.put(ShiftDAO.SAT, saturdayStart + "-" + saturdayEnd);
            }

            Map<?, ?> sunday = (Map<?, ?>) attrMap.get(ShiftDAO.SUN);
            if (sunday != null) {
                String sundayStart = (String) sunday.get(WORK_DAY_START);
                String sundayEnd = (String) sunday.get(WORK_DAY_END);
                attrMap.put(ShiftDAO.SUN, sundayStart + "-" + sundayEnd);
            }

            roleMatcher(attrMap);

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
        Map<? super Object, ? super Object> originalAttrMap = new HashMap<>(attrMap);
        EntityResult result;
        try {

            List<String> attrbuteQueriedList = List.of(ShiftDAO.MON, ShiftDAO.SUN, ShiftDAO.SAT, ShiftDAO.FRI, ShiftDAO.THU, ShiftDAO.WED, ShiftDAO.TUE, ShiftDAO.LOGIN_NAMES, ShiftDAO.ROLE_ID);

            Map<?, ?> originalShiftDays;
            originalShiftDays = daoHelper.query(shiftDAO, keyMap, attrbuteQueriedList, ShiftDAO.Q_SHIFT_WITH_ROLE).getRecordValues(0);
            attrMap.put(ShiftDAO.ROLE_ID, originalShiftDays.get(ShiftDAO.ROLE_ID));

            Map<String, String> monday = (Map<String, String>) attrMap.get(ShiftDAO.MON);
            if (monday == null && originalShiftDays.get(ShiftDAO.MON) != null) {
                monday = new HashMap<>();
                String[] arrayHours = ((String) (originalShiftDays.get(ShiftDAO.MON))).split("-");
                monday.put(WORK_DAY_START, arrayHours[0]);
                monday.put(WORK_DAY_END, arrayHours[1]);
                attrMap.put(ShiftDAO.MON, monday);
            }

            Map<String, String> tuesday = (Map<String, String>) attrMap.get(ShiftDAO.TUE);
            if (tuesday == null && originalShiftDays.get(ShiftDAO.TUE) != null) {
                tuesday = new HashMap<>();
                String[] arrayHours = ((String) (originalShiftDays.get(ShiftDAO.TUE))).split("-");
                tuesday.put(WORK_DAY_START, arrayHours[0]);
                tuesday.put(WORK_DAY_END, arrayHours[1]);
                attrMap.put(ShiftDAO.TUE, tuesday);
            }

            Map<String, String> wednesday = (Map<String, String>) attrMap.get(ShiftDAO.WED);
            if (wednesday == null && originalShiftDays.get(ShiftDAO.WED) != null) {
                wednesday = new HashMap<>();
                String[] arrayHours = ((String) (originalShiftDays.get(ShiftDAO.WED))).split("-");
                wednesday.put(WORK_DAY_START, arrayHours[0]);
                wednesday.put(WORK_DAY_END, arrayHours[1]);
                attrMap.put(ShiftDAO.WED, wednesday);
            }

            Map<String, String> thursday = (Map<String, String>) attrMap.get(ShiftDAO.THU);
            if (thursday == null && originalShiftDays.get(ShiftDAO.THU) != null) {
                thursday = new HashMap<>();
                String[] arrayHours = ((String) (originalShiftDays.get(ShiftDAO.THU))).split("-");
                thursday.put(WORK_DAY_START, arrayHours[0]);
                thursday.put(WORK_DAY_END, arrayHours[1]);
                attrMap.put(ShiftDAO.THU, thursday);
            }

            Map<String, String> friday = (Map<String, String>) attrMap.get(ShiftDAO.FRI);
            if (friday == null && originalShiftDays.get(ShiftDAO.FRI) != null) {
                friday = new HashMap<>();
                String[] arrayHours = ((String) (originalShiftDays.get(ShiftDAO.FRI))).split("-");
                friday.put(WORK_DAY_START, arrayHours[0]);
                friday.put(WORK_DAY_END, arrayHours[1]);
                attrMap.put(ShiftDAO.FRI, friday);
            }

            Map<String, String> saturday = (Map<String, String>) attrMap.get(ShiftDAO.SAT);
            if (saturday == null && originalShiftDays.get(ShiftDAO.SAT) != null) {
                saturday = new HashMap<>();
                String[] arrayHours = ((String) (originalShiftDays.get(ShiftDAO.SAT))).split("-");
                saturday.put(WORK_DAY_START, arrayHours[0]);
                saturday.put(WORK_DAY_END, arrayHours[1]);
                attrMap.put(ShiftDAO.SAT, saturday);
            }

            Map<String, String> sunday = (Map<String, String>) attrMap.get(ShiftDAO.SUN);
            if (sunday == null && originalShiftDays.get(ShiftDAO.SUN) != null) {
                sunday = new HashMap<>();
                String[] arrayHours = ((String) (originalShiftDays.get(ShiftDAO.SUN))).split("-");
                sunday.put(WORK_DAY_START, arrayHours[0]);
                sunday.put(WORK_DAY_END, arrayHours[1]);
                attrMap.put(ShiftDAO.SUN, sunday);
            }

            attrMap.put(ShiftDAO.ROLE_ID, originalShiftDays.get(ShiftDAO.ROLE_ID));

            validateUpdateShift(attrMap, keyMap);
            updateRoleMatcher(attrMap);

            for (Map.Entry<? super Object, ? super Object> entry : attrMap.entrySet()) {
                if (originalAttrMap.containsKey(entry.getKey())) {
                    switch ((String) entry.getKey()) {
                        case ShiftDAO.MON:
                        case ShiftDAO.TUE:
                        case ShiftDAO.WED:
                        case ShiftDAO.THU:
                        case ShiftDAO.FRI:
                        case ShiftDAO.SAT:
                        case ShiftDAO.SUN:
                            Map<String, String> hours = (Map<String, String>) entry.getValue();
                            originalAttrMap.put(entry.getKey(), hours.get(WORK_DAY_START) + "-" + hours.get(WORK_DAY_END));
                            break;
                        default:
                            break;
                    }
                }
            }

            Set<String> shiftEmployees = originalAttrMap.get(ShiftDAO.LOGIN_NAMES) == null
                    ? null
                    : new HashSet<>((List<String>) originalAttrMap.get(ShiftDAO.LOGIN_NAMES));

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

            try {
                result = this.daoHelper.update(this.shiftDAO, originalAttrMap, keyMap);
            } catch (SQLWarningException ignored) {
                result = new EntityResultMapImpl();
            }
            result.put(ShiftDAO.ID, keyMap.get(ShiftDAO.ID));
            List<String> deleteShiftEmployees = (List<String>) originalAttrMap.get(ShiftDAO.DELETE_SHIFT_EMPLOYEES);
            boolean shiftEmployeesIsNull = shiftEmployees == null;
            boolean deleteShiftEmployeesIsNull = deleteShiftEmployees == null;

            if (!shiftEmployeesIsNull || !deleteShiftEmployeesIsNull) {
                shiftEmployees = shiftEmployeesIsNull ? new HashSet<>() : shiftEmployees;
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

    private void updateShiftEmployees(EntityResult result, Set<String> shiftEmployees, List<String> shiftDeleteEmployees) {
        Map<String, Integer> employeesShiftsRelationship = new HashMap<>();
        employeesShiftsRelationship.put(UserDAO.SHIFT_ID, (Integer) result.get(ShiftDAO.ID));
        Map<String, String> employeesShiftsFilter = new HashMap<>();

        for (String loginName : shiftEmployees) {
            employeesShiftsFilter.put(UserDAO.LOGIN_NAME, loginName);
            daoHelper.update(userDAO, employeesShiftsRelationship, employeesShiftsFilter);
        }

        for (String loginName : shiftDeleteEmployees) {
            employeesShiftsFilter.put(UserDAO.LOGIN_NAME, loginName);
            Map<String, ?> attrsValuesToUpdate = new HashMap<>();
            attrsValuesToUpdate.put(UserDAO.SHIFT_ID, null);
            daoHelper.update(userDAO, attrsValuesToUpdate, employeesShiftsFilter);
        }
    }

    @Override
    public EntityResult shiftDelete(Map<?, ?> keyMap) {
        return null;
    }

    @SuppressWarnings("unchecked")
    private void validateWeeklyHours(Map<?, ?> attrMap) throws Exception {
        int mondayDuration;
        int tuesdayDuration;
        int wednesdayDuration;
        int thursdayDuration;
        int fridayDuration;
        int saturdayDuration;
        int sundayDuration;
        BiPredicate<LocalTime, LocalTime> endBeforeStart = (start, end) -> end.isBefore(start);

        Map<?, ?> monday = (Map<?, ?>) attrMap.get(ShiftDAO.MON);
        if (monday != null) {
            LocalTime mondayStartTime = LocalTime.parse((String) monday.get(WORK_DAY_START));
            LocalTime mondayEndTime = LocalTime.parse((String) monday.get(WORK_DAY_END));

            if (endBeforeStart.test(mondayStartTime, mondayEndTime)) {
                throw new InvalidShiftException(END_BEFORE_START);
            }

            mondayDuration = (int) Duration.between(mondayStartTime, mondayEndTime).toMinutes();
        } else {
            mondayDuration = 0;
        }

        Map<?, ?> tuesday = (Map<?, ?>) attrMap.get(ShiftDAO.TUE);
        if (tuesday != null) {
            LocalTime tuesdayStartTime = LocalTime.parse((String) tuesday.get(WORK_DAY_START));
            LocalTime tuesdayEndTime = LocalTime.parse((String) tuesday.get(WORK_DAY_END));

            if (endBeforeStart.test(tuesdayStartTime, tuesdayEndTime)) {
                throw new InvalidShiftException(END_BEFORE_START);
            }

            tuesdayDuration = (int) Duration.between(tuesdayStartTime, tuesdayEndTime).toMinutes();
        } else {
            tuesdayDuration = 0;
        }

        Map<?, ?> wednesday = (Map<?, ?>) attrMap.get(ShiftDAO.WED);
        if (wednesday != null) {
            LocalTime wednesdayStartTime = LocalTime.parse((String) wednesday.get(WORK_DAY_START));
            LocalTime wednesdayEndTime = LocalTime.parse((String) wednesday.get(WORK_DAY_END));

            if (endBeforeStart.test(wednesdayStartTime, wednesdayEndTime)) {
                throw new InvalidShiftException(END_BEFORE_START);
            }

            wednesdayDuration = (int) Duration.between(wednesdayStartTime, wednesdayEndTime).toMinutes();
        } else {
            wednesdayDuration = 0;
        }

        Map<?, ?> thursday = (Map<?, ?>) attrMap.get(ShiftDAO.THU);
        if (thursday != null) {
            LocalTime thursdayStartTime = LocalTime.parse((String) thursday.get(WORK_DAY_START));
            LocalTime thursdayEndTime = LocalTime.parse((String) thursday.get(WORK_DAY_END));

            if (endBeforeStart.test(thursdayStartTime, thursdayEndTime)) {
                throw new InvalidShiftException(END_BEFORE_START);
            }

            thursdayDuration = (int) Duration.between(thursdayStartTime, thursdayEndTime).toMinutes();
        } else {
            thursdayDuration = 0;
        }

        Map<?, ?> friday = (Map<?, ?>) attrMap.get(ShiftDAO.FRI);
        if (friday != null) {
            LocalTime fridayStartTime = LocalTime.parse((String) friday.get(WORK_DAY_START));
            LocalTime fridayEndTime = LocalTime.parse((String) friday.get(WORK_DAY_END));

            if (endBeforeStart.test(fridayStartTime, fridayEndTime)) {
                throw new InvalidShiftException(END_BEFORE_START);
            }

            fridayDuration = (int) Duration.between(fridayStartTime, fridayEndTime).toMinutes();
        } else {
            fridayDuration = 0;
        }

        Map<?, ?> saturday = (Map<?, ?>) attrMap.get(ShiftDAO.SAT);
        if (saturday != null) {
            LocalTime saturdayStartTime = LocalTime.parse((String) saturday.get(WORK_DAY_START));
            LocalTime saturdayEndTime = LocalTime.parse((String) saturday.get(WORK_DAY_END));

            if (endBeforeStart.test(saturdayStartTime, saturdayEndTime)) {
                throw new InvalidShiftException(END_BEFORE_START);
            }

            saturdayDuration = (int) Duration.between(saturdayStartTime, saturdayEndTime).toMinutes();
        } else {
            saturdayDuration = 0;
        }

        Map<?, ?> sunday = (Map<?, ?>) attrMap.get(ShiftDAO.SUN);
        if (sunday != null) {
            LocalTime sundayStartTime = LocalTime.parse((String) sunday.get(WORK_DAY_START));
            LocalTime sundayEndTime = LocalTime.parse((String) sunday.get(WORK_DAY_END));

            if (endBeforeStart.test(sundayStartTime, sundayEndTime)) {
                throw new InvalidShiftException(END_BEFORE_START);
            }

            sundayDuration = (int) Duration.between(sundayStartTime, sundayEndTime).toMinutes();
        } else {
            sundayDuration = 0;
        }

        Set<String> employeeLoginNames = attrMap.get(ShiftDAO.LOGIN_NAMES) == null ? new HashSet<>() : new HashSet<>((List<String>) attrMap.get(ShiftDAO.LOGIN_NAMES));
        int weeklyMinutes = mondayDuration + tuesdayDuration + wednesdayDuration + thursdayDuration + fridayDuration + saturdayDuration + sundayDuration;

        Map<String, Integer> workDurationsByDay = new HashMap<>(7);
        workDurationsByDay.put(ShiftDAO.MON, mondayDuration);
        workDurationsByDay.put(ShiftDAO.TUE, tuesdayDuration);
        workDurationsByDay.put(ShiftDAO.WED, wednesdayDuration);
        workDurationsByDay.put(ShiftDAO.THU, thursdayDuration);
        workDurationsByDay.put(ShiftDAO.FRI, fridayDuration);
        workDurationsByDay.put(ShiftDAO.SAT, saturdayDuration);
        workDurationsByDay.put(ShiftDAO.SUN, sundayDuration);

        validateEmployeeWorkHours(employeeLoginNames, weeklyMinutes, workDurationsByDay, NO_SHIFT_ID_YET);
    }

    @SuppressWarnings("unchecked")
    private void validateUpdateShift(Map<? super Object, ? super Object> attrMap, Map<?, ?> keyMap) throws Exception {
        int mondayDuration;
        int tuesdayDuration;
        int wednesdayDuration;
        int thursdayDuration;
        int fridayDuration;
        int saturdayDuration;
        int sundayDuration;
        BiPredicate<LocalTime, LocalTime> endBeforeStart = (start, end) -> end.isBefore(start);

        Map<?, ?> monday = (Map<?, ?>) attrMap.get(ShiftDAO.MON);
        if (monday != null) {
            LocalTime mondayStartTime = LocalTime.parse((String) monday.get(WORK_DAY_START));
            LocalTime mondayEndTime = LocalTime.parse((String) monday.get(WORK_DAY_END));

            if (endBeforeStart.test(mondayStartTime, mondayEndTime)) {
                throw new InvalidShiftException(END_BEFORE_START);
            }

            mondayDuration = (int) Duration.between(mondayStartTime, mondayEndTime).toMinutes();
        } else {
            mondayDuration = 0;
        }

        Map<?, ?> tuesday = (Map<?, ?>) attrMap.get(ShiftDAO.TUE);
        if (tuesday != null) {
            LocalTime tuesdayStartTime = LocalTime.parse((String) tuesday.get(WORK_DAY_START));
            LocalTime tuesdayEndTime = LocalTime.parse((String) tuesday.get(WORK_DAY_END));

            if (endBeforeStart.test(tuesdayStartTime, tuesdayEndTime)) {
                throw new InvalidShiftException(END_BEFORE_START);
            }

            tuesdayDuration = (int) Duration.between(tuesdayStartTime, tuesdayEndTime).toMinutes();
        } else {
            tuesdayDuration = 0;
        }

        Map<?, ?> wednesday = (Map<?, ?>) attrMap.get(ShiftDAO.WED);
        if (wednesday != null) {
            LocalTime wednesdayStartTime = LocalTime.parse((String) wednesday.get(WORK_DAY_START));
            LocalTime wednesdayEndTime = LocalTime.parse((String) wednesday.get(WORK_DAY_END));

            if (endBeforeStart.test(wednesdayStartTime, wednesdayEndTime)) {
                throw new InvalidShiftException(END_BEFORE_START);
            }

            wednesdayDuration = (int) Duration.between(wednesdayStartTime, wednesdayEndTime).toMinutes();
        } else {
            wednesdayDuration = 0;
        }

        Map<?, ?> thursday = (Map<?, ?>) attrMap.get(ShiftDAO.THU);
        if (thursday != null) {
            LocalTime thursdayStartTime = LocalTime.parse((String) thursday.get(WORK_DAY_START));
            LocalTime thursdayEndTime = LocalTime.parse((String) thursday.get(WORK_DAY_END));

            if (endBeforeStart.test(thursdayStartTime, thursdayEndTime)) {
                throw new InvalidShiftException(END_BEFORE_START);
            }

            thursdayDuration = (int) Duration.between(thursdayStartTime, thursdayEndTime).toMinutes();
        } else {
            thursdayDuration = 0;
        }

        Map<?, ?> friday = (Map<?, ?>) attrMap.get(ShiftDAO.FRI);
        if (friday != null) {
            LocalTime fridayStartTime = LocalTime.parse((String) friday.get(WORK_DAY_START));
            LocalTime fridayEndTime = LocalTime.parse((String) friday.get(WORK_DAY_END));

            if (endBeforeStart.test(fridayStartTime, fridayEndTime)) {
                throw new InvalidShiftException(END_BEFORE_START);
            }

            fridayDuration = (int) Duration.between(fridayStartTime, fridayEndTime).toMinutes();
        } else {
            fridayDuration = 0;
        }

        Map<?, ?> saturday = (Map<?, ?>) attrMap.get(ShiftDAO.SAT);
        if (saturday != null) {
            LocalTime saturdayStartTime = LocalTime.parse((String) saturday.get(WORK_DAY_START));
            LocalTime saturdayEndTime = LocalTime.parse((String) saturday.get(WORK_DAY_END));

            if (endBeforeStart.test(saturdayStartTime, saturdayEndTime)) {
                throw new InvalidShiftException(END_BEFORE_START);
            }

            saturdayDuration = (int) Duration.between(saturdayStartTime, saturdayEndTime).toMinutes();
        } else {
            saturdayDuration = 0;
        }

        Map<?, ?> sunday = (Map<?, ?>) attrMap.get(ShiftDAO.SUN);
        if (sunday != null) {
            LocalTime sundayStartTime = LocalTime.parse((String) sunday.get(WORK_DAY_START));
            LocalTime sundayEndTime = LocalTime.parse((String) sunday.get(WORK_DAY_END));

            if (endBeforeStart.test(sundayStartTime, sundayEndTime)) {
                throw new InvalidShiftException(END_BEFORE_START);
            }

            sundayDuration = (int) Duration.between(sundayStartTime, sundayEndTime).toMinutes();
        } else {
            sundayDuration = 0;
        }

        Set<String> employeeLoginNames = attrMap.get(ShiftDAO.LOGIN_NAMES) == null ? new HashSet<>() : new HashSet<>((List<String>) attrMap.get(ShiftDAO.LOGIN_NAMES));
        int weeklyMinutes = mondayDuration + tuesdayDuration + wednesdayDuration + thursdayDuration + fridayDuration + saturdayDuration + sundayDuration;

        List<String> queriedAtributeList = List.of(UserDAO.LOGIN_NAME);
        Map<? super Object, ? super Object> originalEmployeesInShift = new HashMap<>();
        EntityResult usersInOriginalShiftResult;
        Map<String, Integer> usersInShiftFilter = new HashMap<>();
        usersInShiftFilter.put(UserDAO.SHIFT_ID, (Integer) keyMap.get(ShiftDAO.ID));
        usersInOriginalShiftResult = this.daoHelper.query(userDAO, usersInShiftFilter, queriedAtributeList);
        originalEmployeesInShift.put(ShiftDAO.LOGIN_NAMES, (usersInOriginalShiftResult.get(UsersShiftsDAO.LOGIN_NAME)));

        List<String> previousLoginNames = (List<String>) originalEmployeesInShift.get(ShiftDAO.LOGIN_NAMES);
        employeeLoginNames.addAll(previousLoginNames == null ? new HashSet<>() : previousLoginNames);

        Map<String, Integer> workDurationsByDay = new HashMap<>(7);
        workDurationsByDay.put(ShiftDAO.MON, mondayDuration);
        workDurationsByDay.put(ShiftDAO.TUE, tuesdayDuration);
        workDurationsByDay.put(ShiftDAO.WED, wednesdayDuration);
        workDurationsByDay.put(ShiftDAO.THU, thursdayDuration);
        workDurationsByDay.put(ShiftDAO.FRI, fridayDuration);
        workDurationsByDay.put(ShiftDAO.SAT, saturdayDuration);
        workDurationsByDay.put(ShiftDAO.SUN, sundayDuration);

        attrMap.put(ShiftDAO.LOGIN_NAMES, employeeLoginNames);
        attrMap.put(ShiftDAO.ROLE_ID, attrMap.get(ShiftDAO.ROLE_ID));

        validateEmployeeWorkHours(employeeLoginNames, weeklyMinutes, workDurationsByDay, (int) keyMap.get(ShiftDAO.ID));
    }

    private void validateEmployeeWorkHours(Set<String> employeeLoginNames, int weeklyMinutes, Map<String, Integer> workDurationsByDay, int shiftId) throws Exception {
        List<String> queriedAtributeList;
        for (String employeeLoginName : employeeLoginNames) {
            if (employeeHasIncompatibleShift(employeeLoginName, shiftId)) {
                throw new Exception(employeeLoginName + " already has a shift assigned to them");
            }

            int employeeWeeklyMinutes = weeklyMinutes;

            queriedAtributeList = List.of(UsersDaysOffDAO.DAY);
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
                        employeeWeeklyMinutes = employeeWeeklyMinutes - workDurationsByDay.get(ShiftDAO.MON);
                        break;
                    case (ShiftDAO.TUE):
                        employeeWeeklyMinutes = employeeWeeklyMinutes - workDurationsByDay.get(ShiftDAO.TUE);
                        break;
                    case (ShiftDAO.THU):
                        employeeWeeklyMinutes = employeeWeeklyMinutes - workDurationsByDay.get(ShiftDAO.WED);
                        break;
                    case (ShiftDAO.WED):
                        employeeWeeklyMinutes = employeeWeeklyMinutes - workDurationsByDay.get(ShiftDAO.THU);
                        break;
                    case (ShiftDAO.FRI):
                        employeeWeeklyMinutes = employeeWeeklyMinutes - workDurationsByDay.get(ShiftDAO.FRI);
                        break;
                    case (ShiftDAO.SAT):
                        employeeWeeklyMinutes = employeeWeeklyMinutes - workDurationsByDay.get(ShiftDAO.SAT);
                        break;
                    case (ShiftDAO.SUN):
                        employeeWeeklyMinutes = employeeWeeklyMinutes - workDurationsByDay.get(ShiftDAO.SUN);
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

    private boolean employeeHasIncompatibleShift(String employeeLoginName, int shiftId) {
        Map<String, String> filter = new HashMap<>();
        filter.put(UserDAO.LOGIN_NAME, employeeLoginName);

        EntityResult result = userService.userQuery(filter, Collections.singletonList(UserDAO.SHIFT_ID));

        if (!result.isEmpty()) {
            int existingId = Optional.ofNullable((Integer) result.getRecordValues(0).get(UserDAO.SHIFT_ID)).orElse(-1);
            return existingId != -1 && existingId != shiftId;
        }

        return false;
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
        Set<String> employeeLoginNames = (Set<String>) attrMap.get(ShiftDAO.LOGIN_NAMES);

        Map<? super Object, ? super Object> keymapRoleId = new HashMap<>();
        keymapRoleId.put(ShiftDAO.ROLE_ID, attrMap.get(ShiftDAO.ROLE_ID));
        EntityResult employeeRolename = this.daoHelper.query(userRoleDAO, keymapRoleId, List.of(UserRoleDAO.NAME));
        attrMap.put(ShiftDAO.ROLENAME, (employeeRolename.getRecordValues(0)).get(ShiftDAO.ROLENAME));

        for (String employeeLoginName : employeeLoginNames) {
            if (!userService.getUserRoles(employeeLoginName).contains(attrMap.get(ShiftDAO.ROLENAME))) {
                throw new Exception(IShiftService.E_EMPLOYEE_ROLE_MISMATCH + ", " + employeeLoginName + " does not match");
            }
        }
    }
}
