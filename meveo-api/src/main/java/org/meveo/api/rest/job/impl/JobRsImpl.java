package org.meveo.api.rest.job.impl;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.api.MeveoApiErrorCode;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.job.TimerEntityDto;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.job.JobApi;
import org.meveo.api.job.TimerEntityApi;
import org.meveo.api.logging.LoggingInterceptor;
import org.meveo.api.rest.impl.BaseRs;
import org.meveo.api.rest.job.JobRs;
import org.meveo.model.jobs.TimerInfoDto;
import org.slf4j.Logger;

/**
 * @author Edward P. Legaspi
 **/
@RequestScoped
@Interceptors({ LoggingInterceptor.class })
public class JobRsImpl extends BaseRs implements JobRs {

	@Inject
	private Logger log;

	@Inject
	private JobApi jobApi;
	
	@Inject
	private TimerEntityApi timerEntityApi;

	@Override
	public ActionStatus executeTimer(TimerInfoDto postData) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
		try {
			jobApi.executeTimer(postData, getCurrentUser());
		} catch (MeveoApiException e) {
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
			log.error("error occurred while executing timer ",e);
		} catch (Exception e) {
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage()); 
			log.error("error generated while executing timer ",e);
		}

		log.debug("RESPONSE={}", result);
		return result;
	}
	
	@Override
	public ActionStatus create(TimerEntityDto timerEntityDto) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
		try {
			timerEntityApi.create(timerEntityDto, getCurrentUser());
		} catch (MeveoApiException e) {
			result.setErrorCode(e.getErrorCode());
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
		} catch (Exception e) {
			result.setErrorCode(MeveoApiErrorCode.GENERIC_API_EXCEPTION);
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
		}

		log.debug("RESPONSE={}", result);
		return result;
	}
	
	 
}
