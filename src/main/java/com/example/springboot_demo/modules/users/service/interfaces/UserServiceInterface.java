package com.example.springboot_demo.modules.users.service.interfaces;

import com.example.springboot_demo.modules.users.request.LoginRequest;

public interface UserServiceInterface {
    Object authenticate(LoginRequest request);
}
