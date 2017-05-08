package org.meveo.api.ws.impl;

import java.util.Date;

import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.jws.WebService;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.payment.CardTokenRequestDto;
import org.meveo.api.dto.payment.CardTokenResponseDto;
import org.meveo.api.dto.payment.DDRequestLotOpDto;
import org.meveo.api.dto.payment.DoPaymentRequestDto;
import org.meveo.api.dto.payment.DoPaymentResponseDto;
import org.meveo.api.dto.payment.PaymentDto;
import org.meveo.api.dto.response.CustomerPaymentsResponse;
import org.meveo.api.dto.response.payment.DDRequestLotOpsResponseDto;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.payment.DDRequestLotOpApi;
import org.meveo.api.payment.PaymentApi;
import org.meveo.api.ws.PaymentWs;
import org.meveo.model.payments.DDRequestOpStatusEnum;

@WebService(serviceName = "PaymentWs", endpointInterface = "org.meveo.api.ws.PaymentWs")
@Interceptors({ WsRestApiInterceptor.class })
public class PaymentWsImpl extends BaseWs implements PaymentWs {

    @Inject
    private PaymentApi paymentApi;

    @Inject
    private DDRequestLotOpApi ddrequestLotOpApi;
    
    @Override
    public ActionStatus create(PaymentDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            paymentApi.createPayment(postData);
        } catch (Exception e) {
            log.error("Failed to execute API", e);
            processException(e, result);
        }

        return result;
    }

    @Override
    public CustomerPaymentsResponse list(String customerAccountCode) {
        CustomerPaymentsResponse result = new CustomerPaymentsResponse();
        result.getActionStatus().setStatus(ActionStatusEnum.SUCCESS);

        try {
            result.setCustomerPaymentDtoList(paymentApi.getPaymentList(customerAccountCode));
            result.setBalance(paymentApi.getBalance(customerAccountCode));
        } catch (Exception e) {
            log.error("Failed to execute API", e);
            processException(e, result.getActionStatus());
        }

        return result;
    }

	@Override
	public ActionStatus createDDRequestLotOp(DDRequestLotOpDto ddrequestLotOp) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            ddrequestLotOpApi.create(ddrequestLotOp);
        } catch (Exception e) {
            log.error("Failed to execute API", e);
            processException(e, result);
        }
        return result;	
        }

	@Override
	public DDRequestLotOpsResponseDto listDDRequestLotops(Date fromDueDate,Date toDueDate,DDRequestOpStatusEnum status) {
		DDRequestLotOpsResponseDto result = new DDRequestLotOpsResponseDto();

        try {
            result.setDdrequestLotOps(ddrequestLotOpApi.listDDRequestLotOps(fromDueDate,toDueDate,status));

        } catch (Exception e) {
            log.error("Failed to execute API", e);
            processException(e, result.getActionStatus());
        }

        return result;	
        }

	
	@Override
	public CardTokenResponseDto createCardToken(CardTokenRequestDto cardTokenRequestDto) {
		CardTokenResponseDto response = new CardTokenResponseDto();
		response.setActionStatus(new ActionStatus(ActionStatusEnum.FAIL, ""));
		try{
			response.setTokenID(paymentApi.createCardToken(cardTokenRequestDto));
			response.setActionStatus(new ActionStatus(ActionStatusEnum.SUCCESS, ""));
		}catch(Exception e){
			processException(e, response.getActionStatus());
		}
		
		return response;
	}

	@Override
	public DoPaymentResponseDto doPayment(DoPaymentRequestDto doPaymentRequestDto) {
		DoPaymentResponseDto response = new DoPaymentResponseDto();	
		response.setActionStatus(new ActionStatus(ActionStatusEnum.FAIL, ""));
		try{
			response = paymentApi.doPayment(doPaymentRequestDto);
			response.setActionStatus(new ActionStatus(ActionStatusEnum.SUCCESS, ""));
		}catch(Exception e){
			processException(e, response.getActionStatus());
		}		
		return response;
	}

}
