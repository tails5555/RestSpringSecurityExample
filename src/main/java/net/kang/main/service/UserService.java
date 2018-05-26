package net.kang.main.service;

import net.kang.main.model.DetailVO;
import net.kang.main.model.SignVO;
import net.kang.main.model.UserVO;

import java.util.List;

public interface UserService {
    public UserVO findByUsername(String username);
    public List<UserVO> findAll();
    public UserVO login(String username, String password);
    public boolean update(String username, DetailVO detailVO);
    public boolean create(SignVO signVO);
}
