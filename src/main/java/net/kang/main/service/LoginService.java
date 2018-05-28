package net.kang.main.service;

import net.kang.main.model.UserVO;

// LoginService 인터페이스
public interface LoginService {
    public UserVO login(String username, String password);
}
