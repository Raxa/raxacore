package org.bahmni.module.bahmnicore.service.impl;

import org.bahmni.module.bahmnicore.model.BahmniDrugOrder;
import org.bahmni.module.bahmnicore.service.BahmniDrugOrderService;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class BahmniDrugOrderServiceImpl implements BahmniDrugOrderService {

    @Override
    public void add(String patientId, Date orderDate, List<BahmniDrugOrder> bahmniDrugOrders) {
        
    }
}
