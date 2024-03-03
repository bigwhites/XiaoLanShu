package com.ricky.apicommon.userInfo.entity;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * <p>
 * 
 * </p>
 *
 * @author bigwhites
 * @since 2024-03-03
 */
public class Follow implements Serializable {

    public String uuid;

    public String fromUid;

    public String toUid;

    public LocalDate creareDate;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getFromUid() {
        return fromUid;
    }

    public void setFromUid(String fromUid) {
        this.fromUid = fromUid;
    }

    public String getToUid() {
        return toUid;
    }

    public void setToUid(String toUid) {
        this.toUid = toUid;
    }

    public LocalDate getCreareDate() {
        return creareDate;
    }

    public void setCreareDate(LocalDate creareDate) {
        this.creareDate = creareDate;
    }

    @Override
    public String toString() {
        return "Follow{" +
        "uuid = " + uuid +
        ", fromUid = " + fromUid +
        ", toUid = " + toUid +
        ", creareDate = " + creareDate +
        "}";
    }
}
