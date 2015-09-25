package com.hari.development.gitissuetracker;

import org.joda.time.DateTime;


/**
 * Created by development on 25/09/15.
 */
public class Issue {

    private DateTime createdAt;

    public DateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(DateTime createdAt) {
        this.createdAt = createdAt;
    }
}
