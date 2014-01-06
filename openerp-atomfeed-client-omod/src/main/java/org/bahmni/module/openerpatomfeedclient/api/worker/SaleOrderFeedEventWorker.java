package org.bahmni.module.openerpatomfeedclient.api.worker;

import org.apache.log4j.Logger;
import org.bahmni.module.bahmnicore.service.BahmniDrugOrderService;
import org.bahmni.module.openerpatomfeedclient.api.domain.SaleOrder;
import org.bahmni.module.openerpatomfeedclient.api.exception.OpenERPFeedException;
import org.bahmni.module.openerpatomfeedclient.api.util.ObjectMapperRepository;
import org.ict4h.atomfeed.client.domain.Event;
import org.ict4h.atomfeed.client.service.EventWorker;

import java.io.IOException;

public class SaleOrderFeedEventWorker implements EventWorker{
    private static Logger logger = Logger.getLogger(SaleOrderFeedEventWorker.class);
    private BahmniDrugOrderService bahmniDrugOrderService;

    public SaleOrderFeedEventWorker(BahmniDrugOrderService bahmniDrugOrderService) {
        this.bahmniDrugOrderService = bahmniDrugOrderService;
    }

    @Override
    public void process(Event event) {
        String saleOrderContent = event.getContent();
        logger.info("openerpatomfeedclient:Processing : " + saleOrderContent);
        try {
            SaleOrder saleOrder = ObjectMapperRepository.objectMapper.readValue(saleOrderContent, SaleOrder.class);
            if(saleOrder.getExternalId() == null) {
                bahmniDrugOrderService.add(saleOrder.getCustomerId(), saleOrder.getOrderDate(), saleOrder.getSaleOrderItems());
            }
        } catch (IOException e) {
            logger.error("openelisatomfeedclient:error processing : " + saleOrderContent + e.getMessage(), e);
            throw new OpenERPFeedException("could not read lab result data", e);
        }
    }

    @Override
    public void cleanUp(Event event) {
    }
}
