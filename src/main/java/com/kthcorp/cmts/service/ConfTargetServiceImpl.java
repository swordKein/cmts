package com.kthcorp.cmts.service;

import com.kthcorp.cmts.model.ConfTarget;

import java.util.List;

public interface ConfTargetServiceImpl {
    public List<ConfTarget> getAll();
    public List<ConfTarget> getTargetListActiveFirst10();

    ConfTarget getTargetListByPrefix(ConfTarget req);
}
