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
package org.meveo.admin.action.catalog;

import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.admin.action.BaseBean;
import org.meveo.model.catalog.ProductOffering;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.catalog.impl.ProductOfferingService;
import org.omnifaces.cdi.ViewScoped;

@Named
@ViewScoped
public class ProductOfferingBean extends BaseBean<ProductOffering> {

    private static final long serialVersionUID = -5781632820061377157L;

    @Inject
    private ProductOfferingService productOfferingService;

    /**
     * Constructor. Invokes super constructor and provides class type of this bean for {@link BaseBean}.
     */
    public ProductOfferingBean() {
        super(ProductOffering.class);
    }

    /**
     * @see org.meveo.admin.action.BaseBean#getPersistenceService()
     */
    @Override
    protected IPersistenceService<ProductOffering> getPersistenceService() {
        return productOfferingService;
    }

    @Override
    protected String getDefaultSort() {
        return "code";
    }
}