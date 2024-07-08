package org.example.velogproject.service;

import lombok.RequiredArgsConstructor;
import org.example.velogproject.repository.PostRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MainService {
    private final PostRepository postRepository;


}