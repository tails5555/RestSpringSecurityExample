package net.kang.main.service;

import net.kang.main.domain.Role;
import net.kang.main.model.DetailVO;
import net.kang.main.model.NameEmailVO;
import net.kang.main.model.SignVO;
import net.kang.main.model.UserVO;
import org.springframework.security.access.annotation.Secured;

import javax.servlet.ServletException;
import java.util.List;
import java.util.Map;

// UserService 인터페이스
public interface UserService {
    public UserVO findByUsername(String username);
    public List<UserVO> findForSameLayers(String username);
    public List<UserVO> findAll();
    public String findByNameAndEmail(NameEmailVO nameEmailVO);
    public boolean update(String username, DetailVO detailVO) throws ServletException;
    public boolean roleUpdate(String username, String role, boolean plus) throws ServletException;
    public boolean create(SignVO signVO) throws ServletException;
    public boolean delete(String username);
    public boolean deleteForManager(String username);
    public Map<Role, Long> countWithManagerAndUser();
    public Map<Role, Long> countWithAll();
    public long count();
}
