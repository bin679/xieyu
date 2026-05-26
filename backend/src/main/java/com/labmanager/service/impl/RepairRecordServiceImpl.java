package com.labmanager.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.labmanager.entity.RepairRecord;
import com.labmanager.mapper.RepairRecordMapper;
import com.labmanager.service.RepairRecordService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RepairRecordServiceImpl extends ServiceImpl<RepairRecordMapper, RepairRecord> implements RepairRecordService {

    @Override
    public List<RepairRecord> listWithDetails() {
        return baseMapper.selectAllWithDetails();
    }
}
