package com.labmanager.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.labmanager.entity.BorrowRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface BorrowRecordMapper extends BaseMapper<BorrowRecord> {

    @Select("SELECT br.*, e.name AS equipment_name, u.real_name AS user_name, l.name AS lab_name " +
            "FROM borrow_record br " +
            "LEFT JOIN equipment e ON br.equipment_id = e.id " +
            "LEFT JOIN user u ON br.user_id = u.id " +
            "LEFT JOIN lab l ON e.lab_id = l.id " +
            "ORDER BY br.create_time DESC")
    List<BorrowRecord> selectAllWithDetails();

    @Select("SELECT br.*, e.name AS equipment_name, u.real_name AS user_name, l.name AS lab_name " +
            "FROM borrow_record br " +
            "LEFT JOIN equipment e ON br.equipment_id = e.id " +
            "LEFT JOIN user u ON br.user_id = u.id " +
            "LEFT JOIN lab l ON e.lab_id = l.id " +
            "WHERE br.status = 'borrowing' AND br.planned_return_time < NOW() " +
            "ORDER BY br.planned_return_time ASC")
    List<BorrowRecord> selectOverdueRecords();
}
