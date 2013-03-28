/*
* (C) Copyright 2009-2013 Manaty SARL (http://manaty.net/) and contributors.
*
* Licensed under the GNU Public Licence, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.gnu.org/licenses/gpl-2.0.txt
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.meveo.model.catalog;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.meveo.model.ProviderBusinessEntity;

/**
 * @author R.AITYAAZZA
 * 
 */
@Entity
@Table(name = "CAT_OFFER_TEMPLATE")
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "CAT_OFFER_TEMPLATE_SEQ")
public class OfferTemplate extends ProviderBusinessEntity {

    private static final long serialVersionUID = 1L;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "CAT_OFFER_SERV_TEMPLATES", joinColumns = @JoinColumn(name = "OFFER_TEMPLATE_ID"), inverseJoinColumns = @JoinColumn(name = "SERVICE_TEMPLATE_ID"))
    private List<ServiceTemplate> serviceTemplates;

    public List<ServiceTemplate> getServiceTemplates() {
        return serviceTemplates;
    }

    public void setServiceTemplates(List<ServiceTemplate> serviceTemplates) {
        this.serviceTemplates = serviceTemplates;
    }

}
