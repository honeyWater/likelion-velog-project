package org.example.velogproject.repository;

import org.example.velogproject.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
//    Long countByName(String name);
//    Long countByEmail(String email);
//    Long countByDomain(String username);
    boolean existsById(Long id);
    boolean existsByName(String name);
    boolean existsByEmail(String email);
    boolean existsByDomain(String domain);
    User findByEmail(String email);
}
