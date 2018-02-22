package com.kthcorp.cmts.job.schedule.jobs;

import com.kthcorp.cmts.service.*;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by Ice on 11/4/2016.
 */
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class JobRunner implements Job {
    static final Logger logger = LoggerFactory.getLogger(JobRunner.class);

    private String jobType;

    @Autowired private CollectService collectService;
    @Autowired private RefineService refineService;
    @Autowired private AnalyzeService analyzeService;
    @Autowired private ItemsService itemsService;
    @Autowired private SftpService sftpService;
    //@Autowired
    //private MainService mainServiceImpl;
    //@Autowired
    //private CollectService collecService;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        //someService.writeDataToLog(jobType);
        //mainServiceImpl.runMainService();
        int rt = 0;
        try {
            switch (jobType) {
                case "collectService":
                    rt = collectService.ollehTvMetaCollectScheduleCheck();
                    logger.info("#MLOG schedule.start jobType:"+jobType+"/rt:"+rt);
                    break;
                case "refineService":
                    rt = refineService.ollehTvMetaRefineScheduleCheck();
                    logger.info("#MLOG schedule.start jobType:"+jobType+"/rt:"+rt);
                    break;
                case "analyzeService":
                    rt = analyzeService.ollehTvMetaAnalyzeScheduleCheck();
                    logger.info("#MLOG schedule.start jobType:"+jobType+"/rt:"+rt);
                    break;
                case "itemsService":
                    rt = itemsService.checkInItems();
                    logger.info("#MLOG schedule.checkInItems.start jobType:"+jobType+"/rt:"+rt);
                    break;
                case "dummyService":
                    logger.info("#MLOG schedule.check dummyService");
                    break;
                case "sftpService":
                    rt = sftpService.pollingCcubeSftp();
                    logger.info("#MLOG schedule.check sftpService");
                    break;
            }
        } catch (Exception e) {
            logger.info("#MLOG schedule.error jobType:"+jobType+"/rt:"+rt);
            e.printStackTrace();
        }
    }

    public String getJobType() {
        return jobType;
    }

    public void setJobType(String jobType) {
        this.jobType = jobType;
    }

    public CollectService getCollectService() {
        return collectService;
    }

    public void setCollectService(CollectService collectService) {
        this.collectService = collectService;
    }

    public RefineService getRefineService() {
        return refineService;
    }

    public void setRefineService(RefineService refineService) {
        this.refineService = refineService;
    }

    public AnalyzeService getAnalyzeService() {
        return analyzeService;
    }

    public void setAnalyzeService(AnalyzeService analyzeService) {
        this.analyzeService = analyzeService;
    }

    public ItemsService getItemsService() {
        return itemsService;
    }

    public void setItemsService(ItemsService itemsService) {
        this.itemsService = itemsService;
    }
}
