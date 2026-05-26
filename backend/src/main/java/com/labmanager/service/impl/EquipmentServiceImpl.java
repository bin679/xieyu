package com.labmanager.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.labmanager.entity.Equipment;
import com.labmanager.mapper.EquipmentMapper;
import com.labmanager.service.EquipmentService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class EquipmentServiceImpl extends ServiceImpl<EquipmentMapper, Equipment> implements EquipmentService {

    @Override
    public List<Equipment> listWithLab() {
        return baseMapper.selectAllWithLab();
    }

    @Override
    public List<Map<String, Object>> countByStatus() {
        return baseMapper.countByStatus();
    }

    @Override
    public List<Map<String, Object>> countByLab() {
        return baseMapper.countByLab();
    }
}
