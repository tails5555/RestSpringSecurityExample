package net.kang.main.service;

import net.kang.main.domain.Detail;
import net.kang.main.domain.Info;
import net.kang.main.domain.Role;
import net.kang.main.model.DetailVO;
import net.kang.main.model.NameEmailVO;
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
    public UserVO findByUsername(final String username){
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
    public String findUsername(final NameEmailVO nameEmailVO){
        Optional<Detail> detail = detailRepository.findByNameAndEmail(nameEmailVO.getName(), nameEmailVO.getEmail());
        if(detail.isPresent()){
            Detail tmpDetail = detail.get();
            return tmpDetail.getInfo().getUsername();
        }else{
            return null;
        }
    }

    @Override
    @Transactional
    public boolean update(final String username, final DetailVO detailVO){
        Optional<Info> info = infoRepository.findByUsername(username);
        Optional<Detail> detail = detailRepository.findByInfoUsername(username);
        Optional<Detail> emailDetail = detailRepository.findByEmail(detailVO.getEmail());
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
            if(!emailDetail.isPresent())
                tmpDetail.setEmail(detailVO.getEmail());
            else if(detailVO.getEmail().equals(tmpDetail.getEmail()))
                tmpDetail.setEmail(detailVO.getEmail());
            else
                throw new AuthenticationServiceException("This E-Mail is Existed. Try Again.");
            detailRepository.save(tmpDetail);

            return true;
        }else{
            return false;
        }
    }

    @Override
    @Transactional
    public boolean create(final SignVO signVO){
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

            Optional<Detail> detail = detailRepository.findByEmail(signVO.getEmail());
            if(!detail.isPresent()) {
                Detail newDetail = SignVO.builtByDetail(insertInfo, signVO);
                detailRepository.save(newDetail);
            }else{
                throw new AuthenticationServiceException("E-Mail is Existed. Try Again.");
            }
            return true;
        }
    }

    @Override
    @Transactional
    public boolean delete(final String username){
        Optional<Detail> detail = detailRepository.findByInfoUsername(username);
        if(detail.isPresent()){
            Detail tmpDetail = detail.get();
            detailRepository.delete(tmpDetail);
            infoRepository.deleteByUsername(username);
            return true;
        }else return false;
    }
}
