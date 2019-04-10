package org.meveo.api.ws;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ScriptInstanceDto;
import org.meveo.api.dto.response.GetScriptInstanceResponseDto;
import org.meveo.api.dto.response.ScriptInstanceReponseDto;

@WebService
public interface ScriptInstanceWs extends IBaseWs {

    @WebMethod
    ScriptInstanceReponseDto create(@WebParam(name = "createScriptInstanceRequest") ScriptInstanceDto scriptInstanceDto);

    @WebMethod
    ScriptInstanceReponseDto update(@WebParam(name = "updateScriptInstanceRequest") ScriptInstanceDto scriptInstanceDto);

    @WebMethod
    ActionStatus remove(@WebParam(name = "removeScriptInstanceRequest") String scriptInstanceCode);

    @WebMethod
    GetScriptInstanceResponseDto find(@WebParam(name = "findScriptInstanceRequest") String scriptInstanceCode);

    @WebMethod
    ScriptInstanceReponseDto createOrUpdate(@WebParam(name = "createOrUpdateScriptInstanceRequest") ScriptInstanceDto scriptInstanceDto);

    /**
     * Enable a Script by its code
     * 
     * @param code Script code
     * @return Request processing status
     */
    @WebMethod
    ActionStatus enable(@WebParam(name = "code") String code);

    /**
     * Disable a Script by its code
     * 
     * @param code Script code
     * @return Request processing status
     */
    @WebMethod
    ActionStatus disable(@WebParam(name = "code") String code);

    /**
     * Execute script by its code
     * 
     * @param code Script code
     * @return Request processing status
     */
    @WebMethod
    ActionStatus execute(@WebParam(name = "code") String code);
}