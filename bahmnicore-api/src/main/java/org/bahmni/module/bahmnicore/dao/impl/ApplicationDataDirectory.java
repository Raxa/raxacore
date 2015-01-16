package org.bahmni.module.bahmnicore.dao.impl;

import java.io.File;

public interface ApplicationDataDirectory {
    File getFile(String relativePath);
}
