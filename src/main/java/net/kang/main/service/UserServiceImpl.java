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
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

// UserService 구현체 클래스
@Service
public class UserServiceImpl implements UserService {
    @Autowired InfoRepository infoRepository;
    @Autowired DetailRepository detailRepository;
    @Autowired RoleRepository roleRepository;

    // 회원 ID를 이용해서 회원의 모든 정보를 반환한다.
    @Override
    public UserVO findByUsername(final String username){
        Optional<Info> tmpInfo =  infoRepository.findByUsername(username);

        // 사용자 정보가 존재한다면 UserVO로 만들어서 반환한다.
        if(tmpInfo.isPresent()){
            Info info = tmpInfo.get();
            return UserVO.buildByInfo(info);
        }

        // 사용자 정보가 없다면 null을 반환해서 NOT FOUND Status로 나오게 한다.
        else{
            return null;
        }
    }

    // 동급의 회원 세부 정보를 반환한다.
    @Override
    public List<UserVO> findForSameLayers(final String username){
        Optional<Info> tmpInfo =  infoRepository.findByUsername(username);
        List<UserVO> userVOList = new ArrayList<UserVO>();

        // 사용자 정보가 존재한다면 같은 권한이 포함된 UserVO 목록으로 만들어서 반환한다.
        if(tmpInfo.isPresent()){
            Info info = tmpInfo.get();
            for(Info i : infoRepository.findByRolesContains(info.getRoles())){
                userVOList.add(UserVO.buildByInfo(i));
            }
        }

        return userVOList;
    }

    // 모든 회원의 개인 정보를 반환한다.
    @Override
    public List<UserVO> findAll(){
        List<Info> infos = infoRepository.findAll();

        // 모든 회원의 개인 정보를 UserVO 목록으로 가공할 수 있는 ArrayList를 생성한다.
        List<UserVO> userVOList = new ArrayList<>();

        // 각 회원의 정보를 UserVO로 구축해서 재구성하고 반환한다.
        for(Info info : infos){
            userVOList.add(UserVO.buildByInfo(info));
        }
        return userVOList;
    }

    // 회원 이름과 이메일 정보를 이용해서 존재하는 회원의 ID를 반환한다.
    @Override
    public String findUsername(final NameEmailVO nameEmailVO){
        Optional<Detail> detail = detailRepository.findByNameAndEmail(nameEmailVO.getName(), nameEmailVO.getEmail());

        // 회원 이름과 E-Mail을 통한 세부 정보가 존재한다면 그 회원의 ID를 반환한다.
        if(detail.isPresent()){
            Detail tmpDetail = detail.get();
            return tmpDetail.getInfo().getUsername();
        }

        // 세부 정보가 없다면 null을 반환해서 NOT FOUND Status로 나오게 한다.
        else{
            return null;
        }
    }

    // 회원의 모든 정보를 수정한다.
    @Override
    @Transactional
    public boolean update(final String username, final DetailVO detailVO){
        // 이는 회원의 ID를 이용해서 회원 인증 정보 존재 여부를 파악하기 위한 목적과 비밀번호 변경을 위해 불러오는 역할을 한다.
        Optional<Info> info = infoRepository.findByUsername(username);

        // 이는 회원의 ID를 이용해서 세부 정보 존재 여부를 파악하기 위한 목적과 세부 정보 변경을 위해 불러오는 역할을 한다.
        Optional<Detail> detail = detailRepository.findByInfoUsername(username);

        // 이는 입력한 E-Mail의 중복 여부를 확인하기 위해 작성하였다.
        Optional<Detail> emailDetail = detailRepository.findByEmail(detailVO.getEmail());

        // 사용자 세부 정보와 로그인 정보가 존재하는지 확인한다.
        // 세부 정보의 존재와 로그인 정보가 이중으로 존재하는지 확인한다.
        if(detail.isPresent() && info.isPresent()){
            // 1단계. 이전 비밀번호를 확인한다. 올바르게 확인되었으면 변경을 하고, 아니면 인증 예외를 던진다.
            Info tmpInfo = info.get();

            // 현재 회원의 비밀번호와 비교하여 확인을 하고 난 후에 새로운 비밀번호를 변경할 수 있게 한다.
            if(tmpInfo.getPassword().equals(Encryption.encrypt(detailVO.getBeforePassword(), Encryption.MD5)))
                tmpInfo.setPassword(Encryption.encrypt(detailVO.getNewPassword(), Encryption.MD5));
            else // 비밀번호가 일치하지 않으면 예외를 던진다.
                throw new AuthenticationServiceException("Before Password Is Wrong.");

            // 비밀번호 변경이 올바르게 되었으면 갱신한다.
            infoRepository.save(tmpInfo);

            // 2단계. 사용자 정보를 갱신한다.
            Detail tmpDetail = detail.get();

            // 각 사용자의 주소와 생일을 갱신한다.
            tmpDetail.setAddress(detailVO.getAddress());
            tmpDetail.setBirthday(detailVO.getBirthday());

            // 만일 E-Mail 중복 여부를 확인해서 존재하지 않으면 변경할 수 있게 하고, 타 회원이 가진 E-Mail이면 변경을 못 하게 한다.
            if(!emailDetail.isPresent())
                tmpDetail.setEmail(detailVO.getEmail());
            else if(detailVO.getEmail().equals(tmpDetail.getEmail())) // 설령 같은 이메일을 주고 받으면 변경하는데 지장은 없게 한다.
                tmpDetail.setEmail(detailVO.getEmail());
            else
                throw new AuthenticationServiceException("This E-Mail is Existed. Try Again.");

            // 사용자 세부 정보를 갱신한다.
            detailRepository.save(tmpDetail);

            // true를 반환하여 MODIFIED 상태로 바뀌게 한다.
            return true;
        }else{
            // 사용자 정보가 존재하지 않으면 false를 반환하여 NOT_FOUND 상태로 바뀌게 한다.
            return false;
        }
    }

