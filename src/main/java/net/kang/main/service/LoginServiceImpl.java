package net.kang.main.service;

import net.kang.main.domain.Info;
import net.kang.main.model.UserVO;
import net.kang.main.repository.InfoRepository;
import net.kang.main.util.Encryption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

// LoginService 구현체 클래스
@Service
public class LoginServiceImpl implements LoginService{
    @Autowired InfoRepository infoRepository;

    // 사용자 ID와 비밀번호를 이용한 로그인 기능 구현
    @Override
    public UserVO login(final String username, final String password){
        if(username==null || username.isEmpty()) throw new UsernameNotFoundException("User Name is Null.");
        Optional<Info> tmpInfo = infoRepository.findByUsername(username);
        if(!tmpInfo.isPresent()) throw new UsernameNotFoundException("User Detail is Not Existed.");
        Info info = tmpInfo.get();
        if(!info.getPassword().equals(Encryption.encrypt(password, Encryption.MD5))) throw new UsernameNotFoundException("User Password is Wrong.");
        else return UserVO.buildByInfo(info);
    }
}
