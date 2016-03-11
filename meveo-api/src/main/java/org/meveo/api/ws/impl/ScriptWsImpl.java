package org.meveo.api.ws.impl;

import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.jws.WebService;

import org.meveo.api.MeveoApiErrorCodeEnum;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.response.script.OfferModelScriptResponseDto;
import org.meveo.api.dto.response.script.ServiceModelScriptResponseDto;
import org.meveo.api.dto.script.OfferModelScriptDto;
import org.meveo.api.dto.script.ServiceModelScriptDto;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.script.OfferModelScriptApi;
import org.meveo.api.script.ServiceModelScriptApi;
import org.meveo.api.ws.ScriptWs;

/**
 * @author Edward P. Legaspi
 **/
@WebService(serviceName = "ScriptWs", endpointInterface = "org.meveo.api.ws.ScriptWs")
@Interceptors({ WsRestApiInterceptor.class })
public class ScriptWsImpl extends BaseWs implements ScriptWs {

	@Inject
	private OfferModelScriptApi offerModelScriptApi;

	@Inject
	private ServiceModelScriptApi serviceModelScriptApi;

	@Override
	public ActionStatus createOfferModelScript(OfferModelScriptDto postData) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
		try {
			offerModelScriptApi.create(postData, getCurrentUser());
		} catch (MeveoApiException e) {
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
			log.error("Error when create {}. {}", this.getClass().getSimpleName(), e);
		} catch (Exception e) {
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
			log.error("Unknown exception when create {}. {}", this.getClass().getSimpleName(), e);
		}
		log.debug("RESPONSE={}", result);
		return result;
	}

	@Override
	public ActionStatus updateOfferModelScript(OfferModelScriptDto postData) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
		try {
			offerModelScriptApi.update(postData, getCurrentUser());
		} catch (MeveoApiException e) {
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
			log.error("Error when update {}. {}", this.getClass().getSimpleName(), e);
		} catch (Exception e) {
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
			log.error("Unknown exception when update {}. {}", this.getClass().getSimpleName(), e);
		}
		log.debug("RESPONSE={}", result);
		return result;
	}

	@Override
	public ActionStatus createOrUpdateOfferModelScript(OfferModelScriptDto postData) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

		try {
			offerModelScriptApi.createOrUpdate(postData, getCurrentUser());
		} catch (MeveoApiException e) {
			result.setErrorCode(e.getErrorCode());
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
		} catch (Exception e) {
			result.setErrorCode(MeveoApiErrorCodeEnum.GENERIC_API_EXCEPTION);
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
		}

		log.debug("RESPONSE={}", result);
		return result;
	}

	@Override
	public ActionStatus removeOfferModelScript(String code) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
		try {
			offerModelScriptApi.delete(code, getCurrentUser());
		} catch (MeveoApiException e) {
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
			log.error("Error when delete {}. {}", this.getClass().getSimpleName(), e);
		} catch (Exception e) {
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
			log.error("Unknown exception when delete {}. {}", this.getClass().getSimpleName(), e);
		}
		log.debug("RESPONSE={}", result);
		return result;
	}

	@Override
	public OfferModelScriptResponseDto findOfferModelScript(String code) {
		OfferModelScriptResponseDto result = new OfferModelScriptResponseDto();
		result.getActionStatus().setStatus(ActionStatusEnum.SUCCESS);

		try {
			result.setOfferModelScript(offerModelScriptApi.get(code, getCurrentUser().getProvider()));
		} catch (Exception e) {
			result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
			result.getActionStatus().setMessage(e.getMessage());
			log.error("Error when get {}. {}", this.getClass().getSimpleName(), e);
		}
		log.debug("RESPONSE={}", result);
		return result;
	}

	@Override
	public ActionStatus createServiceModelScript(ServiceModelScriptDto postData) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
		try {
			serviceModelScriptApi.create(postData, getCurrentUser());
		} catch (MeveoApiException e) {
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
			log.error("Error when create {}. {}", this.getClass().getSimpleName(), e);
		} catch (Exception e) {
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
			log.error("Unknown exception when create {}. {}", this.getClass().getSimpleName(), e);
		}
		log.debug("RESPONSE={}", result);
		return result;
	}

	@Override
	public ActionStatus updateServiceModelScript(ServiceModelScriptDto postData) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
		try {
			serviceModelScriptApi.update(postData, getCurrentUser());
		} catch (MeveoApiException e) {
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
			log.error("Error when update {}. {}", this.getClass().getSimpleName(), e);
		} catch (Exception e) {
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
			log.error("Unknown exception when update {}. {}", this.getClass().getSimpleName(), e);
		}
		log.debug("RESPONSE={}", result);
		return result;
	}

	@Override
	public ActionStatus createOrUpdateServiceModelScript(ServiceModelScriptDto postData) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

		try {
			serviceModelScriptApi.createOrUpdate(postData, getCurrentUser());
		} catch (MeveoApiException e) {
			result.setErrorCode(e.getErrorCode());
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
		} catch (Exception e) {
			result.setErrorCode(MeveoApiErrorCodeEnum.GENERIC_API_EXCEPTION);
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
		}

		log.debug("RESPONSE={}", result);
		return result;
	}

	@Override
	public ActionStatus removeServiceModelScript(String code) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
		try {
			serviceModelScriptApi.delete(code, getCurrentUser());
		} catch (MeveoApiException e) {
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
			log.error("Error when delete {}. {}", this.getClass().getSimpleName(), e);
		} catch (Exception e) {
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
			log.error("Unknown exception when delete {}. {}", this.getClass().getSimpleName(), e);
		}
		log.debug("RESPONSE={}", result);
		return result;
	}

	@Override
	public ServiceModelScriptResponseDto findServiceModelScript(String code) {
		ServiceModelScriptResponseDto result = new ServiceModelScriptResponseDto();
		result.getActionStatus().setStatus(ActionStatusEnum.SUCCESS);

		try {
			result.setServiceModelScript(serviceModelScriptApi.get(code, getCurrentUser().getProvider()));
		} catch (Exception e) {
			result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
			result.getActionStatus().setMessage(e.getMessage());
			log.error("Error when get {}. {}", this.getClass().getSimpleName(), e);
		}
		log.debug("RESPONSE={}", result);
		return result;
	}

}