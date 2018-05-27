package net.kang.main.service;

import net.kang.main.model.UserVO;

public interface LoginService {
    public UserVO login(String username, String password);
}
