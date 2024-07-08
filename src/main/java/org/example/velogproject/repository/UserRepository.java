package org.example.velogproject.repository;

import org.example.velogproject.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    //    Long countByName(String name);
//    Long countByEmail(String email);
//    Long countByDomain(String username);
    boolean existsById(Long id);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByDomain(String domain);

    User findByUsername(String username);

    User findByEmail(String email);

    Optional<User> findByProviderAndSocialId(String provider, String socialId);
}
