package com.kthcorp.cmts.util;

import org.bitbucket.eunjeon.seunjeon.Analyzer;
import org.bitbucket.eunjeon.seunjeon.Eojeol;
import org.bitbucket.eunjeon.seunjeon.LNode;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.client.transport.TransportClient;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

public class ElasticsearchUtil {

    // 형태소 분석 후 단어 취득
    public static String getSimpleWords(String reqUrl, String req) throws Exception {
       String result = "";
        try {
            System.out.println("#ElasticSearch URI:"+reqUrl);

            Map<String,Object> reqMap = new HashMap<String, Object>();
            reqMap.put("data", req);
            result = HttpClientUtil.reqGet(reqUrl, "", null,reqMap, "bypass");

            System.out.println("#ElasticSearch Result:"+result.toString());

        } catch (Exception e) { e.printStackTrace(); }

       return result;
    }

    // 인덱스에 데이터 PUT
    public static String putData(String reqUrl, Map<String, Object> req) throws Exception {
        String result = "";
        try {
            String resultStr = HttpClientUtil.reqPut(reqUrl, req);

        } catch (Exception e) { e.printStackTrace(); }

        return result;
    }

}