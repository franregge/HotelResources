package com.ontimize.hr.model.core.service;

import com.ontimize.hr.api.core.service.IEmployeeService;
import com.ontimize.hr.api.core.service.IShiftService;
import com.ontimize.hr.api.core.service.IUserService;
import com.ontimize.hr.api.core.service.exception.InvalidShiftException;
import com.ontimize.hr.model.core.RoleNames;
import com.ontimize.hr.model.core.dao.*;
import com.ontimize.jee.common.db.SQLStatementBuilder;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.common.dto.EntityResultMapImpl;
import com.ontimize.jee.common.security.PermissionsProviderSecured;
import com.ontimize.jee.common.tools.EntityResultTools;
import com.ontimize.jee.server.dao.DefaultOntimizeDaoHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import java.sql.Time;
import java.time.*;
import java.util.*;

@Lazy
@Service("EmployeeService")
public class EmployeeService implements IEmployeeService {

    @Autowired
    private UserService userService;
    @Autowired
    private DefaultOntimizeDaoHelper daoHelper;
    @Autowired
    private UserRoleDAO userRoleDAO;

    @Autowired
    private UsersDaysOffDAO usersDaysOffDAO;
    @Autowired
    private EmployeesEntryDepartureDAO employeesEntryDepartureDAO;


    @Secured({PermissionsProviderSecured.SECURED})
    @Override
    public EntityResult employeeQuery(Map<?, ?> filter, List<?> attrList) {
        return userService.userIdentifiedQuery(filter, attrList, UserDAO.Q_EMPLOYEE_QUERY);
    }

    @SuppressWarnings("unchecked")
    @Secured({PermissionsProviderSecured.SECURED})
    @Override
    public EntityResult employeeInsert(Map<? super Object, ? super Object> attrMap) {
        EntityResult result;

        attrMap.put(UserDAO.ROLE_NAME, RoleNames.EMPLOYEE);

        Set<String> daysOff = attrMap.get(UserDAO.DAYS_OFF) == null ? null : new HashSet<>((List<String>) attrMap.get(UserDAO.DAYS_OFF));

        Set<String> days = new HashSet<>(List.of(
                "monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday"
        ));

        try {
            Map<String, String> filter = new HashMap<>();
            filter.put(UserRoleDAO.NAME, RoleNames.EMPLOYEE);
            List<String> queriedAttrs = List.of(UserRoleDAO.ID);
            EntityResult roleQueryEntityResult = daoHelper.query(userRoleDAO, filter, queriedAttrs);
            Integer employeeRoleId = (Integer) roleQueryEntityResult.getRecordValues(0).get(UserRoleDAO.ID);
            String specificRoleName = (String) attrMap.get(UserDAO.EMPLOYEE_ROLE);

            // TODO: ufffffffffff.... quitar isto pronto, uso de enums
            if (
                    specificRoleName.equals(RoleNames.CLIENT)
                            || specificRoleName.equals(RoleNames.MANAGER)
                            || specificRoleName.equals(RoleNames.GUEST)
                            || specificRoleName.equals(RoleNames.ROOT)
            ) {
                throw new Exception("Invalid employee type");
            }

            filter.put(UserRoleDAO.NAME, specificRoleName);
            roleQueryEntityResult = daoHelper.query(userRoleDAO, filter, queriedAttrs);
            Integer specificRoleId = (Integer) roleQueryEntityResult.getRecordValues(0).get(UserRoleDAO.ID);

            attrMap.put(UserDAO.ROLE_IDS, List.of(employeeRoleId, specificRoleId));

            validateDaysOff(daysOff, days);

            result = userService.userInsert(attrMap);

            Map<String, String> daysOffToInsert = new HashMap<>();
            daysOffToInsert.put(UsersDaysOffDAO.LOGIN_NAME, (String) attrMap.get(UserDAO.LOGIN_NAME));

            for (String dayOff : daysOff) {
                daysOffToInsert.put(UsersDaysOffDAO.DAY, dayOff);
                daoHelper.insert(this.usersDaysOffDAO, daysOffToInsert);
            }

            if (result.getCode() == EntityResult.OPERATION_WRONG) {
                return result;
            }

            result.setCode(EntityResult.OPERATION_SUCCESSFUL_SHOW_MESSAGE);
            result.setMessage(IUserService.USER_INSERT_SUCCESS);
        } catch (Exception e) {
            result = new EntityResultMapImpl();
            result.setMessage(e.getMessage());
            result.setCode(EntityResult.OPERATION_WRONG);

        }

        return result;
    }

