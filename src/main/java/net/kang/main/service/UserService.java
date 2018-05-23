package net.kang.main.service;

import net.kang.main.domain.User;

import java.util.Optional;

public interface UserService {
    public User login(String loginId, String passwd);
}
