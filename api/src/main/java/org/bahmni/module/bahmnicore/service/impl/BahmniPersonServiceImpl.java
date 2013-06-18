package org.bahmni.module.bahmnicore.service.impl;

import org.bahmni.module.bahmnicore.dao.PersonAttributeTypeDao;
import org.bahmni.module.bahmnicore.model.BahmniPersonAttributeType;
import org.bahmni.module.bahmnicore.service.BahmniPersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BahmniPersonServiceImpl implements BahmniPersonService {


    private PersonAttributeTypeDao personAttributeTypeDao;

    @Autowired
    public BahmniPersonServiceImpl(PersonAttributeTypeDao personAttributeTypeDao) {
        this.personAttributeTypeDao = personAttributeTypeDao;
    }

    @Override
    public List<BahmniPersonAttributeType> getAllPatientAttributeTypes() {
        return null;
//        return personAttributeTypeDao.getAll();
    }
}
