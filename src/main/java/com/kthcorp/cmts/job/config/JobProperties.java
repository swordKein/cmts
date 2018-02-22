package com.kthcorp.cmts.job.config;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;

/**
 * Created by Ice on 11/4/2016.
 */
@Component
public class JobProperties {

    @NotNull
    @NotEmpty
    private String cronExpression;

    @NotNull
    @NotEmpty
    private String jobType;

    public String getCronExpression() {
        return cronExpression;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

    public String getJobType() {
        return jobType;
    }

    public void setJobType(String jobType) {
        this.jobType = jobType;
    }
}
