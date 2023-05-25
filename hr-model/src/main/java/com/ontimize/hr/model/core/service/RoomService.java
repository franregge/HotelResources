package com.ontimize.hr.model.core.service;

import com.ontimize.hr.api.core.service.IRoomService;
import com.ontimize.hr.model.core.dao.RoomDAO;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.server.dao.DefaultOntimizeDaoHelper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

public class RoomService implements IRoomService {

    @Autowired
    private DefaultOntimizeDaoHelper daoHelper;
    @Autowired
    private RoomDAO roomDAO;

    @Override
    public EntityResult roomQuery(Map<?, ?> keymap, List<?> attrList) {
        return this.daoHelper.query(this.roomDAO, keymap, attrList);
    }

    @Override
    public EntityResult roomInsert(Map<?, ?> attrMap) {
        return this.daoHelper.insert(this.roomDAO, attrMap);
    }

    @Override
    public EntityResult roomUpdate(Map<?, ?> attrMap, Map<?, ?> keyMap) {
        return this.daoHelper.update(this.roomDAO, attrMap, keyMap);
    }
}
