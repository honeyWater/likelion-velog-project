package org.example.velogproject.repository;

import org.example.velogproject.domain.Tag;
import org.example.velogproject.dto.TagGroupDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TagRepository extends JpaRepository<Tag, Long> {

    @Query("""
        select NEW org.example.velogproject.dto.TagGroupDto(t.tagName, COUNT(t))
        from Tag t
        where t.user.id = :userId
        group by t.tagName
        order by COUNT(t) desc
        """)
    List<TagGroupDto> findTagCountByUserId(@Param("userId") Long userId);
}
