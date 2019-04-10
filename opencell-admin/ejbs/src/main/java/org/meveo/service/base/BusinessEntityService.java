/*
 * (C) Copyright 2015-2016 Opencell SAS (http://opencellsoft.com/) and contributors.
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * This program is not suitable for any direct or indirect application in MILITARY industry
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.service.base;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.meveo.model.BusinessEntity;
import org.meveo.model.customEntities.CustomEntityInstance;
import org.meveo.model.generic.wf.WorkflowInstance;

@Stateless
public class BusinessEntityService extends BusinessService<BusinessEntity> {

    public void setEntityClass(Class<BusinessEntity> clazz) {
        this.entityClass = (Class<BusinessEntity>) clazz;
    }

    public BusinessEntity findByWorkflowInstance(WorkflowInstance workflowInstance) {

        String code = workflowInstance.getEntityInstanceCode();
        String stringQuery = "select be from " + entityClass.getSimpleName() + " be where upper(code)=:code";

        if (workflowInstance.getTargetEntityClass().equals(CustomEntityInstance.class.getName())) {
            stringQuery += " and cet_code=:cet_code";
        }

        Query query = getEntityManager().createQuery(stringQuery, entityClass).setParameter("code", code.toUpperCase()).setMaxResults(1);

        if (workflowInstance.getTargetEntityClass().equals(CustomEntityInstance.class.getName())) {
            query.setParameter("cet_code", workflowInstance.getTargetCetCode());
        }

        try {
            return (BusinessEntity) query.getSingleResult();
        } catch (NoResultException e) {
            log.debug("No {} of code {} found", getEntityClass().getSimpleName(), code);
            return null;
        }
    }
}
