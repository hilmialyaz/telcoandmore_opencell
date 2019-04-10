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
package org.meveo.jpa;

import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnit;

import org.infinispan.Cache;
import org.infinispan.context.Flag;
import org.meveo.commons.utils.ParamBean;
import org.meveo.security.keycloak.CurrentUserProvider;
import org.slf4j.Logger;

/**
 * Handles Entity manager instantiation. Based on https://www.tomas-dvorak.cz/posts/jpa-multitenancy/
 * 
 * @author Andrius Karpavicius
 */
@Stateless
public class EntityManagerProvider {

    @PersistenceUnit(unitName = "MeveoAdmin")
    private EntityManagerFactory emf;

    @PersistenceContext(unitName = "MeveoAdmin")
    private EntityManager emfForJobs;

    @Inject
    private CurrentUserProvider currentUserProvider;

    @Inject
    private Logger log;

    @Resource(lookup = "java:jboss/infinispan/cache/opencell/opencell-multiTenant-cache")
    private Cache<String, EntityManagerFactory> entityManagerFactories;

    private static boolean isMultiTenancyEnabled = ParamBean.isMultitenancyEnabled();

    /**
     * Instantiates an Entity manager for use CDI injection. Will consider a tenant that currently connected user belongs to.
     * 
     * @return Entity manager instance
     */
    @Produces
    @RequestScoped
    @MeveoJpa
    public EntityManagerWrapper getEntityManager() {
        String providerCode = currentUserProvider.getCurrentUserProviderCode();

        log.trace("Produce EM for provider {}", providerCode);

        if (providerCode == null || !isMultiTenancyEnabled) {

            // Create an container managed persistence context main provider, for API and JOBs
            if (FacesContext.getCurrentInstance() == null) {
                return new EntityManagerWrapper(emfForJobs, false);

                // Create an application managed persistence context main provider, for GUI
            } else {
                final EntityManager em = emf.createEntityManager();
                EntityManager emProxy = (EntityManager) Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class<?>[] { EntityManager.class }, (proxy, method, args) -> {
                    em.joinTransaction();
                    return method.invoke(em, args);
                });
                return new EntityManagerWrapper(emProxy, true);
            }
        }

        // Create an application managed persistence context for provider
        final EntityManager em = createEntityManager(providerCode);
        EntityManager emProxy = (EntityManager) Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class<?>[] { EntityManager.class }, (proxy, method, args) -> {
            em.joinTransaction();
            return method.invoke(em, args);
        });
        return new EntityManagerWrapper(emProxy, true);
    }

    /**
     * Close Entity manager for GUI
     * 
     * @param entityManagerWrapper Entity manager wrapper to dispose
     */
    public void disposeEMWrapper(@Disposes @MeveoJpa EntityManagerWrapper entityManagerWrapper) {
        // log.error("AKK will try dispose entityManagerWrapper");
        entityManagerWrapper.dispose();
    }

    /**
     * Get entity manager for a given provider. Entity manager produced from a entity manager factory will join a transaction.
     * 
     * @param providerCode Provider code
     * @return Entity manager instance
     */
    public EntityManager getEntityManager(String providerCode) {

        log.trace("Get EM for provider {}", providerCode);

        boolean isMultiTenancyEnabled = ParamBean.isMultitenancyEnabled();

        if (providerCode == null || !isMultiTenancyEnabled) {
            return emfForJobs;
        }

        EntityManager currentEntityManager = createEntityManager(providerCode);

        final EntityManager currentEntityManagerFinal = currentEntityManager;
        return (EntityManager) Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class<?>[] { EntityManager.class }, (proxy, method, args) -> {
            currentEntityManagerFinal.joinTransaction();
            return method.invoke(currentEntityManagerFinal, args);
        });
    }

    /**
     * Get entity manager for use in Bean managed transactions. Entity manager produced from a entity manager factory will NOT join a transaction. Will consider a tenant that
     * currently connected user belongs to.
     * 
     * @return Get entity manager for a given provider. Entity manager produced from a entity manager factory will join a transaction
     */
    public EntityManager getEntityManagerWoutJoinedTransactions() {
        String providerCode = currentUserProvider.getCurrentUserProviderCode();

        log.trace("Produce EM for provider {}", providerCode);

        if (providerCode == null || !isMultiTenancyEnabled) {

            // Create an container managed persistence context main provider, for API and JOBs
            if (FacesContext.getCurrentInstance() == null) {
                return emfForJobs;

                // Create an application managed persistence context main provider, for GUI
            } else {
                return emf.createEntityManager();
            }
        }

        // Create an application managed persistence context for provider
        return createEntityManager(providerCode);

    }

    private EntityManager createEntityManager(String providerCode) {
        // log.trace("Create EM for provider {}", providerCode);
        try {
            return entityManagerFactories.get(providerCode).createEntityManager();
        } catch (NullPointerException e) {
            log.error("Failed to create EM for provider {}. EMFs created for {}", providerCode, entityManagerFactories.keySet().toArray());
        }
        return null;
    }

    /**
     * Create a new Entity manager factory for a given tenant
     * 
     * @param providerCode Provider/tenant code
     */
    public void registerEntityManagerFactory(String providerCode) {
        log.trace("Create EMF for provider {}", providerCode);

        if (entityManagerFactories.containsKey(providerCode)) {
            return;
        }

        Map<String, String> props = new TreeMap<>();
        props.put("hibernate.default_schema", convertToSchemaName(providerCode));

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("MeveoAdminMultiTenant", props);

        entityManagerFactories.getAdvancedCache().withFlags(Flag.IGNORE_RETURN_VALUES).put(providerCode, emf);

        log.debug("Created EMF for provider {}", providerCode);
    }

    /**
     * Remove Entity manager factory for a given tenant
     * 
     * @param providerCode Provider/tenant code
     */
    public void unregisterEntityManagerFactory(String providerCode) {

        log.trace("Remove EMF for provider {}", providerCode);

        entityManagerFactories.getAdvancedCache().withFlags(Flag.IGNORE_RETURN_VALUES).remove(providerCode);

        log.debug("Removed EMF for provider {}", providerCode);
    }

    /**
     * Convert/normalize provider code to a valid schema name: replace spaces with _ and lowercase it.
     * 
     * @param providerCode Provider code
     * @return Schema name corresponding to a provider code
     */
    private String convertToSchemaName(String providerCode) {
        return providerCode.replace(' ', '_').toLowerCase();
    }
}