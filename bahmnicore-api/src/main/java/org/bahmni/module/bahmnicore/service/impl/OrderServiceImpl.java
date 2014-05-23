package org.bahmni.module.bahmnicore.service.impl;

import org.bahmni.module.bahmnicore.dao.OrderDao;
import org.bahmni.module.bahmnicore.service.OrderService;
import org.openmrs.DrugOrder;
import org.openmrs.Order;
import org.openmrs.OrderType;
import org.openmrs.Patient;
import org.openmrs.api.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    private org.openmrs.api.OrderService orderService;
    private PatientService patientService;
    private OrderDao orderDao;

    @Autowired
    public OrderServiceImpl(org.openmrs.api.OrderService orderService, PatientService patientService, OrderDao orderDao) {
        this.orderService = orderService;
        this.patientService = patientService;
        this.orderDao = orderDao;
    }

    @Override
    public List<Order> getPendingOrders(String patientUuid, String orderTypeUuid) {
        Patient patient = patientService.getPatientByUuid(patientUuid);
        List<Patient> patients = Arrays.asList(patient);

        OrderType orderType = orderService.getOrderTypeByUuid(orderTypeUuid);
        List<OrderType> orderTypes = Arrays.asList(orderType);

        List<Order> allOrders = orderService.getOrders(Order.class, patients, null, org.openmrs.api.OrderService.ORDER_STATUS.NOTVOIDED, null, null, orderTypes);
        List<Order> completedOrders = orderDao.getCompletedOrdersFrom(Collections.unmodifiableList(allOrders));

        allOrders.removeAll(completedOrders);
        return allOrders;
    }

    @Override
    public List<DrugOrder> getActiveDrugOrders(String patientUuid) {
        Patient patient = patientService.getPatientByUuid(patientUuid);
        return orderDao.getActiveDrugOrders(patient);
    }
}