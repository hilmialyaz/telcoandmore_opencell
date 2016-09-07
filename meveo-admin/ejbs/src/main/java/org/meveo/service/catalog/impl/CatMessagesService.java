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
package org.meveo.service.catalog.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.commons.utils.ReflectionUtils;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.BusinessEntity;
import org.meveo.model.IEntity;
import org.meveo.model.MultilanguageEntity;
import org.meveo.model.billing.CatMessages;
import org.meveo.model.crm.Provider;
import org.meveo.service.base.MultilanguageEntityService;
import org.meveo.service.base.PersistenceService;

/**
 * CatMessagesService service implementation.
 */
@Stateless
public class CatMessagesService extends PersistenceService<CatMessages> {

	private static final String INVALID_CLASS_TYPE = "Invalid class type!";
    
    private Map<String, MultilanguageEntityService<?>> services;
    
    @PostConstruct
    private void init() {

    }
    
    @Inject
	private void initServiceList(@Any Instance<MultilanguageEntityService<?>> entityServices) {
		if (services == null) {
			services = Collections.synchronizedMap(new HashMap<String, MultilanguageEntityService<?>>());
		}
		if (services.isEmpty()) {
			for (MultilanguageEntityService<?> entityService : entityServices) {
				services.put(ReflectionUtils.getCleanClassName(entityService.getEntityClass().getSimpleName()), entityService);
			}
		}
	}

