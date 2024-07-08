package org.example.velogproject.controller;

import lombok.RequiredArgsConstructor;
import org.example.velogproject.service.MainService;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class MainController {
    private final MainService mainService;


}
