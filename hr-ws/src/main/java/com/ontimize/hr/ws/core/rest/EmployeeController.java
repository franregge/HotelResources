package com.ontimize.hr.ws.core.rest;

import com.ontimize.hr.api.core.service.IEmployeeService;
import com.ontimize.hr.model.core.RoleNames;
import com.ontimize.hr.model.core.dao.EmployeesEntryDepartureDAO;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.common.dto.EntityResultMapImpl;
import com.ontimize.jee.common.services.user.UserInformation;
import com.ontimize.jee.server.rest.ORestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/employees")
public class EmployeeController extends ORestController<IEmployeeService> {

    @Autowired
    private IEmployeeService employeeService;

    @Override
    public IEmployeeService getService() {
        return employeeService;
    }

    @PostMapping("/clockIn")
    public EntityResult clockIn(@RequestBody Map<? super Object, ? super Object> data, Authentication authentication) {
        EntityResult result;

        if (authentication.getAuthorities().stream().noneMatch(auth -> auth.getAuthority().equals(RoleNames.EMPLOYEE))) {
            result = new EntityResultMapImpl();
            result.setMessage(EmployeesEntryDepartureDAO.E_NOT_EMPLOYEE);
            result.setCode(EntityResult.OPERATION_WRONG);
            return result;
        }

        if (!((UserInformation) authentication.getPrincipal()).getUsername().equals(((Map<?, ?>) data.get("data")).get(EmployeesEntryDepartureDAO.LOGIN_NAME))) {
             result = new EntityResultMapImpl();
             result.setCode(EntityResult.OPERATION_WRONG);
             result.setMessage(EmployeesEntryDepartureDAO.E_CANNOT_CLOCK_IN_OTHERS);
             return result;
        }

        return employeeService.clockInInsert((Map<? super Object, ? super Object>) data.get("data"));
    }
    @PostMapping("/employeesPerShift")
    public EntityResult employeesPerShiftQuery(@RequestBody  Map<?super Object,? super Object>filter){

        return employeeService.employeesPerShiftQuery((Map<? super Object, ? super Object>) filter.get("filter"),new HashMap<>());
    }

}
