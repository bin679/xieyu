package com.labmanager.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.labmanager.dto.LoginDTO;
import com.labmanager.dto.RegisterDTO;
import com.labmanager.entity.User;

public interface UserService extends IService<User> {
    User login(LoginDTO loginDTO);
    User register(RegisterDTO registerDTO);
    Page<User> getUserPage(Integer current, Integer size, String keyword);
}
