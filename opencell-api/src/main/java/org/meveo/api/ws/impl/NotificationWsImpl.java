package org.meveo.api.ws.impl;

import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.jws.WebService;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.notification.EmailNotificationDto;
import org.meveo.api.dto.notification.ScriptNotificationDto;
import org.meveo.api.dto.notification.WebHookDto;
import org.meveo.api.dto.response.notification.GetEmailNotificationResponseDto;
import org.meveo.api.dto.response.notification.GetScriptNotificationResponseDto;
import org.meveo.api.dto.response.notification.GetWebHookNotificationResponseDto;
import org.meveo.api.dto.response.notification.InboundRequestsResponseDto;
import org.meveo.api.dto.response.notification.NotificationHistoriesResponseDto;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.notification.EmailNotificationApi;
import org.meveo.api.notification.ScriptNotificationApi;
import org.meveo.api.notification.WebHookApi;
import org.meveo.api.ws.NotificationWs;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.notification.EmailNotification;
import org.meveo.model.notification.ScriptNotification;
import org.meveo.model.notification.WebHook;

/**
 * @author Edward P. Legaspi
 * @author Abdellatif BARI
 * @lastModifiedVersion 7.0
 */
@WebService(serviceName = "NotificationWs", endpointInterface = "org.meveo.api.ws.NotificationWs")
@Interceptors({ WsRestApiInterceptor.class })
public class NotificationWsImpl extends BaseWs implements NotificationWs {

    @Inject
    private ScriptNotificationApi notificationApi;

    @Inject
    private WebHookApi webhookNotificationApi;

    @Inject
    private EmailNotificationApi emailNotificationApi;

    @Override
    public ActionStatus createNotification(ScriptNotificationDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            notificationApi.create(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus updateNotification(ScriptNotificationDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            notificationApi.update(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public GetScriptNotificationResponseDto findNotification(String notificationCode) {
        GetScriptNotificationResponseDto result = new GetScriptNotificationResponseDto();

        try {
            result.setNotificationDto(notificationApi.find(notificationCode));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus removeNotification(String notificationCode) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            notificationApi.remove(notificationCode);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus createWebHookNotification(WebHookDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            WebHook webHook = webhookNotificationApi.create(postData);
            if (StringUtils.isBlank(postData.getCode())) {
                result.setEntityCode(webHook.getCode());
            }
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus updateWebHookNotification(WebHookDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            webhookNotificationApi.update(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public GetWebHookNotificationResponseDto findWebHookNotification(String notificationCode) {
        GetWebHookNotificationResponseDto result = new GetWebHookNotificationResponseDto();

        try {
            result.setWebhookDto(webhookNotificationApi.find(notificationCode));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus removeWebHookNotification(String notificationCode) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            webhookNotificationApi.remove(notificationCode);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus createEmailNotification(EmailNotificationDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            EmailNotification emailNotification = emailNotificationApi.create(postData);
            if (StringUtils.isBlank(postData.getCode())) {
                result.setEntityCode(emailNotification.getCode());
            }
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus updateEmailNotification(EmailNotificationDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            emailNotificationApi.update(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public GetEmailNotificationResponseDto findEmailNotification(String notificationCode) {
        GetEmailNotificationResponseDto result = new GetEmailNotificationResponseDto();

        try {
            result.setEmailNotificationDto(emailNotificationApi.find(notificationCode));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus removeEmailNotification(String notificationCode) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            emailNotificationApi.remove(notificationCode);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public NotificationHistoriesResponseDto listNotificationHistory() {
        NotificationHistoriesResponseDto result = new NotificationHistoriesResponseDto();

        try {
            result.setNotificationHistories(notificationApi.listNotificationHistory());
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public InboundRequestsResponseDto listInboundRequest() {
        InboundRequestsResponseDto result = new InboundRequestsResponseDto();

        try {
            result.setInboundRequests(notificationApi.listInboundRequest());
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus createOrUpdateEmailNotification(EmailNotificationDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            EmailNotification emailNotification = emailNotificationApi.createOrUpdate(postData);
            if (StringUtils.isBlank(postData.getCode())) {
                result.setEntityCode(emailNotification.getCode());
            }
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus createOrUpdateNotification(ScriptNotificationDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
        try {
            notificationApi.createOrUpdate(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus createOrUpdateWebHookNotification(WebHookDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            WebHook webHook = webhookNotificationApi.createOrUpdate(postData);
            if (StringUtils.isBlank(postData.getCode())) {
                result.setEntityCode(webHook.getCode());
            }
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus enableNotification(String code) {
        ActionStatus result = new ActionStatus();

        try {
            notificationApi.enableOrDisable(code, true);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus disableNotification(String code) {
        ActionStatus result = new ActionStatus();

        try {
            notificationApi.enableOrDisable(code, false);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus enableWebHookNotification(String code) {
        ActionStatus result = new ActionStatus();

        try {
            webhookNotificationApi.enableOrDisable(code, true);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus disableWebHookNotification(String code) {
        ActionStatus result = new ActionStatus();

        try {
            webhookNotificationApi.enableOrDisable(code, false);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus enableEmailNotification(String code) {
        ActionStatus result = new ActionStatus();

        try {
            emailNotificationApi.enableOrDisable(code, true);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus disableEmailNotification(String code) {
        ActionStatus result = new ActionStatus();

        try {
            emailNotificationApi.enableOrDisable(code, false);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }
}