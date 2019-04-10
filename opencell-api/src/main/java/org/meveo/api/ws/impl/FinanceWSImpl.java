package org.meveo.api.ws.impl;

import java.util.List;

import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.jws.WebService;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.finance.ReportExtractDto;
import org.meveo.api.dto.finance.RevenueRecognitionRuleDto;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.dto.response.finance.ReportExtractExecutionResultResponseDto;
import org.meveo.api.dto.response.finance.ReportExtractExecutionResultsResponseDto;
import org.meveo.api.dto.response.finance.ReportExtractResponseDto;
import org.meveo.api.dto.response.finance.ReportExtractsResponseDto;
import org.meveo.api.dto.response.finance.RunReportExtractDto;
import org.meveo.api.dto.response.payment.RevenueRecognitionRuleDtoResponse;
import org.meveo.api.dto.response.payment.RevenueRecognitionRuleDtosResponse;
import org.meveo.api.finance.ReportExtractApi;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.payment.RevenueRecognitionRuleApi;
import org.meveo.api.ws.FinanceWs;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.finance.ReportExtract;
import org.meveo.model.finance.RevenueRecognitionRule;

/**
 * @author Edward P. Legaspi
 * @author Abdellatif BARI
 * @lastModifiedVersion 7.0
 */
@WebService(serviceName = "FinanceWs", endpointInterface = "org.meveo.api.ws.FinanceWs")
@Interceptors({ WsRestApiInterceptor.class })
public class FinanceWSImpl extends BaseWs implements FinanceWs {

    @Inject
    private RevenueRecognitionRuleApi rrrApi;

    @Inject
    private ReportExtractApi reportExtractApi;

    @Override
    public ActionStatus createRevenueRecognitionRule(RevenueRecognitionRuleDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
        try {
            RevenueRecognitionRule revenueRecognitionRule = rrrApi.create(postData);
            if (StringUtils.isBlank(postData.getCode())) {
                result.setEntityCode(revenueRecognitionRule.getCode());
            }
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus updateRevenueRecognitionRule(RevenueRecognitionRuleDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
        try {
            rrrApi.update(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus deleteRevenueRecognitionRule(String code) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
        try {
            rrrApi.remove(code);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public RevenueRecognitionRuleDtosResponse listRevenueRecognitionRules() {
        RevenueRecognitionRuleDtosResponse result = new RevenueRecognitionRuleDtosResponse();
        result.getActionStatus().setStatus(ActionStatusEnum.SUCCESS);
        result.getActionStatus().setMessage("");
        try {
            List<RevenueRecognitionRuleDto> dtos = rrrApi.list();
            result.setRevenueRecognitionRules(dtos);
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public RevenueRecognitionRuleDtoResponse getRevenueRecognitionRule(String code) {
        RevenueRecognitionRuleDtoResponse result = new RevenueRecognitionRuleDtoResponse();
        result.getActionStatus().setStatus(ActionStatusEnum.SUCCESS);

        try {
            result.setRevenueRecognitionRuleDto(rrrApi.find(code));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus createOrUpdateRevenueRecognitionRule(RevenueRecognitionRuleDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
        try {
            RevenueRecognitionRule revenueRecognitionRule = rrrApi.createOrUpdate(postData);
            if (StringUtils.isBlank(postData.getCode())) {
                result.setEntityCode(revenueRecognitionRule.getCode());
            }
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus createReportExtract(ReportExtractDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            ReportExtract reportExtract = reportExtractApi.create(postData);
            if (StringUtils.isBlank(postData.getCode())) {
                result.setEntityCode(reportExtract.getCode());
            }
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus updateReportExtract(ReportExtractDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            reportExtractApi.update(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus createOrUpdateReportExtract(ReportExtractDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            ReportExtract reportExtract = reportExtractApi.createOrUpdate(postData);
            if (StringUtils.isBlank(postData.getCode())) {
                result.setEntityCode(reportExtract.getCode());
            }
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus removeReportExtract(String reportExtractCode) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            reportExtractApi.remove(reportExtractCode);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ReportExtractsResponseDto listReportExtract(PagingAndFiltering pagingAndFiltering) {
        ReportExtractsResponseDto result = new ReportExtractsResponseDto();

        try {
            result = reportExtractApi.list(pagingAndFiltering);
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ReportExtractResponseDto findReportExtract(String reportExtractCode) {
        ReportExtractResponseDto result = new ReportExtractResponseDto();

        try {
            result.setReportExtract(reportExtractApi.find(reportExtractCode));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ReportExtractExecutionResultResponseDto runReportExtract(RunReportExtractDto postData) {
    	ReportExtractExecutionResultResponseDto result = new ReportExtractExecutionResultResponseDto();

        try {
        	result.setReportExtractExecutionResult(reportExtractApi.runReportExtract(postData));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus enableRevenueRecognitionRule(String code) {
        ActionStatus result = new ActionStatus();

        try {
            rrrApi.enableOrDisable(code, true);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus disableRevenueRecognitionRule(String code) {
        ActionStatus result = new ActionStatus();

        try {
            rrrApi.enableOrDisable(code, false);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus enableReportExtract(String code) {
        ActionStatus result = new ActionStatus();

        try {
            reportExtractApi.enableOrDisable(code, true);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus disableReportExtract(String code) {
        ActionStatus result = new ActionStatus();

        try {
            reportExtractApi.enableOrDisable(code, false);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ReportExtractExecutionResultsResponseDto listReportExtractRunHistory(PagingAndFiltering pagingAndFiltering) {
        ReportExtractExecutionResultsResponseDto result = new ReportExtractExecutionResultsResponseDto();

        try {
            result = reportExtractApi.listReportExtractRunHistory(pagingAndFiltering);
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ReportExtractExecutionResultResponseDto findReportExtractHistoryById(Long id) {
        ReportExtractExecutionResultResponseDto result = new ReportExtractExecutionResultResponseDto();

        try {
            result.setReportExtractExecutionResult(reportExtractApi.findReportExtractHistory(id));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ReportExtractExecutionResultsResponseDto findReportExtractHistoryByCode(String code) {
        ReportExtractExecutionResultsResponseDto result = new ReportExtractExecutionResultsResponseDto();

        try {
            result = reportExtractApi.listReportExtractRunHistoryByRECode(code);
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

}