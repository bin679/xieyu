package com.labmanager.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.labmanager.entity.BorrowRecord;
import java.util.List;

public interface BorrowRecordService extends IService<BorrowRecord> {
    List<BorrowRecord> listWithDetails();
    List<BorrowRecord> listOverdue();
    void returnEquipment(Long borrowId);
}
