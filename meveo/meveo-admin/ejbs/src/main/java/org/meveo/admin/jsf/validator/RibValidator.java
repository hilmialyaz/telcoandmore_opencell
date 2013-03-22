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
package org.meveo.admin.jsf.validator;

import java.math.BigDecimal;
import java.util.ResourceBundle;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;

/**
 * @author Gediminas Ubartas
 * @created Jan 31, 2011
 * 
 */
@FacesValidator("ribValidator")
public class RibValidator implements Validator {
    
    @Inject
    private ResourceBundle resourceMessages;
    
	public boolean checkRib(String rib) {
		StringBuilder extendedRib = new StringBuilder(rib.length());
		for (char currentChar : rib.toCharArray()) {
			// Works on base 36
			int currentCharValue = Character.digit(currentChar, Character.MAX_RADIX);
			if (currentCharValue == -1)
				return false;
			// Convert character to simple digit
			extendedRib
					.append(currentCharValue < 10 ? currentCharValue
							: (currentCharValue + (int) StrictMath.pow(2,
									(currentCharValue - 10) / 9)) % 10);
		}

		return new BigDecimal(extendedRib.toString()).remainder(new BigDecimal(97)).intValue() == 0;
	}

	public void validate(FacesContext context, UIComponent component, Object value)
			throws ValidatorException {

		/*
		 * TODO: ModelValidator modelValidator = new ModelValidator();
		 * modelValidator.validate(context, component, value);
		 */

		String bankCodeId = (String) component.getAttributes().get("bankCodeId");
		UIInput bankCodeField = (UIInput) context.getViewRoot().findComponent(bankCodeId);

		String branchCodeId = (String) component.getAttributes().get("branchCodeId");
		UIInput branchCodeField = (UIInput) context.getViewRoot().findComponent(branchCodeId);

		String accountNumberId = (String) component.getAttributes().get("accountNumberId");
		UIInput accountNumberField = (UIInput) context.getViewRoot().findComponent(accountNumberId);

		String keyId = (String) component.getAttributes().get("keyId");
		UIInput keyField = (UIInput) context.getViewRoot().findComponent(keyId);

		StringBuilder rib = new StringBuilder();
		rib.append(bankCodeField.getSubmittedValue());
		rib.append(branchCodeField.getSubmittedValue());
		rib.append(accountNumberField.getSubmittedValue());
		rib.append(keyField.getSubmittedValue());

		if (!checkRib(rib.toString())) {
			FacesMessage facesMessage = new FacesMessage();
			String message = resourceMessages.getString("commons.ribValidation");
			facesMessage.setDetail(message);
			facesMessage.setSummary(message);
			facesMessage.setSeverity(FacesMessage.SEVERITY_ERROR);
			throw new ValidatorException(facesMessage);
		}
	}
}
