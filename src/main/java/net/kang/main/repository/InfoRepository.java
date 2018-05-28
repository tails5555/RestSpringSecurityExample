package net.kang.main.repository;

import net.kang.main.domain.Info;
import net.kang.main.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InfoRepository extends JpaRepository<Info, Long> {
    public List<Info> findByRolesContains(List<Role> roles);
    public Optional<Info> findByUsername(String username);
    public void deleteByUsername(String username);
    public long countByRolesContains(Role role);
}
