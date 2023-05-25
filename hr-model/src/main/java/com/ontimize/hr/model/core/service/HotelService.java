package com.ontimize.hr.model.core.service;

import com.ontimize.hr.api.core.service.IHotelService;
import com.ontimize.hr.api.core.service.IUserService;
import com.ontimize.hr.api.core.service.exception.HotelAlreadyExistsException;
import com.ontimize.hr.api.core.service.exception.InvalidNumberOfFloorsException;
import com.ontimize.hr.model.core.dao.HotelDAO;
import com.ontimize.hr.model.core.dao.UserDao;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.server.dao.DefaultOntimizeDaoHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.*;

@Lazy
@Service("HotelService")
public class HotelService implements IHotelService {

    @Autowired
    private HotelDAO hotelDAO;

    @Autowired
    private DefaultOntimizeDaoHelper daoHelper;


    //Sample to permission
    //@Secured({ PermissionsProviderSecured.SECURED })

    @Override
    public EntityResult hotelQuery(Map<?, ?> keyMap, List<?> attrList) {
        return this.daoHelper.query(hotelDAO, keyMap, attrList);
    }

    @Override
    public EntityResult hotelInsert(Map<?, ?> attrMap) throws HotelAlreadyExistsException, InvalidNumberOfFloorsException {
        Map<String,String>filter =new HashMap<>();
        filter.put("name",(String) attrMap.get("name"));
        List<String>attrList= List.of("name");
        EntityResult entityResult = hotelQuery(filter,attrList);

        if (entityResult.calculateRecordNumber()>0) {
            throw new HotelAlreadyExistsException("This hotel already exists");
        }

        if ((int)attrMap.get("NUMBER_OF_FLOORS") > 9 || (int)attrMap.get("NUMBER_OF_FLOORS") < 1) {
            throw new InvalidNumberOfFloorsException("The number of floors must be between 1 and 9");
        }

        return this.daoHelper.insert(hotelDAO, attrMap);
    }

    @Override
    public EntityResult hotelUpdate(Map<?, ?> attrMap, Map<?, ?> keyMap) {


        return this.daoHelper.update(hotelDAO, attrMap, keyMap);
    }

}
