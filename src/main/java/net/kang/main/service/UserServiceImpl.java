package net.kang.main.service;

import net.kang.main.domain.Detail;
import net.kang.main.domain.Info;
import net.kang.main.model.UserVO;
import net.kang.main.repository.DetailRepository;
import net.kang.main.repository.InfoRepository;
import net.kang.main.repository.RoleRepository;
import net.kang.main.util.Encryption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    @Autowired InfoRepository infoRepository;
    @Autowired DetailRepository detailRepository;
    @Autowired RoleRepository roleRepository;

    public UserVO findByUsername(String username){
        Optional<Info> tmpInfo =  infoRepository.findByUsername(username);
        if(tmpInfo.isPresent()){
            Info info = tmpInfo.get();
            return UserVO.buildByInfo(info);
        }else{
            return null;
        }
    }

    public List<UserVO> findAll(){
        List<Info> infos = infoRepository.findAll();
        List<UserVO> userVOList = new ArrayList<>();
        for(Info info : infos){
            userVOList.add(UserVO.buildByInfo(info));
        }
        return userVOList;
    }

    public UserVO login(String username, String password){
        if(username==null || username.isEmpty()) throw new UsernameNotFoundException("User Name is Null.");
        Optional<Info> tmpInfo = infoRepository.findByUsername(username);
        if(!tmpInfo.isPresent()) throw new UsernameNotFoundException("User Detail is Not Existed.");
        Info info = tmpInfo.get();
        if(!info.getPassword().equals(Encryption.encrypt(password, Encryption.MD5))) throw new UsernameNotFoundException("User Password is Wrong.");
        else return UserVO.buildByInfo(info);
    }
}
