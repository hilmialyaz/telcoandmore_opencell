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
package org.meveo.admin.action.payments;

import java.util.List;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.admin.action.BaseBean;
import org.meveo.admin.action.CustomFieldBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.web.interceptor.ActionMethod;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.InvoiceType;
import org.meveo.model.payments.PaymentMethodEnum;
import org.meveo.model.payments.PaymentScheduleInstance;
import org.meveo.model.payments.PaymentScheduleInstanceItem;
import org.meveo.model.payments.PaymentScheduleTemplate;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.payments.impl.PaymentScheduleTemplateService;
import org.primefaces.model.LazyDataModel;


/**
 * Standard backing bean for {@link PaymentScheduleTemplate} (extends {@link BaseBean} that provides almost all common methods to handle entities filtering/sorting in datatable, their
 * create, edit, view, delete operations). It works with Manaty custom JSF components.
 */
@Named
@ViewScoped
public class PaymentScheduleTemplateBean extends CustomFieldBean<PaymentScheduleTemplate> {
    private static final long serialVersionUID = 1L;
    /**
     * Injected @{link PaymentScheduleTemplate} service. Extends {@link PersistenceService}.
     */
    @Inject
    private PaymentScheduleTemplateService paymentScheduleTemplateService;

    /**
     * Constructor. Invokes super constructor and provides class type of this bean for {@link BaseBean}.
     */
    public PaymentScheduleTemplateBean() {
        super(PaymentScheduleTemplate.class);
    }

    /**
     * Factory method for entity to edit. If objectId param set load that entity from database, otherwise create new.
     * @return payment gateway.
     * 
     */
    @Override
    public PaymentScheduleTemplate initEntity() {
        super.initEntity();       
        return entity;
    }

    @Override
    public void search() {
        getFilters();
        if (!filters.containsKey("disabled")) {
            filters.put("disabled", false);
        }
        super.search();
    }

    @Override
    @ActionMethod
    public String saveOrUpdate(boolean killConversation) throws BusinessException {
        String outcome = super.saveOrUpdate(killConversation);

        if (outcome != null) {
            return getBackViewSave();
        }
        return null;
    }

    /**
     * @see org.meveo.admin.action.BaseBean#getPersistenceService()
     */
    @Override
    protected IPersistenceService<PaymentScheduleTemplate> getPersistenceService() {
        return paymentScheduleTemplateService;
    }

    @Override
    protected String getDefaultSort() {
        return "code";
    }

    public List<PaymentMethodEnum> getAllowedPaymentMethods() {
        return appProvider.getPaymentMethods();
    }
}
