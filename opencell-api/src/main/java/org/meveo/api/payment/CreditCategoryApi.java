package org.meveo.api.payment;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseApi;
import org.meveo.api.dto.account.CreditCategoryDto;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.payments.CreditCategory;
import org.meveo.service.payments.impl.CreditCategoryService;

/**
 * The CRUD Api for CreditCategory Entity.
 *
 * @author Edward P. Legaspi
 * @author Abdellatif BARI
 * @since 22 Aug 2017
 * @lastModifiedVersion 7.0
 */
@Stateless
public class CreditCategoryApi extends BaseApi {

	@Inject
	private CreditCategoryService creditCategoryService;

	private static CreditCategory dtoToEntity(CreditCategoryDto dto, CreditCategory entity) {
		CreditCategory e = entity;
		if (entity == null) {
			e = new CreditCategory();
			e.setCode(dto.getCode());
		}
		e.setDescription(dto.getDescription());

		return e;
	}

	private static CreditCategoryDto entityToDto(CreditCategory e) {
		CreditCategoryDto dto = new CreditCategoryDto();
		dto.setCode(e.getCode());
		dto.setDescription(e.getDescription());

		return dto;
	}

	public CreditCategory create(CreditCategoryDto postData) throws MeveoApiException, BusinessException {
		CreditCategory creditCategory = dtoToEntity(postData, null);
		creditCategoryService.create(creditCategory);

		handleMissingParameters();
		return creditCategory;
	}

	public CreditCategory update(CreditCategoryDto postData) throws MeveoApiException, BusinessException {
		if (StringUtils.isBlank(postData.getCode())) {
			missingParameters.add("code");
		}

		CreditCategory creditCategory = creditCategoryService.findByCode(postData.getCode());
		if (creditCategory == null) {
			throw new EntityDoesNotExistsException(CreditCategory.class, postData.getCode());
		}

		creditCategory = dtoToEntity(postData, creditCategory);
		creditCategory = creditCategoryService.update(creditCategory);

		handleMissingParameters();
		return creditCategory;
	}

	public CreditCategory createOrUpdate(CreditCategoryDto postData) throws MeveoApiException, BusinessException {
		CreditCategory creditCategory = creditCategoryService.findByCode(postData.getCode());
		if (creditCategory == null) {
			creditCategory = create(postData);
		} else {
			creditCategory = update(postData);
		}
		return creditCategory;
	}

	public CreditCategoryDto find(String creditCategoryCode) throws MeveoApiException {
		CreditCategory creditCategory = creditCategoryService.findByCode(creditCategoryCode);
		if (creditCategory == null) {
			throw new EntityDoesNotExistsException(CreditCategory.class, creditCategoryCode);
		}

		return entityToDto(creditCategory);
	}

	public List<CreditCategoryDto> list() throws MeveoApiException {
		List<CreditCategoryDto> result = new ArrayList<>();

		List<CreditCategory> creditCategories = creditCategoryService.listActive();
		if (creditCategories != null && !creditCategories.isEmpty()) {
			result = creditCategories.stream().map(c -> entityToDto(c)).collect(Collectors.toList());
		}

		return result;
	}
	
	public void remove(String creditCategoryCode) throws MeveoApiException, BusinessException {
		CreditCategory creditCategory = creditCategoryService.findByCode(creditCategoryCode);
		if (creditCategory == null) {
			throw new EntityDoesNotExistsException(CreditCategory.class, creditCategoryCode);
		}

		creditCategoryService.remove(creditCategory);
	}

}
