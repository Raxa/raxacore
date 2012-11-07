package org.raxa.module.raxacore.db;

/**
 * Copyright 2012, Raxa
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
import java.util.List;
import org.openmrs.api.db.DAOException;
import org.raxa.module.raxacore.Image;

/**
 * Interface for accessing raxacore_image
 */
public interface ImageDAO {
	
	/**
	 * Saves a Image
	 * 
	 * @param Image to be saved
	 * @throws DAOException
	 * @should save a image
	 */
	public Image saveImage(Image image) throws DAOException;
	
	/**
	 * Purge a Image from database
	 * @param Image object to be purged
	 * @should delete a image
	 */
	public void deleteImage(Image image) throws DAOException;
	
	/**
	 * Get image by internal identifier
	 * 
	 * @param imageID image id
	 * @return image with given internal identifier
	 * @throws DAOException
	 * @should get a image
	 */
	public Image getImage(Integer imageID) throws DAOException;
	
	/**
	 * Find {@link Image} matching a uuid
	 * 
	 * @param uuid
	 * @return {@link Image}
	 * @should get a image by uuid
	 */
	public Image getImageByUuid(String uuid);
	
	/**
	 * Find {@link Image} matching a name
	 * 
	 * @param name
	 * @return {@link Image}
	 * @should get a image by name
	 */
	public List<Image> getImagesByName(String name);
	
	/**
	 * Find {@link Image} matching a tag
	 * 
	 * @param tag
	 * @return {@link Image}
	 * @should get images by tag
	 */
	public List<Image> getImagesByTag(String tag);

	/**
	 * Find {@link Image} updated latest matching a tag
	 * 
	 * @param tag
	 * @return {@link Image}
	 * @should get images by tag
	 */
	public Image getLatestImageByTag(String tag);

    /**
	 * Find {@link Image} matching providerId
	 * 
	 * @param providerId
	 * @return List of Images
	 * @should get a image list by providerId
	 */
	public List<Image> getImagesByProviderId(Integer providerSentId);
	
	/**
	 * Find {@link Image} matching patientId
	 * 
	 * @param patientId
	 * @return List of Images
	 * @should get a image list by patientId
	 */
	public List<Image> getImagesByPatientId(Integer patientId);
	
	/**
	 * Update Image
	 * @return {@link Image}
	 * @should update a Image
	 */
	Image updateImage(Image image) throws DAOException;
	
	/**
	 *Get all Image
	 *@return List of Images
	 */
	public List<Image> getAllImages() throws DAOException;
	
	/**
	 * Void a Image in the database
	 * @param Image object to be purged
	 * @param String reason for voiding Image
	 * @should void a image
	 */
	public Image voidImage(Image image, String reason);
	
	public List<Image> getImagesByLocationId(Integer locationId);
}
