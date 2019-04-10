package org.meveo.api.ws.impl;

import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.jws.WebService;

import org.meveo.api.billing.AccountingCodeApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.billing.AccountingCodeDto;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.dto.response.billing.AccountingCodeGetResponseDto;
import org.meveo.api.dto.response.billing.AccountingCodeListResponseDto;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.ws.AccountingWs;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.billing.AccountingCode;

/**
 * @author Edward P. Legaspi
 * @author Abdellatif BARI
 * @lastModifiedVersion 7.0
 */
@WebService(serviceName = "AccountingWs", endpointInterface = "org.meveo.api.ws.AccountingWs")
@Interceptors({ WsRestApiInterceptor.class })
public class AccountingWsImpl extends BaseWs implements AccountingWs {

    @Inject
    private AccountingCodeApi accountingCodeApi;

    @Override
    public ActionStatus createAccountingCode(AccountingCodeDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            AccountingCode accountingCode = accountingCodeApi.create(postData);
            if (StringUtils.isBlank(postData.getCode())) {
                result.setEntityCode(accountingCode.getCode());
            }
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus updateAccountingCode(AccountingCodeDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            accountingCodeApi.update(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus createOrUpdateAccountingCode(AccountingCodeDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            AccountingCode accountingCode = accountingCodeApi.createOrUpdate(postData);
            if (StringUtils.isBlank(postData.getCode())) {
                result.setEntityCode(accountingCode.getCode());
            }
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public AccountingCodeGetResponseDto findAccountingCode(String accountingCode) {
        AccountingCodeGetResponseDto result = new AccountingCodeGetResponseDto();

        try {
            result.setAccountingCode(accountingCodeApi.find(accountingCode));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public AccountingCodeListResponseDto listAccountingCode(PagingAndFiltering pagingAndFiltering) {
        AccountingCodeListResponseDto result = new AccountingCodeListResponseDto();

        try {
            return accountingCodeApi.list(pagingAndFiltering);
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus removeAccountingCode(String accountingCode) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            accountingCodeApi.remove(accountingCode);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus enableAccountingCode(String code) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            accountingCodeApi.enableOrDisable(code, true);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus disableAccountingCode(String code) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            accountingCodeApi.enableOrDisable(code, false);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }
}