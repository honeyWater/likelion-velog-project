package org.example.velogproject.service;

import lombok.RequiredArgsConstructor;
import org.example.velogproject.dto.TagGroupDto;
import org.example.velogproject.repository.TagRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TagService {
    private final TagRepository tagRepository;

    @Transactional(readOnly = true)
    public List<TagGroupDto> getTagsByUserId(Long userId){
        return tagRepository.findTagCountByUserId(userId);
    }
}