    @Override
    public boolean roleUpdate(String username, String role, boolean isPlus){
        Optional<Info> info =  infoRepository.findByUsername(username);
        Optional<Role> tmpRole = roleRepository.findByName(role);
        if(!tmpRole.isPresent())
            throw new AuthenticationServiceException("Invalid Roles! Try Again!");
        if(info.isPresent()) {
            Info tmpInfo = info.get();
            List<Role> roles = tmpInfo.getRoles();
            if(isPlus){
                Role addRole = tmpRole.get();
                if(!roles.contains(addRole))
                    roles.add(addRole);
                tmpInfo.setRoles(roles);
                infoRepository.save(tmpInfo);
            }else {
                Role removeRole = tmpRole.get();
                if (roles.contains(removeRole))
                    roles.remove(removeRole);
                tmpInfo.setRoles(roles);
                infoRepository.save(tmpInfo);
            }
            return true;
        }else return false;
    }

    // 회원 가입 정보를 추가한다.
    @Override
    @Transactional
    public boolean create(final SignVO signVO){
        // 초기 회원은 일반 USER로 설정하게 한다.
        Optional<Role> userRole = roleRepository.findByName("USER");
        List<Role> role = Arrays.asList(userRole.get());

        // 가입하려는 ID의 중복을 확인하기 위하여 입력한 회원 인증 정보로 확인 진행을 한다.
        Optional<Info> info = infoRepository.findByUsername(signVO.getUsername());

        if(info.isPresent()){ // 만일 사용자가 존재한다면 예외 처리를 한다.
            throw new AuthenticationServiceException("Username is Existed. Try Again.");
        }else{
            // 사용자 정보가 존재하지 않으면 회원 등록을 시작한다.
            Info newInfo = new Info();
            newInfo.setUsername(signVO.getUsername());

            // 비밀번호 일치 확인 진행 뒤에 일치하면 비밀번호 암호화를 시작한다.
            if(signVO.getPassword_1().equals(signVO.getPassword_2())){
                newInfo.setPassword(Encryption.encrypt(signVO.getPassword_1(), Encryption.MD5));
            }

            // 비밀번호가 일치하지 않으면 false를 반환하여 변경 불가능을 알린다.
            else{
                return false;
            }

            // 새로운 회원 인증 정보에 USER 권한을 가지게끔 설정한다.
            newInfo.setRoles(role);

            // 현재 추가된 회원 인증 정보를 받아서 이는 회원 세부 정보에서 추가할 때 사용하도록 한다.
            Info insertInfo = infoRepository.save(newInfo);

            // E-Mail을 이용한 세부 정보 존재 여부를 확인 진행한다.
            Optional<Detail> detail = detailRepository.findByEmail(signVO.getEmail());

            // 현재 세부 정보에 중복된 E-Mail이 존재하지 않으면 새로운 정보를 저장할 수 있도록 설정한다.
            if(!detail.isPresent()) {
                Detail newDetail = SignVO.builtByDetail(insertInfo, signVO);
                detailRepository.save(newDetail);
            }

            // E-Mail이 존재한다면 예외 처리를 한다.
            else{
                throw new AuthenticationServiceException("E-Mail is Existed. Try Again.");
            }
            return true;
        }
    }

    // 회원 모든 정보를 삭제한다.
    @Override
    @Transactional
    public boolean delete(final String username){
        Optional<Detail> detail = detailRepository.findByInfoUsername(username);

        // 회원 세부 정보가 존재하는 경우에는 삭제 작업을 실행한다.
        if(detail.isPresent()){
            // 세부 정보를 가져와서 이를 삭제하고 난 후에 username을 이용해 회원 인증 정보를 삭제한다.
            Detail tmpDetail = detail.get();
            detailRepository.delete(tmpDetail);
            infoRepository.deleteByUsername(username);
            return true;
        }else return false;
    }

    // 회원 ID로 강제 탈퇴하는 기능을 구현한다. 이 기능은 MANAGER에게 부여되는 기능인데 MANAGER는 USER 권한만 가진 사람을 강퇴한다.
    @Override
    @Transactional
    public boolean deleteForManager(final String username){
        Optional<Detail> detail = detailRepository.findByInfoUsername(username);
        if(detail.isPresent()){
            Detail tmpDetail = detail.get();
            List<Role> roles = tmpDetail.getInfo().getRoles();
            boolean isDeleted = false;
            for(Role role : roles){
                if(role.getName().equals("USER") && roles.size()==1) isDeleted = true;
            }
            if(isDeleted) {
                detailRepository.delete(tmpDetail);
                infoRepository.deleteByUsername(username);
            }
            return true;
        }else return false;
    }

    // 매니저가 일반 사용자와 매니저의 수를 파악할 수 있도록 하는 기능을 추가하였다.
    @Override
    public Map<Role, Long> countWithManagerAndUser(){
        Map<Role, Long> result = new HashMap<>();
        List<Role> roles = roleRepository.findAll();
        for(Role role : roles){
            if(role.getName().equals("USER") || role.getName().equals("MANAGER")){
                result.put(role, infoRepository.countByRolesContains(role));
            }
        }
        return result;
    }

    // 관리자가 권한 별 모든 회원의 수를 파악할 수 있는 기능을 추가하였다.
    @Override
    public Map<Role, Long> countWithAll(){
        Map<Role, Long> result = new HashMap<>();
        List<Role> roles = roleRepository.findAll();
        for(Role role : roles){
            result.put(role, infoRepository.countByRolesContains(role));
        }
        return result;
    }

    @Override
    public long count(){
        return infoRepository.count();
    }
}
