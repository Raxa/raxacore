package org.raxa.module.raxacore.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.util.OpenmrsUtil;
import org.raxa.module.raxacore.Image;
import org.raxa.module.raxacore.ImageService;
import org.raxa.module.raxacore.db.ImageDAO;

/**
 * Copyright 2012, Raxa
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */


public class ImageServiceImpl implements ImageService{

    private ImageDAO dao;
	
	private Log log = LogFactory.getLog(this.getClass());

    private static final String IMGDIR = "patientimages";
	
	/**
	 * @see org.raxa.module.raxacore.RaxaAlertService#setRaxaAlertDAO
	 */
	@Override
	public void setImageDAO(ImageDAO dao) {
		this.dao = dao;
	}

    @Override
    public Image saveImage(Image image) {
        File imgDir = new File(OpenmrsUtil.getApplicationDataDirectory() + System.getProperty("file.separator") + IMGDIR);
        File img = new File(getPath(image));
        try {
            if (!imgDir.exists()) {
                FileUtils.forceMkdir(imgDir);
            }
        } catch (IOException e) {
            log.error(e);
        }
        try {
            FileOutputStream fos = new FileOutputStream(imgDir + System.getProperty("file.separator") + image.getFileName());
            fos.write(image.getImageData());
            fos.close();
        } catch (Exception e) {
            log.error(e);
        }
        return dao.saveImage(image);
    }

    @Override
    public Image getImageByUuid(String uuid) {
        Image image = dao.getImageByUuid(uuid);
        File f = new File(getPath(image));
        try{
            FileInputStream fis = new FileInputStream(f);
            byte[] imageData = IOUtils.toByteArray(fis);
            fis.read(imageData);
            image.setImageData(imageData);
            fis.close();
            return image;
        }catch(IOException ex){
            log.error("Reading image directory failed with: "+ex.getMessage());
        }
        return new Image();
    }

    @Override
    public List<Image> getAllImages() {
        return dao.getAllImages();
    }

    @Override
    public List<Image> getImagesByName(String name) {
        return dao.getImagesByName(name);
    }

    @Override
    public Image updateImage(Image image) {
        return dao.updateImage(image);
    }

    @Override
    public void deleteImage(Image image) {
        dao.deleteImage(image);
    }

    @Override
    public List<Image> getImagesByProviderUuid(String providerUuid) {
        return dao.getImagesByProviderId(Context.getProviderService().getProviderByUuid(providerUuid).getId());
    }

    @Override
    public List<Image> getImagesByPatientUuid(String patientUuid) {
        return dao.getImagesByPatientId(Context.getPatientService().getPatientByUuid(patientUuid).getId());
    }

    @Override
    public List<Image> getImagesByLocationUuid(String locationUuid) {
        return dao.getImagesByLocationId(Context.getLocationService().getLocationByUuid(locationUuid).getId());
    }

    @Override
    public List<Image> getImagesByTag(String tag) {
        return dao.getImagesByTag(tag);
    }

    @Override
    public Image getLatestImageByTag(String tag) {
        Image image = dao.getLatestImageByTag(tag);
        return getImageByUuid(image.getUuid());
    }

    @Override
    public void onStartup() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void onShutdown() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getPath(Image p) {
        return OpenmrsUtil.getApplicationDataDirectory() + System.getProperty("file.separator") + 
                IMGDIR + System.getProperty("file.separator") + p.getFileName();
    }


}
