package com.labmanager.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.labmanager.entity.Equipment;
import java.util.List;
import java.util.Map;

public interface EquipmentService extends IService<Equipment> {
    List<Equipment> listWithLab();
    List<Map<String, Object>> countByStatus();
    List<Map<String, Object>> countByLab();
}
