package org.raxa.module.raxacore.db.hibernate;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.openmrs.api.context.Context;
import org.openmrs.api.db.DAOException;
import org.raxa.module.raxacore.Image;
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

public class HibernateImageDAO implements ImageDAO{

	/**
	 * Hibernate session factory
	 */
	private SessionFactory sessionFactory;
	
	/**
	 * Set session factory
	 *
	 * @param sessionFactory
	 */
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

    @Override
    public Image saveImage(Image image) throws DAOException {
		sessionFactory.getCurrentSession().saveOrUpdate(image);
        return image;
    }

    @Override
    public void deleteImage(Image image) throws DAOException {
		sessionFactory.getCurrentSession().delete(image);
    }

    @Override
    public Image getImage(Integer imageID) throws DAOException {
		return (Image) sessionFactory.getCurrentSession().get(Image.class, imageID);
    }

    @Override
    public List<Image> getImagesByPatientId(Integer patientId) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Image.class);
		criteria.add(Restrictions.eq("patientId", patientId));
		List<Image> images = new ArrayList<Image>();
		images.addAll(criteria.list());
		return images;
    }

    @Override
    public Image getImageByUuid(String uuid) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Image.class);
		criteria.add(Restrictions.eq("uuid", uuid));
		return (Image) criteria.uniqueResult();
    }

    @Override
    public List<Image> getImagesByName(String name) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Image.class);
		criteria.add(Restrictions.like("name", name, MatchMode.ANYWHERE));
		List<Image> images = new ArrayList<Image>();
		images.addAll(criteria.list());
		return images;
    }

    @Override
    public List<Image> getImagesByTag(String tag) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Image.class);
		criteria.add(Restrictions.like("tag", tag, MatchMode.ANYWHERE));
		List<Image> images = new ArrayList<Image>();
		images.addAll(criteria.list());
		return images;
    }

    @Override
    public Image getLatestImageByTag(String tag) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Image.class);
		criteria.add(Restrictions.like("tag", tag, MatchMode.ANYWHERE));
        criteria.addOrder(Order.asc("dateCreated"));
        List<Image> images = new ArrayList<Image>();
		images.addAll(criteria.list());
        if(images.size()>0){
    		return images.get(images.size()-1);
        }
        else{
            return null;
        }
    }

    @Override
    public List<Image> getImagesByProviderId(Integer providerId) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Image.class);
		criteria.add(Restrictions.eq("providerId", providerId));
		List<Image> images = new ArrayList<Image>();
		images.addAll(criteria.list());
		return images;
    }

    @Override
    public Image updateImage(Image image) throws DAOException {
		sessionFactory.getCurrentSession().update(image);
		return image;
    }

    @Override
    public List<Image> getAllImages() throws DAOException {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Image.class);
		List<Image> images = new ArrayList<Image>();
		images.addAll(criteria.list());
		return images;
    }

    @Override
    public Image voidImage(Image image, String reason) {
		if (reason == null) {
			throw new IllegalArgumentException("The argument 'reason' is required and so cannot be null");
		}
		
		image.setVoided(true);
		image.setVoidedBy(Context.getAuthenticatedUser());
		image.setDateVoided(new Date());
		image.setVoidReason(reason);
		saveImage(image);
		return image;
    }

    @Override
    public List<Image> getImagesByLocationId(Integer locationId) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Image.class);
		criteria.add(Restrictions.eq("locationId", locationId));
		List<Image> images = new ArrayList<Image>();
		images.addAll(criteria.list());
		return images;
    }

}
