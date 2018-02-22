package com.kthcorp.cmts.service;

import com.kthcorp.cmts.mapper.ConfTargetMapper;
import com.kthcorp.cmts.model.ConfTarget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ConfTargetService implements ConfTargetServiceImpl {
    private static final Logger log = LoggerFactory.getLogger(ConfTargetService.class);

    @Autowired
    private ConfTargetMapper confTargetMapper;

    @Override
    public List<ConfTarget> getAll() {
        return confTargetMapper.getAll();
    }

    @Override
    public List<ConfTarget> getTargetListActiveFirst10() {
        return confTargetMapper.getTargetListActiveFirst10();
    }

    @Override
    public ConfTarget getTargetListByPrefix (ConfTarget req) { return confTargetMapper.getTargetListByPrefix(req);  }
}
