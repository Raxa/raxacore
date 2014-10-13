package org.openmrs.module.bahmnicore.web.v1_0.controller;

import org.apache.log4j.Logger;
import org.bahmni.csv.KeyValue;
import org.bahmni.fileexport.FileExporter;
import org.bahmni.module.admin.csv.exporter.ConceptSetExporter;
import org.bahmni.module.admin.csv.models.ConceptRow;
import org.openmrs.api.AdministrationService;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.v1_0.controller.BaseRestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Controller
public class AdminExportController extends BaseRestController {
    private final String baseUrl = "/rest/" + RestConstants.VERSION_1 + "/bahmnicore/admin/export";
    private static Logger logger = Logger.getLogger(AdminExportController.class);

    @Autowired
    private ConceptSetExporter conceptSetExporter;

    @RequestMapping(value = baseUrl + "/conceptset", method = RequestMethod.GET)
    @ResponseBody
    public void export(HttpServletResponse response) {
        try {
            FileExporter<ConceptRow> fileExporter = new FileExporter<ConceptRow>();

//            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//            List<ConceptRow> conceptRows = conceptSetExporter.exportConcepts("All_Tests_and_Panels");
//            response.setContentType("application/zip");
//            response.setHeader("Content-Disposition", "attachment; filename=\"" + "trial.zip" + "\"");
//            outputStream = fileExporter.exportCSV(conceptRows, outputStream);
//            ZipOutputStream zipOutputStream = new ZipOutputStream(response.getOutputStream());
//            zipOutputStream.putNextEntry(new ZipEntry("some.csv"));
//            zipOutputStream.write(outputStream.toByteArray());
//            zipOutputStream.closeEntry();
//            zipOutputStream.close();
//            response.flushBuffer();
        } catch (Exception e) {
            logger.error("Could not upload file", e);
        }
    }
}
