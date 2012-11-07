package org.raxa.module.raxacore;

import java.util.List;
import org.openmrs.api.OpenmrsService;
import org.raxa.module.raxacore.db.ImageDAO;
import org.springframework.transaction.annotation.Transactional;

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

@Transactional
public interface ImageService extends OpenmrsService{

    void setImageDAO(ImageDAO dao);
    
    Image saveImage(Image image);
	
	Image getImageByUuid(String uuid);
	
	List<Image> getAllImages();
	
	List<Image> getImagesByName(String name);
	
	Image updateImage(Image image);
	
	void deleteImage(Image image);
	
	List<Image> getImagesByProviderUuid(String providerUuid);

	List<Image> getImagesByPatientUuid(String patientUuid);    
    
	List<Image> getImagesByLocationUuid(String locationUuid);
	
	List<Image> getImagesByTag(String tag);
    
    Image getLatestImageByTag(String tag);
}
