package com.kthcorp.cmts.util;

import com.kthcorp.cmts.model.EsConfig;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.Collections;

public class RestUtil {
    // 형태소 분석 후 단어 취득
    /*
    public static String getES(String reqStr) throws Exception {
        String result = "";

        try {
            EsConfig esConfig = new EsConfig();
            System.out.println("##REST::ElasticSearch server:"+EsConfig.INSTANCE.getEs_host()+":"+EsConfig.INSTANCE.getEs_port()+"//:request_param:"+reqStr);
            RestClient restClient = RestClient.builder(
                    new HttpHost(EsConfig.INSTANCE.getEs_host(), EsConfig.INSTANCE.getEs_port(), "http")).build();

            HttpEntity entity = new NStringEntity(reqStr, ContentType.APPLICATION_JSON);

            Response response = restClient.performRequest("GET", "/"+EsConfig.INSTANCE.getIdx()+"/_analyze?analyzer=korean",
                    Collections.singletonMap("pretty", "true"), entity);

            //System.out.println(EntityUtils.toString(response.getEntity()));
            result = EntityUtils.toString(response.getEntity());

            System.out.println("#REST::ElasticSearch Result:"+result.toString());

        } catch (Exception e) { e.printStackTrace(); }

       return result;
    }
    */
}