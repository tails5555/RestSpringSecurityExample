package net.kang.main.service;

import net.kang.main.domain.Detail;
import net.kang.main.domain.Info;
import net.kang.main.domain.Role;
import net.kang.main.model.DetailVO;
import net.kang.main.model.SignVO;
import net.kang.main.model.UserVO;
import net.kang.main.repository.DetailRepository;
import net.kang.main.repository.InfoRepository;
import net.kang.main.repository.RoleRepository;
import net.kang.main.util.Encryption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    @Autowired InfoRepository infoRepository;
    @Autowired DetailRepository detailRepository;
    @Autowired RoleRepository roleRepository;

    @Override
    public UserVO findByUsername(String username){
        Optional<Info> tmpInfo =  infoRepository.findByUsername(username);
        if(tmpInfo.isPresent()){
            Info info = tmpInfo.get();
            return UserVO.buildByInfo(info);
        }else{
            return null;
        }
    }

    @Override
    public List<UserVO> findAll(){
        List<Info> infos = infoRepository.findAll();
        List<UserVO> userVOList = new ArrayList<>();
        for(Info info : infos){
            userVOList.add(UserVO.buildByInfo(info));
        }
        return userVOList;
    }

    @Override
    public UserVO login(String username, String password){
        if(username==null || username.isEmpty()) throw new UsernameNotFoundException("User Name is Null.");
        Optional<Info> tmpInfo = infoRepository.findByUsername(username);
        if(!tmpInfo.isPresent()) throw new UsernameNotFoundException("User Detail is Not Existed.");
        Info info = tmpInfo.get();
        if(!info.getPassword().equals(Encryption.encrypt(password, Encryption.MD5))) throw new UsernameNotFoundException("User Password is Wrong.");
        else return UserVO.buildByInfo(info);
    }

    @Override
    @Transactional
    public boolean update(String username, DetailVO detailVO){
        Optional<Info> info = infoRepository.findByUsername(username);
        Optional<Detail> detail = detailRepository.findByInfoUsername(username);
        // 사용자 정보와 로그인 정보가 존재하는지 확인한다.
        if(detail.isPresent() && info.isPresent()){
            // 1단계. 이전 비밀번호를 확인한다. 올바르게 확인되었으면 변경을 하고, 아니면 인증 예외를 던진다.
            Info tmpInfo = info.get();
            if(tmpInfo.getPassword().equals(Encryption.encrypt(detailVO.getBeforePassword(), Encryption.MD5)))
                tmpInfo.setPassword(Encryption.encrypt(detailVO.getNewPassword(), Encryption.MD5));
            else
                throw new AuthenticationServiceException("Before Password Is Wrong.");
            infoRepository.save(tmpInfo);

            // 2단계. 사용자 정보를 갱신한다.
            Detail tmpDetail = detail.get();
            tmpDetail.setAddress(detailVO.getAddress());
            tmpDetail.setBirthday(detailVO.getBirthday());
            tmpDetail.setEmail(detailVO.getEmail());
            detailRepository.save(tmpDetail);

            return true;
        }else{
            return false;
        }
    }

    @Override
    @Transactional
    public boolean create(SignVO signVO){
        Optional<Role> userRole = roleRepository.findByName("USER");
        List<Role> role = Arrays.asList(userRole.get());

        Optional<Info> info = infoRepository.findByUsername(signVO.getUsername());
        if(info.isPresent()){ // 만일 사용자가 존재한다면 false를 반환한다.
            throw new AuthenticationServiceException("Username is Existed. Try Again.");
        }else{
            Info newInfo = new Info();
            newInfo.setUsername(signVO.getUsername());
            if(signVO.getPassword_1().equals(signVO.getPassword_2())){
                newInfo.setPassword(Encryption.encrypt(signVO.getPassword_1(), Encryption.MD5));
            }else{
                return false;
            }
            newInfo.setRoles(role);
            Info insertInfo = infoRepository.save(newInfo);

            Detail detail = SignVO.builtByDetail(insertInfo, signVO);
            detailRepository.save(detail);

            return true;
        }
    }
}