    @Secured({PermissionsProviderSecured.SECURED})
    public EntityResult clockInInsert(Map<? super Object, ? super Object> attrMap) {
        EntityResult result;

        String employeeLoginName = (String) attrMap.get(UserDAO.LOGIN_NAME);

        try {
            LocalDateTime entry = LocalDateTime.now();
            Map<String, ? super Object> entryMap = new HashMap<>();
            entryMap.put(EmployeesEntryDepartureDAO.ENTRY, entry);

            Map<String, ? super Object> filter = new HashMap<>();
            filter.put(UserDAO.LOGIN_NAME, employeeLoginName);
            filter.put(EmployeesEntryDepartureDAO.WORKING_DAY, Date.from(entry.toLocalDate().atStartOfDay(ZoneId.systemDefault()).toInstant()));

            List<String> queriedAttrs = List.of(EmployeesEntryDepartureDAO.WORKING_DAY);
            EntityResult userEntryEntityResult = daoHelper.query(employeesEntryDepartureDAO, filter, queriedAttrs);

            if (userEntryEntityResult.isEmpty()) {
                entryMap.put(EmployeesEntryDepartureDAO.WORKING_DAY, Date.from(entry.toLocalDate().atStartOfDay(ZoneId.systemDefault()).toInstant()));
                entryMap.put(EmployeesEntryDepartureDAO.LOGIN_NAME, filter.get(EmployeesEntryDepartureDAO.LOGIN_NAME));
                entryMap.put(EmployeesEntryDepartureDAO.ENTRY, Time.valueOf(entry.toLocalTime()));

                result = daoHelper.insert(employeesEntryDepartureDAO, entryMap);
                result.setCode(EntityResult.OPERATION_SUCCESSFUL_SHOW_MESSAGE);
                result.setMessage(EmployeesEntryDepartureDAO.OPERATION_SUCCESS);
            } else {
                throw new Exception(EmployeesEntryDepartureDAO.E_ENTRY_SAVED);
            }
        } catch (Exception e) {
            result = new EntityResultMapImpl();
            result.setMessage(e.getMessage());
            result.setCode(EntityResult.OPERATION_WRONG);
        }

        return result;
    }

    @Secured({PermissionsProviderSecured.SECURED})
    public EntityResult clockOutUpdate(final Map<? super Object, ? super Object> attrMap, Map<? super Object, ? super Object> filter) {
        EntityResult result;
        String employeeLoginName = (String) filter.get(UserDAO.LOGIN_NAME);

        try {
            LocalDateTime clockOut = LocalDateTime.now();

            Map<String, ? super Object> existingRecordFilter = new HashMap<>();
            existingRecordFilter.put(UserDAO.LOGIN_NAME, employeeLoginName);
            existingRecordFilter.put(EmployeesEntryDepartureDAO.WORKING_DAY, Date.from(clockOut.toLocalDate().atStartOfDay(ZoneId.systemDefault()).toInstant()));

            List<String> queriedAttrs = List.of(EmployeesEntryDepartureDAO.ID, EmployeesEntryDepartureDAO.DEPARTURE);
            EntityResult userEntryEntityResult = daoHelper.query(employeesEntryDepartureDAO, existingRecordFilter, queriedAttrs);

            if (userEntryEntityResult.isEmpty()) {
                throw new Exception(EmployeesEntryDepartureDAO.E_NO_CLOCK_IN);
            }

            if (userEntryEntityResult.getRecordValues(0).get(EmployeesEntryDepartureDAO.DEPARTURE) != null) {
                throw new Exception(EmployeesEntryDepartureDAO.E_ALREADY_CLOCKED_OUT);
            }

            attrMap.put(EmployeesEntryDepartureDAO.DEPARTURE, Time.valueOf(clockOut.toLocalTime()));
            filter.put(EmployeesEntryDepartureDAO.ID, userEntryEntityResult.getRecordValues(0).get(EmployeesEntryDepartureDAO.ID));

            result = daoHelper.update(employeesEntryDepartureDAO, attrMap, filter);
            result.setCode(EntityResult.OPERATION_SUCCESSFUL_SHOW_MESSAGE);
            result.setMessage(EmployeesEntryDepartureDAO.CLOCK_OUT_SUCCESS);
        } catch (Exception e) {
            result = new EntityResultMapImpl();
            result.setMessage(e.getMessage());
            result.setCode(EntityResult.OPERATION_WRONG);
        }

        return result;
    }

    private void validateDaysOff(Set<String> daysOff, Set<String> days) throws InvalidShiftException {
        if (daysOff != null) {
            if (daysOff.isEmpty()) {
                throw new InvalidShiftException(IShiftService.NO_DAYS_OFF);
            }

            if (!days.containsAll(daysOff)) {
                throw new InvalidShiftException(IShiftService.INVALID_DAY_OFF);
            }

            if (daysOff.containsAll(days)) {
                throw new InvalidShiftException(IShiftService.ALL_DAYS_OFF);
            }
        } else {
            throw new InvalidShiftException(IShiftService.NO_DAYS_OFF);
        }
    }