    public String getMessageDescription(BusinessEntity businessEntity, String languageCode,Provider provider) {
        String result = getMessageDescription(businessEntity.getCode(),getEntityClass(businessEntity), languageCode, businessEntity.getDescription(),provider);
        if (StringUtils.isBlank(result)) {
            result = businessEntity.getCode();
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    public String getMessageDescription(String entityCode,String entityClass, String languageCode, String defaultDescription,Provider provider) {
        long startDate = System.currentTimeMillis();
        if (entityCode == null||entityClass==null||languageCode==null) {
            return defaultDescription;
        }
        QueryBuilder qb = new QueryBuilder(CatMessages.class, "c");
        qb.addCriterion("c.entityCode","=", entityCode,true);
        qb.addCriterion("c.entityClass", "=", entityClass, true);
        qb.addCriterion("c.languageCode","=", languageCode, true);
        qb.addCriterionEntity("c.provider", provider);
        List<CatMessages> catMessages = qb.getQuery(getEntityManager()).getResultList();

        String description = (catMessages.size() > 0 && !StringUtils.isBlank(catMessages.get(0).getDescription())) ? catMessages.get(0).getDescription() : defaultDescription;

        log.debug("get message " + entityCode + " description =" + description + ", time=" + (System.currentTimeMillis() - startDate));
        return description;
    }

    public CatMessages getCatMessages(BusinessEntity businessEntity, String languageCode,Provider provider) {

        return getCatMessages(businessEntity.getCode(),getEntityClass(businessEntity), languageCode,provider);
    }

    public CatMessages getCatMessages(String entityCode,String entityClass, String languageCode,Provider provider) {
        return getCatMessages(getEntityManager(), entityCode,entityClass, languageCode,provider);
    }

    @SuppressWarnings("unchecked")
    private CatMessages getCatMessages(EntityManager em, String entityCode,String entityClass, String languageCode,Provider provider) {
        QueryBuilder qb = new QueryBuilder(CatMessages.class, "c");
        qb.addCriterion("c.entityCode","=", entityCode, true);
        qb.addCriterion("c.entityClass", "=", entityClass, true);
        qb.addCriterion("c.languageCode","=", languageCode, true);
        qb.addCriterionEntity("c.provider", provider);
        List<CatMessages> cats = (List<CatMessages>) qb.getQuery(em).getResultList();
        return cats != null && cats.size() > 0 ? cats.get(0) : null;
    }

    @SuppressWarnings("unchecked")
    public List<CatMessages> getCatMessagesList(String entityClass,String entityCode,Provider provider) {
        log.debug("getCatMessagesList entityClass={},entityCode={} ", entityClass,entityCode);
        if (StringUtils.isBlank(entityCode)||StringUtils.isBlank(entityClass)) {
            return new ArrayList<CatMessages>();
        }
        QueryBuilder qb = new QueryBuilder(CatMessages.class, "c");
        qb.addCriterion("c.entityCode", "=", entityCode, true);
        qb.addCriterion("c.entityClass", "=", entityClass, true);
        qb.addCriterionEntity("c.provider",provider);
        List<CatMessages> cats = (List<CatMessages>) qb.getQuery(getEntityManager()).getResultList();
        return cats;
    }

    /**
     * Get all messages of a given class in a given language
     * 
     * @param clazz Class to get messages for
     * @param languageCode Language to get messages in
     * @return A list of messages
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public List<CatMessages> getCatMessagesList(Class clazz, String languageCode,Provider provider) {
        QueryBuilder qb = new QueryBuilder(CatMessages.class, "c");
        qb.addCriterion("c.entityClass", "=",getEntityClass(clazz), true);
        qb.addCriterion("c.languageCode","=", languageCode, true);
        qb.addCriterionEntity("c.provider", provider);
        List<CatMessages> cats = (List<CatMessages>) qb.getQuery(getEntityManager()).getResultList();
        return cats;
    }

    public void batchRemove(String entityClass, String entityCode,Provider provider) {
        String strQuery = "DELETE FROM " + CatMessages.class.getSimpleName() + " c WHERE c.entityCode=:entityCode and c.entityClass=:entityClass and c.provider=:provider";

        try {
            getEntityManager().createQuery(strQuery).setParameter("entityCode",entityCode).setParameter("entityClass",entityClass).setParameter("provider", provider).executeUpdate();
        } catch (Exception e) {
            log.error("failed to batch remove", e);
        }
    }

    /**
     * Get a message code prefix for a given class
     * 
     * @param clazz Class
     * @return A message code in a format "className_"
     */
    public String getEntityClass(IEntity entity) {
        return ReflectionUtils.getCleanClassName(entity.getClass().getSimpleName());
    }
    @SuppressWarnings("rawtypes")
    public String getEntityClass(Class clazz) {
        return ReflectionUtils.getCleanClassName(clazz.getSimpleName());
    }

    public CatMessages findByCodeClassAndLanguage(String entityCode,String entityClass, String languageCode,Provider provider) {
        QueryBuilder qb = new QueryBuilder(CatMessages.class, "c");
        qb.addCriterion("c.entityCode","=", entityCode, true);
        qb.addCriterion("c.entityClass", "=", entityClass, true);
        qb.addCriterion("c.languageCode","=", languageCode, true);
        qb.addCriterionEntity("c.provider",provider);
        try {
            return (CatMessages) qb.getQuery(getEntityManager()).getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }
    
    @SuppressWarnings("unchecked")
	public List<CatMessages> findByCodeAndClass(String entityCode,String entityClass,Provider provider) {
        QueryBuilder qb = new QueryBuilder(CatMessages.class, "c");
        qb.addCriterion("c.entityCode","=", entityCode, true);
        qb.addCriterion("c.entityClass", "=", entityClass, true);
        qb.addCriterionEntity("c.provider", provider);
        try {
            return (List<CatMessages>) qb.getQuery(getEntityManager()).getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public List<CatMessages> list(PaginationConfiguration config) {
        List<CatMessages> catMessages = super.list(config);
        for (CatMessages catMsg : catMessages) {
            BusinessEntity entity = getBusinessEntity(catMsg,getCurrentProvider());
            if (entity != null) {
                //catMsg.setEntityCode(entity.getCode());
                catMsg.setEntityDescription(entity.getDescription());
            }
        }
        return catMessages;
    }

	private BusinessEntity getBusinessEntity(CatMessages catMessages,Provider provider) {
		BusinessEntity entity = null;
		if (catMessages != null) {
			String entityCode = catMessages.getEntityCode();
			String entityClass=catMessages.getEntityClass();
			try {
				MultilanguageEntityService<?> service = getMultilanguageEntityService(entityClass);
				if (service != null) {
					entity = service.findByCode(entityCode, provider);
				}
			} catch (NumberFormatException e) {
				log.warn("Failed to parse id. Returning null entity. {}",e.getMessage());
			} catch (BusinessException e) {
				e.printStackTrace();
				log.warn("Failed to retrieve entity. Returning null. {}",e.getMessage());
			}
		}
		return entity;
	}
     
	public MultilanguageEntityService<?> getMultilanguageEntityService(String entityClassName)
			throws BusinessException {
		MultilanguageEntityService<?> service = null;
		log.debug("entityClassName {}",entityClassName);
		if (entityClassName != null) {
			service = this.services.get(entityClassName);
		}
		if (service == null) {
			throw new BusinessException(INVALID_CLASS_TYPE);
		}
		return service;
	}
	
	public BusinessEntity findBusinessEntityByCodeAndClass(String entityCode,String entityClass,Provider provider) {
		if (StringUtils.isBlank(entityCode)) {
			return null;
		}
		Class<?> entityClazz = ReflectionUtils.getClassBySimpleNameAndAnnotation(entityClass, MultilanguageEntity.class);
		QueryBuilder qb=new QueryBuilder(entityClazz,"c");
		qb.addCriterion("c.code", "=", entityCode, true);
		qb.addCriterionEntity("c.provider", provider);
		try {
			return (BusinessEntity) qb.getQuery(getEntityManager()).getSingleResult();
		} catch (Exception e) {
			log.warn("Invalid Entity code {},class {}.  Will return null entity.",entityCode,entityClass, e);
		}
		return null;
	}
}