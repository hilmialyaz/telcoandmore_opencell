package org.meveo.model.notification;

/**
 * @author Abdellatif BARI
 * @lastModifiedVersion 7.0
 */

public enum NotificationEventTypeEnum {
    CREATED, UPDATED, REMOVED, TERMINATED, DISABLED, PROCESSED, REJECTED, REJECTED_CDR, LOGGED_IN, INBOUND_REQ, ENABLED,
    LOW_BALANCE, FILE_UPLOAD, FILE_DOWNLOAD, FILE_RENAME, FILE_DELETE, COUNTER_DEDUCED, END_OF_TERM, STATUS_UPDATED, RENEWAL_UPDATED;

    public String getLabel() {
        return this.getClass().getSimpleName() + "." + this.name();
    }
}
