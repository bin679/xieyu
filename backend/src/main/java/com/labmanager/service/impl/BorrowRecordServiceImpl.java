package com.labmanager.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.labmanager.entity.BorrowRecord;
import com.labmanager.entity.Equipment;
import com.labmanager.mapper.BorrowRecordMapper;
import com.labmanager.mapper.EquipmentMapper;
import com.labmanager.service.BorrowRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BorrowRecordServiceImpl extends ServiceImpl<BorrowRecordMapper, BorrowRecord> implements BorrowRecordService {

    @Autowired
    private EquipmentMapper equipmentMapper;

    @Override
    public List<BorrowRecord> listWithDetails() {
        return baseMapper.selectAllWithDetails();
    }

    @Override
    public List<BorrowRecord> listOverdue() {
        return baseMapper.selectOverdueRecords();
    }

    @Override
    @Transactional
    public void returnEquipment(Long borrowId) {
        BorrowRecord record = baseMapper.selectById(borrowId);
        if (record == null) {
            throw new IllegalArgumentException("借用记录不存在");
        }
        if (!"borrowing".equals(record.getStatus()) && !"overdue".equals(record.getStatus())) {
            throw new IllegalArgumentException("该记录不是借用中状态");
        }
        record.setStatus("returned");
        record.setActualReturnTime(LocalDateTime.now());
        baseMapper.updateById(record);

        Equipment equipment = equipmentMapper.selectById(record.getEquipmentId());
        if (equipment != null) {
            equipment.setStatus("available");
            equipmentMapper.updateById(equipment);
        }
    }
}
