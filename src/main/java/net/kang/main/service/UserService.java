package net.kang.main.service;

import net.kang.main.domain.Role;
import net.kang.main.model.DetailVO;
import net.kang.main.model.NameEmailVO;
import net.kang.main.model.SignVO;
import net.kang.main.model.UserVO;

import java.util.List;
import java.util.Map;

// UserService 인터페이스
public interface UserService {
    public UserVO findByUsername(String username);
    public List<UserVO> findForSameLayers(String username);
    public List<UserVO> findAll();
    public String findUsername(NameEmailVO nameEmailVO);
    public boolean update(String username, DetailVO detailVO);
    public boolean roleUpdate(String username, String role, boolean plus);
    public boolean create(SignVO signVO);
    public boolean delete(String username);
    public boolean deleteForManager(String username);
    public Map<Role, Long> countWithManagerAndUser();
    public Map<Role, Long> countWithAll();
    public long count();
}
