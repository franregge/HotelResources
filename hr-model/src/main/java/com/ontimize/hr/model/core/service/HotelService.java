package com.ontimize.hr.model.core.service;

import com.ontimize.hr.api.core.service.IHotelService;
import com.ontimize.hr.api.core.service.IUserService;
import com.ontimize.hr.model.core.dao.HotelDAO;
import com.ontimize.hr.model.core.dao.UserDao;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.server.dao.DefaultOntimizeDaoHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Lazy
@Service("HotelService")
public class HotelService implements IHotelService {

    @Autowired
    private HotelDAO hotelDAO;

    @Autowired
    private DefaultOntimizeDaoHelper daoHelper;

    public void loginQuery(Map<?, ?> key, List<?> attr) {
    }

    //Sample to permission
    //@Secured({ PermissionsProviderSecured.SECURED })

    @Override
    public EntityResult hotelQuery(Map<?, ?> keyMap, List<?> attrList) {
        return this.daoHelper.query(hotelDAO, keyMap, attrList);
    }

    @Override
    public EntityResult hotelInsert(Map<?, ?> attrMap) {

        return this.daoHelper.insert(hotelDAO, attrMap);
    }

    @Override
    public EntityResult hotelUpdate(Map<?, ?> attrMap, Map<?, ?> keyMap) {
        return this.daoHelper.update(hotelDAO, attrMap, keyMap);
    }

}
