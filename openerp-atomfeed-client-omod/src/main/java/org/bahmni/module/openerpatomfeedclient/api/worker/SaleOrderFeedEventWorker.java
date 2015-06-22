package org.bahmni.module.openerpatomfeedclient.api.worker;

import org.apache.log4j.Logger;
import org.bahmni.module.bahmnicore.model.BahmniFeedDrugOrders;
import org.bahmni.module.bahmnicore.service.BahmniDrugOrderService;
import org.bahmni.module.openerpatomfeedclient.api.OpenERPAtomFeedProperties;
import org.bahmni.module.openerpatomfeedclient.api.domain.SaleOrder;
import org.bahmni.module.openerpatomfeedclient.api.exception.OpenERPFeedException;
import org.bahmni.module.openerpatomfeedclient.api.util.ObjectMapperRepository;
import org.ict4h.atomfeed.client.domain.Event;
import org.ict4h.atomfeed.client.service.EventWorker;

public class SaleOrderFeedEventWorker implements EventWorker{
    public static final String PHARMACY_VISIT = "PHARMACY VISIT";
    private static Logger logger = Logger.getLogger(SaleOrderFeedEventWorker.class);
    private BahmniDrugOrderService bahmniDrugOrderService;
    private OpenERPAtomFeedProperties properties;

    public SaleOrderFeedEventWorker(BahmniDrugOrderService bahmniDrugOrderService, OpenERPAtomFeedProperties properties) {
        this.bahmniDrugOrderService = bahmniDrugOrderService;
        this.properties = properties;
    }

    @Override
    public void process(Event event) {
        String saleOrderContent = event.getContent();
        logger.info("openERPatomfeedclient:Processing : " + saleOrderContent);
        try {
            SaleOrder saleOrder = ObjectMapperRepository.objectMapper.readValue(saleOrderContent, SaleOrder.class);
            if(saleOrder.getExternalId() == null || saleOrder.getExternalId().trim().length() == 0) {
                BahmniFeedDrugOrders uniqueOrders = new BahmniFeedDrugOrders(saleOrder.getSaleOrderItems()).getUniqueOrders();
                bahmniDrugOrderService.add(saleOrder.getCustomerId(), saleOrder.getOrderDate(), uniqueOrders, properties.getSystemUserName(), PHARMACY_VISIT);
            }
        } catch (Exception e) {
            logger.error("openERPatomfeedclient:error processing : " + saleOrderContent + e.getMessage(), e);
            throw new OpenERPFeedException("could not read drug data", e);
        }
    }

    @Override
    public void cleanUp(Event event) {
    }
}
