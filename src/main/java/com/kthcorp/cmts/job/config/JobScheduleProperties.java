package com.kthcorp.cmts.job.config;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@ConfigurationProperties(prefix = "schedule")
@Validated
public class JobScheduleProperties {

    @NotNull
    @NotEmpty
    @Valid
    private List<JobProperties> jobs;

    public List<JobProperties> getJobs() {
        return jobs;
    }

    public void setJobs(List<JobProperties> jobs) {
        this.jobs = jobs;
    }
}
