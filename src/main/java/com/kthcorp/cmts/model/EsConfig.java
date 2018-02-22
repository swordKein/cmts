package com.kthcorp.cmts.model;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class EsConfig {
    public static EsConfig INSTANCE;

    @Value("${elasticsearch.host}")
    private String es_host;
    @Value("${elasticsearch.port}")
    private Integer es_port;
    @Value("${elasticsearch.idx}")
    private String idx;

    @PostConstruct
    public void init() {
        INSTANCE = this;
    }

    public String getEs_host() {
        return es_host;
    }
    public Integer getEs_port() {
        return es_port;
    }
    public String getIdx() { return idx; }

}