    @Override
    @Secured({PermissionsProviderSecured.SECURED})
    public EntityResult employeeDelete(Map<?, ?> keyMap) {
        String loginName = (String) keyMap.get(UserDAO.LOGIN_NAME);
        EntityResult result;

        try {
            if (loginName == null) {
                throw new IllegalArgumentException("You must provide a username");
            }

            if (!userService.getUserRoles(loginName).contains(RoleNames.EMPLOYEE)) {
                throw new AccessDeniedException(ERR_CANNOT_DELETE_USER);
            }

            result = userService.userDelete(keyMap);
        } catch (Exception e) {
            result = new EntityResultMapImpl();
            result.setCode(EntityResult.OPERATION_WRONG);
            result.setMessage(e.getMessage());
        }

        return result;
    }

    @Override
    @Secured({PermissionsProviderSecured.SECURED})
    public EntityResult employeeUpdate(Map<? super Object, ? super Object> attrMap, Map<? super Object, ? super Object> filter) {

        EntityResult result;

        try {
            if (!userService.getUserRoles((String) filter.get(UserDAO.LOGIN_NAME)).contains(RoleNames.EMPLOYEE)) {
                throw new AccessDeniedException(IUserService.WRONG_ROLE);
            }

            result = userService.userUpdate(attrMap, filter);

        } catch (Exception e) {
            result = new EntityResultMapImpl();
            result.setCode(EntityResult.OPERATION_WRONG);
            result.setMessage(e.getMessage());
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    @Override
    @Secured({PermissionsProviderSecured.SECURED})
    public EntityResult employeesPerShiftQuery(Map<? super Object, ? super Object> filter, final Map<? super Object, ? super Object> attrMap) {
        LocalDate localDate = LocalDate.now();
        String dayOfWeekName = localDate.getDayOfWeek().toString().toLowerCase();

        EntityResult result;
        try {
            Map<String, ? super Object> shiftAndCurrentEmployees = new HashMap<>();

            Map<String, Object> employeesInShiftFilter = new HashMap<>();
            employeesInShiftFilter.put(UserDAO.SHIFT_ID, filter.get(ShiftDAO.ID));
            Map<String, Object> employeesInShiftFilterFreeToday = new HashMap<>(employeesInShiftFilter);
            employeesInShiftFilterFreeToday.put(UsersDaysOffDAO.DAY, dayOfWeekName);
            List<String> employeesFreeToday = Optional.ofNullable((List<String>) daoHelper
                    .query(usersDaysOffDAO, employeesInShiftFilterFreeToday, List.of(UsersDaysOffDAO.LOGIN_NAME))
                    .get(UsersDaysOffDAO.LOGIN_NAME)).orElse(new ArrayList<>());
            List<String> employeeLoginNames = (List<String>) userService.userQuery(employeesInShiftFilter, List.of(UserDAO.LOGIN_NAME)).get(UserDAO.LOGIN_NAME);
            employeeLoginNames.removeAll(employeesFreeToday);

            List<Map<String, Object>> employeesInShift = new ArrayList<>();
            Map<String, Object> employeeClockedInFilter = new HashMap<>();
            for (String employeeLoginName : employeeLoginNames) {
                employeeClockedInFilter.put(EmployeesEntryDepartureDAO.LOGIN_NAME, employeeLoginName);
                employeeClockedInFilter.put(EmployeesEntryDepartureDAO.WORKING_DAY, Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
                EntityResult employeeStatusResult = daoHelper
                        .query(
                                employeesEntryDepartureDAO,
                                employeeClockedInFilter,
                                List.of(EmployeesEntryDepartureDAO.ENTRY, EmployeesEntryDepartureDAO.DEPARTURE)
                        );

                boolean isFinished = !employeeStatusResult
                        .getRecordValues(0)
                        .isEmpty() &&
                        (employeeStatusResult
                                .getRecordValues(0)
                                .get(EmployeesEntryDepartureDAO.DEPARTURE)) != null;

                boolean isPresent = !isFinished &&
                        !employeeStatusResult
                                .getRecordValues(0)
                                .isEmpty() &&
                        employeeStatusResult
                                .getRecordValues(0)
                                .get(EmployeesEntryDepartureDAO.ENTRY) != null;

                Map<String, Object> employee = new HashMap<>();
                employee.put("login_name", employeeLoginName);
                employee.put("present", isPresent);
                employee.put("shift_finished", isFinished);
                employeesInShift.add(employee);
            }

            shiftAndCurrentEmployees.put("employees", employeesInShift);
            shiftAndCurrentEmployees.put(ShiftDAO.ID, filter.get(ShiftDAO.ID));
            result = new EntityResultMapImpl();
            result.addRecord(shiftAndCurrentEmployees);
        } catch (Exception e) {
            result = new EntityResultMapImpl();
            result.setCode(EntityResult.OPERATION_WRONG);
            result.setMessage(e.getMessage());
            e.printStackTrace();
        }

        return result;
    }
}
