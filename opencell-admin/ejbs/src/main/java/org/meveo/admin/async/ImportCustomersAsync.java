/**
 * 
 */
package org.meveo.admin.async;

import java.util.concurrent.Future;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.job.importexport.ImportCustomersJobBean;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.security.MeveoUser;
import org.meveo.security.keycloak.CurrentUserProvider;

/**
 * @author anasseh
 * 
 */

@Stateless
public class ImportCustomersAsync {

    @Inject
    private ImportCustomersJobBean importCustomersJobBean;

    @Inject
    private CurrentUserProvider currentUserProvider;

    /**
     * Import customers
     * 
     * @param result Job execution result
     * @param lastCurrentUser Current user. In case of multitenancy, when user authentication is forced as result of a fired trigger (scheduled jobs, other timed event
     *        expirations), current user might be lost, thus there is a need to reestablish.
     * @return Future String
     */
    @Asynchronous
    public Future<String> launchAndForget(JobExecutionResultImpl result, MeveoUser lastCurrentUser) {

        currentUserProvider.reestablishAuthentication(lastCurrentUser);
        
        importCustomersJobBean.execute(result);

        return new AsyncResult<String>("OK");
    }
}