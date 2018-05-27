package net.kang.main.repository;

import net.kang.main.domain.Detail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DetailRepository extends JpaRepository<Detail, Long> {
    public Optional<Detail> findByNameAndEmail(String name, String email);
    public Optional<Detail> findByInfoUsername(String username);
    public Optional<Detail> findByEmail(String email);
}
