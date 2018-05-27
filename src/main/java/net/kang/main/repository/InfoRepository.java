package net.kang.main.repository;

import net.kang.main.domain.Info;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InfoRepository extends JpaRepository<Info, Long> {
    public Optional<Info> findByUsername(String username);
    void deleteByUsername(String username);
}
