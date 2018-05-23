package net.kang.main.service;

import net.kang.main.domain.User;
import net.kang.main.repository.UserRepository;
import net.kang.main.util.Encryption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    @Autowired UserRepository userRepository;
    public User login(String loginId, String passwd){
        Optional<User> tmpUser = userRepository.findByLoginId(loginId);
        if(tmpUser.isPresent()){
            User user = tmpUser.get();
            String pw = Encryption.encrypt(passwd, Encryption.MD5);
            if(!user.getPassword().equals(pw)) return null;
            else return user;
        }else return null;
    }
}
