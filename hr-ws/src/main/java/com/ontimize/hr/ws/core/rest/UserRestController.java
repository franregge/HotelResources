package com.ontimize.hr.ws.core.rest;


import com.ontimize.hr.model.core.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ontimize.hr.api.core.service.IUserService;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.server.rest.ORestController;

import java.util.Map;


@RestController
@RequestMapping("/users")
public class UserRestController extends ORestController<IUserService> {

    @Autowired
    private IUserService userSrv;

    @Override
    public IUserService getService() {
        return this.userSrv;
    }

    @RequestMapping(
            value = "/login",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EntityResult> login() {
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(
            value="/employee",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EntityResult> employeeInsert(@RequestBody Map<?, ?>attrMap){
        EntityResult result = userSrv.employeeInsert(attrMap);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping(value = "/signup", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EntityResult> addUser(@RequestBody Map<?, ?> attrMap) {
        return new ResponseEntity<>(userSrv.userInsert(attrMap), HttpStatus.OK);
    }
}
