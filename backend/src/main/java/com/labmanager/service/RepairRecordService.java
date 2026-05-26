package com.labmanager.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.labmanager.entity.RepairRecord;
import java.util.List;

public interface RepairRecordService extends IService<RepairRecord> {
    List<RepairRecord> listWithDetails();
}
