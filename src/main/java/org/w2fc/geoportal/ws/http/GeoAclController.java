package org.w2fc.geoportal.ws.http;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.w2fc.conf.ObjectFactory;
import org.w2fc.geoportal.dao.GeoACLDao;
import org.w2fc.geoportal.domain.GeoObject;
import org.w2fc.geoportal.domain.GeoUser;
import org.w2fc.geoportal.utils.ServiceRegistry;
import org.w2fc.geoportal.ws.model.GetObjectsResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Yevhen
 */
@Controller
@RequestMapping("/acl")
public class GeoAclController {

    @Autowired
    private ServiceRegistry serviceRegistry;

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public GetObjectsResponse list(){

        GeoUser user = serviceRegistry.getUserDao().getCurrentGeoUser();

        List<GeoObject> aclObjects = serviceRegistry.getACLDao().listByUser(user.getId());

        List<GeoObject> aclObjectsWithTags = new ArrayList<GeoObject>();
        for (GeoObject aclObject : aclObjects) {
            aclObjectsWithTags.add(serviceRegistry.getGeoObjectDao().getWithTags(aclObject.getId()));
        }

        GetObjectsResponse response = new GetObjectsResponse();
        response.setList(ObjectFactory.createGeoObjectFullAdapterList(aclObjectsWithTags));

        return response;
    }
}
