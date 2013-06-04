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
package org.meveo.service.billing.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;

import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.admin.Seller;
import org.meveo.model.admin.User;
import org.meveo.model.billing.InstanceStatusEnum;
import org.meveo.model.billing.OneShotChargeInstance;
import org.meveo.model.billing.ServiceInstance;
import org.meveo.model.billing.Subscription;
import org.meveo.model.catalog.OneShotChargeTemplate;
import org.meveo.model.catalog.OneShotChargeTemplateTypeEnum;
import org.meveo.service.base.BusinessService;

@Stateless
@LocalBean
public class OneShotChargeInstanceService extends BusinessService<OneShotChargeInstance> {

	@EJB
	private WalletOperationService chargeApplicationService;

	public OneShotChargeInstance findByCodeAndSubsription(String code, Long subscriptionId) {
		OneShotChargeInstance oneShotChargeInstance = null;
		try {
			log.debug("start of find {} by code (code={}, subscriptionId={}) ..", new Object[] {
					"OneShotChargeInstance", code, subscriptionId });
			QueryBuilder qb = new QueryBuilder(OneShotChargeInstance.class, "c");
			qb.addCriterion("c.code", "=", code, true);
			qb.addCriterion("c.subscription.id", "=", subscriptionId, true);
			oneShotChargeInstance = (OneShotChargeInstance) qb.getQuery(getEntityManager())
					.getSingleResult();
			log.debug("end of find {} by code (code={}, subscriptionId={}). Result found={}.",
					new Object[] { "OneShotChargeInstance", code, subscriptionId,
							oneShotChargeInstance != null });
		} catch (NoResultException nre) {
			log.debug("findByCodeAndSubsription : aucune charge ponctuelle n'a ete trouvee");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return oneShotChargeInstance;
	}

	public OneShotChargeInstance oneShotChargeInstanciation(Subscription subscription,
			ServiceInstance serviceInstance, OneShotChargeTemplate chargeTemplate, Date effetDate,
			BigDecimal amoutWithoutTax, BigDecimal amoutWithoutTx2, Integer quantity,
			Seller seller, User creator) throws BusinessException {

		if (quantity == null) {
			quantity = 1;
		}
		OneShotChargeInstance oneShotChargeInstance = new OneShotChargeInstance(
				chargeTemplate.getCode(), chargeTemplate.getDescription(), effetDate,
				amoutWithoutTax, amoutWithoutTx2, subscription, chargeTemplate, seller);
		oneShotChargeInstance.setStatus(InstanceStatusEnum.INACTIVE);
		if (chargeTemplate.getOneShotChargeTemplateType() == OneShotChargeTemplateTypeEnum.TERMINATION) {
			oneShotChargeInstance.setTerminationServiceInstance(serviceInstance);
		} else {
			oneShotChargeInstance.setSubscriptionServiceInstance(serviceInstance);
		}
		oneShotChargeInstance.setChargeDate(serviceInstance.getSubscriptionDate());

		create(oneShotChargeInstance, creator, chargeTemplate.getProvider());
		return oneShotChargeInstance;
	}

	public Long oneShotChargeApplication(Subscription subscription,
			OneShotChargeTemplate chargetemplate, Date effetDate, BigDecimal amoutWithoutTax,
			BigDecimal amoutWithoutTx2, Integer quantity, String criteria1, String criteria2,
			String criteria3, Seller seller, User creator) throws BusinessException {

		if (quantity == null) {
			quantity = 1;
		}
		/*
		 * OneShotChargeInstance oneshotCharge =
		 * findByCodeAndSubsription(chargeCode, subscription.getId()); if
		 * (oneshotCharge != null) { throw new BusinessException("this one shot
		 * charge instance already exists for this subscription. code=" +
		 * oneshotCharge.getCode() + ",subscriptionCode=" +
		 * subscription.getCode()); } OneShotChargeTemplate chargetemplate =
		 * oneShotChargeTemplateService.findByCode(chargeCode);
		 */

		OneShotChargeInstance oneShotChargeInstance = new OneShotChargeInstance(
				chargetemplate.getCode(), chargetemplate.getDescription(), effetDate,
				amoutWithoutTax, amoutWithoutTx2, subscription, chargetemplate, seller);
		oneShotChargeInstance.setCriteria1(criteria1);
		oneShotChargeInstance.setCriteria2(criteria2);
		oneShotChargeInstance.setCriteria3(criteria3);

		create(oneShotChargeInstance, creator, chargetemplate.getProvider());

		chargeApplicationService.oneShotWalletOperation(subscription, oneShotChargeInstance,
				quantity, effetDate, creator);

		return oneShotChargeInstance.getId();
	}

	public void oneShotChargeApplication(Subscription subscription,
			OneShotChargeInstance oneShotChargeInstance, Date effetDate, Integer quantity,
			User creator) throws BusinessException {

		chargeApplicationService.oneShotWalletOperation(subscription, oneShotChargeInstance,
				quantity, effetDate, creator);

	}

	@SuppressWarnings("unchecked")
	public List<OneShotChargeInstance> findOneShotChargeInstancesBySubscriptionId(
			Long subscriptionId) {
		QueryBuilder qb = new QueryBuilder(OneShotChargeInstance.class, "c");
		qb.addCriterion("c.subscription.id", "=", subscriptionId, true);
		return qb.getQuery(getEntityManager()).getResultList();
	}

}
