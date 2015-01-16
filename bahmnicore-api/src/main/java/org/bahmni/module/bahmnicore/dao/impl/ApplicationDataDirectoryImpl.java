package org.bahmni.module.bahmnicore.dao.impl;

import java.io.File;
import org.openmrs.util.OpenmrsUtil;

public class ApplicationDataDirectoryImpl implements ApplicationDataDirectory {

    @Override
    public File getFile(String relativePath) {
        return new File(OpenmrsUtil.getApplicationDataDirectory() + relativePath);
    }
}
