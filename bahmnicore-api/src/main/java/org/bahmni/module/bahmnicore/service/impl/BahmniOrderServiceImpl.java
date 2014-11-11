package org.bahmni.module.bahmnicore.service.impl;

import org.bahmni.module.bahmnicore.dao.BahmniOrderDao;
import org.bahmni.module.bahmnicore.service.BahmniOrderService;
import org.openmrs.CareSetting;
import org.openmrs.DrugOrder;
import org.openmrs.Order;
import org.openmrs.Patient;
import org.openmrs.api.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;
import java.util.List;

@Service
public class BahmniOrderServiceImpl implements BahmniOrderService {

    private org.openmrs.api.OrderService orderService;
    private PatientService patientService;
    private BahmniOrderDao bahmniOrderDao;

    @Autowired
    public BahmniOrderServiceImpl(org.openmrs.api.OrderService orderService, PatientService patientService, BahmniOrderDao bahmniOrderDao) {
        this.orderService = orderService;
        this.patientService = patientService;
        this.bahmniOrderDao = bahmniOrderDao;
    }

    @Override
    public List<Order> getPendingOrders(String patientUuid, String orderTypeUuid) {
        Patient patient = patientService.getPatientByUuid(patientUuid);
        List<Order> allOrders = orderService.getAllOrdersByPatient(patient);
        orderService.getActiveOrders(patient, null, orderService.getCareSettingByName(CareSetting.CareSettingType.OUTPATIENT.toString()), new Date());
        List<Order> completedOrders = bahmniOrderDao.getCompletedOrdersFrom(Collections.unmodifiableList(allOrders));
        allOrders.removeAll(completedOrders);
        return allOrders;
    }

    @Override
    public List<DrugOrder> getScheduledDrugOrders(String patientUuid) {
        Patient patient = patientService.getPatientByUuid(patientUuid);
        return bahmniOrderDao.getScheduledDrugOrders(patient);
    }
}