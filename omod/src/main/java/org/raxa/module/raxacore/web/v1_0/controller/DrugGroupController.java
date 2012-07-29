package org.raxa.module.raxacore.web.v1_0.controller;

import com.google.common.base.Joiner;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RestUtil;
import org.openmrs.module.webservices.rest.web.annotation.WSDoc;
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.openmrs.module.webservices.rest.web.v1_0.controller.BaseRestController;
import org.raxa.module.raxacore.DrugGroup;
import org.raxa.module.raxacore.DrugGroupService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping(value = "/rest/v1/raxacore/druggroup")
public class DrugGroupController extends BaseRestController {

    DrugGroupService service = Context.getService(DrugGroupService.class);
    Gson gson = new GsonBuilder().serializeNulls().create();
    //	SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
    private static final String[] REF = {"uuid", "drug_group_name"};

    //<editor-fold defaultstate="collapsed" desc="getResourceVersion">
    /**
     * Returns the Resource Version
     */
    private String getResourceVersion() {
        return "1.0";
    }

    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="GET all">
    /**
     * Get all the unretired patient lists (as REF representation) in the system
     *
     * @param request
     * @param response
     * @return
     * @throws ResponseException
     */
    @RequestMapping(method = RequestMethod.GET)
    @WSDoc("Get All Unretired DrugGroup Lists in the system")
    @ResponseBody()
    public String getDrugGroupList(HttpServletRequest request, HttpServletResponse response) throws ResponseException {
        List<DrugGroup> drugGroupList = service.getDrugGroupList();
        ArrayList results = new ArrayList();
        for (DrugGroup drugGroup : drugGroupList) {
            SimpleObject obj = new SimpleObject();
            obj.add("uuid", drugGroup.getUuid());
            obj.add("drug_group_name", drugGroup.getDrugGroupName());
            results.add(obj);
        }
        return gson.toJson(new SimpleObject().add("results", results));
    }

    //</editor-fold>   
    //<editor-fold defaultstate="collapsed" desc="POST - Update List">
    /**
     * Updates the Patient List by making a POST call with uuid in URL and
     *
     * @param uuid the uuid for the patient list resource
     * @param post
     * @param request
     * @param response
     * @return 200 response status
     * @throws ResponseException
     */
    @RequestMapping(method = RequestMethod.POST)
    @WSDoc("Create a new DrugGroup")
    @ResponseBody
    public Object updateDrugGroup(@RequestBody SimpleObject post, HttpServletRequest request, HttpServletResponse response)
            throws ResponseException {
        DrugGroup drugGroup = new DrugGroup();
        drugGroup.setUuid(post.get("uuid").toString());
        drugGroup.setDrugGroupName(post.get("name").toString());
        drugGroup.setDateCreated(new Date());
        DrugGroup created = service.saveDrugGroup(drugGroup);
        SimpleObject obj = new SimpleObject();
        obj.add("uuid", created.getUuid());
        obj.add("name", created.getDrugGroupName());
        obj.add("date", created.getDateCreated());
        return RestUtil.noContent(response);
    }
    //</editor-fold>
}
