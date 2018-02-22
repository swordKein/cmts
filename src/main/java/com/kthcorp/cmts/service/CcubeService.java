package com.kthcorp.cmts.service;

import com.google.gson.JsonObject;
import com.kthcorp.cmts.mapper.AuthUserMapper;
import com.kthcorp.cmts.mapper.CcubeMapper;
import com.kthcorp.cmts.mapper.ItemsMetasMapper;
import com.kthcorp.cmts.model.*;
import com.kthcorp.cmts.util.AES256Util;
import com.kthcorp.cmts.util.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

@Service
public class CcubeService implements CcubeServiceImpl {

    private static final Logger logger = LoggerFactory.getLogger(CollectService.class);

    @Value("${cmts.property.serverid}")
    private String serverid;
    @Autowired
    private CcubeMapper ccubeMapper;

    @Override
    public List<CcubeContent> get50ActiveCcubeContents() {
        return ccubeMapper.get50ActiveCcubeContents();
    }

    @Override
    public int uptCcubeContentStat(CcubeContent req) {
        return ccubeMapper.uptCcubeContentStat(req);
    }

    @Override
    public List<CcubeSeries> get50ActiveCcubeSeries() {
        return ccubeMapper.get50ActiveCcubeSeries();
    }

    @Override
    public int uptCcubeSeriesStat(CcubeSeries req) {
        return ccubeMapper.uptCcubeSeriesStat(req);
    }

    @Override
    public CcubeContent getCcubeContentByCid(CcubeContent req) {
        return ccubeMapper.getCcubeContentByCid(req);
    }

    @Override
    public CcubeSeries getCcubeSeriesById(CcubeSeries req) {
        return ccubeMapper.getCcubeSeriesById(req);
    }

    @Override
    public int getCcubeItemIdx(CcubeKeys req) {
        return ccubeMapper.getCcubeItemIdx(req);
    }

    @Override
    public int getCcubeCIdx(CcubeKeys req) {
        return ccubeMapper.getCcubeKeysIdx(req);
    }


    @Override
    public int insCcubeKeys(CcubeKeys req) {
        int result = 0;
        if (req != null) {
            if (req.getContent_id() != null && "".equals(req.getContent_id())) req.setContent_id("");
            if (req.getKmrb_id() != null && "".equals(req.getKmrb_id())) req.setKmrb_id("");
            if (req.getMaster_content_id() != null && "".equals(req.getMaster_content_id())) req.setMaster_content_id("");
            if (req.getSeries_id() != null && "".equals(req.getSeries_id())) req.setSeries_id("");

            result = ccubeMapper.insCcubeKeys(req);
            if (result > 0 && req.getCidx() > 0) {
                result = req.getCidx();
            }
        }

        return result;
    }

    private String getEmptyCheck(Object req) {
        if (req == null) {
            return "";
        } else {
            return req.toString();
        }
    }

    @Override
    public JsonObject getCcubeDatasByItemIdx(int itemIdx) {
        JsonObject result = null;
        if (itemIdx > 0) {
            CcubeKeys ckey = ccubeMapper.getCcubeKeys(itemIdx);
            if(ckey != null) {
                if(ckey.getSeries_id() != null && !"0".equals(ckey.getSeries_id())) {
                    CcubeSeries ser = new CcubeSeries();
                    ser.setSeries_id(ckey.getSeries_id());
                    CcubeSeries curSer = ccubeMapper.getCcubeSeriesById(ser);
                    if (curSer != null) {
                        result = new JsonObject();
                        result.addProperty("SERIES_ID", curSer.getSeries_id());
                        result.addProperty("SERIES_NM", getEmptyCheck(curSer.getSeries_nm()));
                        result.addProperty("MASTER_CONTENT_ID", "");
                        result.addProperty("CONTENT_ID", "");
                        result.addProperty("PURITY_TITLE", getEmptyCheck(curSer.getPurity_title()));
                        result.addProperty("CONTENT_TITLE", "");
                        result.addProperty("ENG_TITLE", getEmptyCheck(curSer.getEng_title()));
                        result.addProperty("TITLE_BRIEF", "");
                        result.addProperty("DIRECTOR", getEmptyCheck(curSer.getDirector()));
                        result.addProperty("YEAR", getEmptyCheck(curSer.getYear()));
                        result.addProperty("ACTORS_DISPLAY", getEmptyCheck(curSer.getActors_display()));
                        result.addProperty("COUNTRY_OF_ORIGIN", getEmptyCheck(curSer.getCountry_of_origin()));
                        result.addProperty("SAD_CTGRY_NM", getEmptyCheck(curSer.getSad_ctgry_nm()));
                        result.addProperty("DOMESTIC_RELEASE_DATE", "");
                        result.addProperty("KT_RATING", getEmptyCheck(curSer.getKt_rating()));
                        result.addProperty("KMRB_ID", "");
                        result.addProperty("POSTER_URL", getEmptyCheck(curSer.getPoster_url()));
                    }
                } else if (result == null && ckey.getContent_id() != null && !"0".equals(ckey.getContent_id())) {
                    CcubeContent con = new CcubeContent();
                    con.setContent_id(ckey.getContent_id());
                    CcubeContent curCon = ccubeMapper.getCcubeContentByCid(con);
                    if (curCon != null) {
                        result = new JsonObject();
                        result.addProperty("SERIES_ID", "");
                        result.addProperty("SERIES_NM", "");
                        result.addProperty("MASTER_CONTENT_ID", getEmptyCheck(curCon.getMaster_content_id()));
                        result.addProperty("CONTENT_ID", getEmptyCheck(curCon.getContent_id()));
                        result.addProperty("PURITY_TITLE", getEmptyCheck(curCon.getPurity_title()));
                        result.addProperty("CONTENT_TITLE", getEmptyCheck(curCon.getContent_title()));
                        result.addProperty("ENG_TITLE", getEmptyCheck(curCon.getEng_title()));
                        result.addProperty("TITLE_BRIEF", getEmptyCheck(curCon.getTitle_brief()));
                        result.addProperty("DIRECTOR", getEmptyCheck(curCon.getDirector()));
                        result.addProperty("YEAR", getEmptyCheck(curCon.getYear()));
                        result.addProperty("ACTORS_DISPLAY", getEmptyCheck(curCon.getActors_display()));
                        result.addProperty("COUNTRY_OF_ORIGIN", getEmptyCheck(curCon.getCountry_of_origin()));
                        result.addProperty("SAD_CTGRY_NM", getEmptyCheck(curCon.getSad_ctgry_nm()));
                        result.addProperty("DOMESTIC_RELEASE_DATE", getEmptyCheck(curCon.getDomestic_release_date()));
                        result.addProperty("KT_RATING", getEmptyCheck(curCon.getKt_rating()));
                        result.addProperty("KMRB_ID", getEmptyCheck(curCon.getKmrb_id()));
                        result.addProperty("POSTER_URL", getEmptyCheck(curCon.getPoster_url()));
                    }
                }
            }
        }

        return result;
    }
}
