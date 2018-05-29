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
        // 1단계. 사용자 ID를 기반으로 한 회원 조회 시도. username이 NULL이면 예외 처리를 한다.
        if(username==null || username.isEmpty()) throw new UsernameNotFoundException("User Name is Null.");
        Optional<Info> tmpInfo = infoRepository.findByUsername(username);

        // 2단계. 사용자 인증 정보가 존재하지 않으면 예외 처리를 할 수 있도록 처리한다. 하지만 인증 정보가 존재하면 반환하여 가져온다.
        if(!tmpInfo.isPresent()) throw new UsernameNotFoundException("User Authentication is Not Existed.");
        Info info = tmpInfo.get();

        // 3단계. 비밀번호를 비교하여 일치하지 않으면 예외 처리를 한다. 일치하면 사용자 정보를 반환하여 Authentication 객체에 저장한다.
        if(!info.getPassword().equals(Encryption.encrypt(password, Encryption.MD5))) throw new UsernameNotFoundException("User Password is Wrong.");
        else return UserVO.buildByInfo(info);
    }
}
