/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */

package org.bahmni.module.bahmnicore.db.hibernate;

import org.hibernate.EmptyInterceptor;
import org.hibernate.type.Type;
import org.joda.time.MutableDateTime;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Date;

/**
 * Prior to MySQL version 5.6 the DATETIME datatype is only precise to the second, and in version 5.6, a column datatype
 * of DATETIME is precise to the second. (To get millisecond precision you'd need to say DATETIME(3).) Thus all the
 * DATETIME fields in all existing OpenMRS installations running on MySQL are precise to the second.
 * <p/>
 * We use java.util.Date in OpenMRS, which has millisecond precision, so when saving an OpenMRS object to the database,
 * date conversion happens. Prior to version 5.6, MySQL used to drop the millisecond component from a DATETIME when
 * saving it. Starting in version 5.6, MySQL <em>rounds</em> a datetime, e.g. if you save a visit with startDatetime of
 * 2014-02-05 14:35:17.641 it will be stored in the database rounded up to the next second: 2014-02-05 14:35:18.
 * <p/>
 * This can have several undesired effects. Take the following code snippet:
 * <code>
 * Visit v = new Visit();
 * // set Patient, VisitType, etc
 * v.setStartDatetime(new Date());
 * return "redirect:patient.page?ptId=" + v.getPatient().getId()
 * </code>
 * In the 50% of cases where v.startDatetime was rounded up to the next second, the redirect takes us to the page for
 * a patient who does not have an "active" visit, though they have a future one that will start in less than a second.
 * <p/>
 * To achieve the MySQL 5.5 behavior while running on version 5.6+, we use a hibernate interceptor to drop the
 * millisecond component of dates before writing them to the database.
 */

//TODO : Temp fix - https://tickets.openmrs.org/browse/TRUNK-4252
@Component
public class DropMillisecondsHibernateInterceptor extends EmptyInterceptor {

    @Override
    public boolean onFlushDirty(Object entity, Serializable id, Object[] currentState, Object[] previousState, String[] propertyNames, Type[] types) {
        return removeMillisecondsFromDateFields(currentState);
    }

    @Override
    public boolean onSave(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {
        return removeMillisecondsFromDateFields(state);
    }

    /**
     * If any item in fieldValues is a Date with non-zero milliseconds, it is replaced with the Date corresponding to
     * the same second, with no milliseconds.
     *
     * @param fieldValues
     * @return whether anything was modified
     */
    private boolean removeMillisecondsFromDateFields(Object[] fieldValues) {
        boolean anyChanges = false;
        for (int i = fieldValues.length - 1; i >= 0; --i) {
            Object candidate = fieldValues[i];
            if (candidate instanceof Date) {
                Date noMilliseconds = removeMilliseconds((Date) candidate);
                if (!noMilliseconds.equals(candidate)) {
                    fieldValues[i] = noMilliseconds;
                    anyChanges = true;
                }
            }
        }
        return anyChanges;
    }

    private Date removeMilliseconds(Date date) {
        MutableDateTime dt = new MutableDateTime(date);
        dt.setMillisOfSecond(0);
        return dt.toDate();
    }

}
