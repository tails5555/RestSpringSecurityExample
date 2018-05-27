package net.kang.main.service;

import net.kang.main.domain.Role;
import net.kang.main.model.DetailVO;
import net.kang.main.model.NameEmailVO;
import net.kang.main.model.SignVO;
import net.kang.main.model.UserVO;

import java.util.List;
import java.util.Map;

public interface UserService {
    public UserVO findByUsername(String username);
    public List<UserVO> findAll();
    public String findUsername(NameEmailVO nameEmailVO);
    public boolean update(String username, DetailVO detailVO);
    public boolean create(SignVO signVO);
    public boolean delete(String username);
    public boolean deleteForManager(String username);
    public Map<Role, Long> countWithManagerAndUser();
}
