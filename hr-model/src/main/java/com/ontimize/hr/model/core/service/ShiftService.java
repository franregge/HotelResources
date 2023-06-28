package com.ontimize.hr.model.core.service;

import com.ontimize.hr.api.core.service.IShiftService;
import com.ontimize.hr.api.core.service.exception.EmployeeNotFoundException;
import com.ontimize.hr.api.core.service.exception.InvalidWeeklyHoursException;
import com.ontimize.hr.api.core.service.exception.UserDataException;
import com.ontimize.hr.api.core.service.exception.UserDoesNotExistException;
import com.ontimize.hr.model.core.dao.ShiftDAO;
import com.ontimize.hr.model.core.dao.UserDAO;
import com.ontimize.hr.model.core.dao.UserRoleDAO;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.common.dto.EntityResultMapImpl;
import com.ontimize.jee.common.security.PermissionsProviderSecured;
import com.ontimize.jee.server.dao.DefaultOntimizeDaoHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalTime;
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

    @Secured({PermissionsProviderSecured.SECURED})
    @Override
    public EntityResult shiftQuery(Map<?, ?> keyMap, List<?> attrList) {
        return null;
    }

    @SuppressWarnings("unchecked")
    @Secured({PermissionsProviderSecured.SECURED})
    @Override
    public EntityResult shiftInsert(Map<? super Object, ? super Object> attrMap) {
        // TODO: guardar lista de empleados de un Shift, role_id, días máximos de traballo
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
            result.setMessage(IShiftService.OPERATION_SUCCESS);
        } catch (Exception e) {
            result = new EntityResultMapImpl();
            result.setMessage(e.getMessage());
            result.setCode(EntityResult.OPERATION_WRONG);
        }
        return result;
    }

    @Secured({PermissionsProviderSecured.SECURED})
    @Override
    public EntityResult shiftUpdate(Map<? super Object, ? super Object> attrMap, Map<?, ?> keyMap) {
        // TODO: probar método
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


            result = this.daoHelper.insert(this.shiftDAO, attrMap);
            result.setCode(EntityResult.OPERATION_SUCCESSFUL_SHOW_MESSAGE);
            result.setMessage(IShiftService.OPERATION_SUCCESS);
        } catch (Exception e) {
            result = new EntityResultMapImpl();
            result.setMessage(e.getMessage());
            result.setCode(EntityResult.OPERATION_WRONG);

        }
        return result;
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

        if (mondayDuration + tuesdayDuration + wednesdayDuration + thursdayDuration + fridayDuration + saturdayDuration + sundayDuration > 40) {
            throw new InvalidWeeklyHoursException(IShiftService.E_MORE_THAN_40H);
        }
    }
    //Comprobar que o empregado ten o rol propio do turno

    private void roleMatcher(Map<?, ?> attrMap) throws Exception {
        List<String> employeeLoginNames = (List) attrMap.get(ShiftDAO.LOGIN_NAMES);
        for (String employeeLoginName : employeeLoginNames) {
            if (!(userService.getUserRoles(employeeLoginName)).contains(attrMap.get(ShiftDAO.ROLE_NAME))) {
                throw new Exception(IShiftService.E_EMPLOYEE_ROLE_MISMATCH + ", " + employeeLoginName + " does not match");
            }
        }
    }


}
