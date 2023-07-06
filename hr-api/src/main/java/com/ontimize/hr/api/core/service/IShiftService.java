package com.ontimize.hr.api.core.service;

import com.ontimize.hr.api.core.service.exception.UserDoesNotExistException;
import com.ontimize.jee.common.dto.EntityResult;

import java.util.List;
import java.util.Map;

public interface IShiftService {

    EntityResult shiftQuery(Map<? super Object, ? super Object> filter, List<? super Object> attrList);

    EntityResult shiftInsert(Map<? super Object, ? super Object> attrMap);

    EntityResult shiftUpdate(Map<? super Object, ? super  Object> attrMap, Map<? super Object, ? super Object> keyMap) throws UserDoesNotExistException;

    EntityResult shiftDelete(Map<?, ?> keyMap);

    String E_MORE_THAN_40H = "This employee cannot work more than 40 hours in a week: ";
    String E_EMPLOYEE_ROLE_MISMATCH = "Employee role has to match shift role";
    String INSERTION_SUCCESS = "Shift inserted successfully";
    String UPDATE_SUCCESS = "Shift updated successfully";
    String END_BEFORE_START = "Shift end time must be after the start time";
    String NO_DAYS_OFF = "Employees must have at least one day off";
    String INVALID_DAY_OFF = "This day off is not valid";
    String ALL_DAYS_OFF = "Employees can't have all days off";
    String E_INVALID_DAY_OFF_SAVED = "Employee has invalid day off";
    String E_NOT_FOUND = "No shift with the specified id was found";
    String MULTIPLE_SHIFTS = "The employee has more than one shift";
    String EMPLOYEE_FILTER = "employee";
    String EMPLOYEE_COLUMNS = "columns";
    String SHIFT_EMPLOYEES = "employees";

}
