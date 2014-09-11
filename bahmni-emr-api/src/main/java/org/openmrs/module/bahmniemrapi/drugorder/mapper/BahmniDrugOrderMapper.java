package org.openmrs.module.bahmniemrapi.drugorder.mapper;

import org.openmrs.DrugOrder;
import org.openmrs.module.bahmniemrapi.drugorder.contract.BahmniDrugOrder;
import org.openmrs.module.emrapi.encounter.OrderMapper;
import org.openmrs.module.emrapi.encounter.mapper.OrderMapper1_10;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BahmniDrugOrderMapper {

    private BahmniProviderMapper providerMapper;

    public BahmniDrugOrderMapper(BahmniProviderMapper providerMapper) {
        this.providerMapper = providerMapper;
    }

    public List<BahmniDrugOrder> mapToResponse(List<DrugOrder> activeDrugOrders) throws IOException {

        OrderMapper drugOrderMapper = new OrderMapper1_10();

        List<BahmniDrugOrder> bahmniDrugOrders = new ArrayList<>();

        for (DrugOrder openMRSDrugOrder : activeDrugOrders) {
            BahmniDrugOrder bahmniDrugOrder = new BahmniDrugOrder();
            bahmniDrugOrder.setDrugOrder(drugOrderMapper.mapDrugOrder(openMRSDrugOrder));
            bahmniDrugOrder.setVisit(openMRSDrugOrder.getEncounter().getVisit());
            bahmniDrugOrder.setProvider(providerMapper.map(openMRSDrugOrder.getOrderer()));
            bahmniDrugOrders.add(bahmniDrugOrder);
        }
        return bahmniDrugOrders;
    }
}