package org.example.velogproject.controller;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.example.velogproject.domain.User;
import org.example.velogproject.dto.PostCardDto;
import org.example.velogproject.jwt.util.JwtTokenizer;
import org.example.velogproject.service.PostService;
import org.example.velogproject.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class PostController {
    private final JwtTokenizer jwtTokenizer;
    private final UserService userService;
    private final PostService postService;

    // 로그인한 유저의 프로필 이미지를 model 에 추가하는 메서드
    private void addSignedInUserToModel(HttpServletRequest request, Model model) {
        jwtTokenizer.getAccessToken(request)
            .map(jwtTokenizer::getUserIdFromToken)
            .flatMap(userService::getUserById)
            .ifPresent(user -> model.addAttribute("signedIn", user.getProfileImage()));
    }

    // 기간(period)에 따른 트렌딩 처리
    @GetMapping(value = {"/", "/trending/{period}"})
    public String getHomePageAndTrending(@PathVariable(required = false) String period,
                                         HttpServletRequest request, Model model) {
        if (period == null) {
            period = "month"; // 기본값은 월간으로
        }

        List<PostCardDto> trendingPosts;
        String trendingPeriod;

        switch (period) {
            case "day":
                trendingPosts = postService.getDailyTrendingPosts();
                trendingPeriod = "오늘";
                break;
            case "week":
                trendingPosts = postService.getWeeklyTrendingPosts();
                trendingPeriod = "이번 주";
                break;
            case "year":
                trendingPosts = postService.getYearlyTrendingPosts();
                trendingPeriod = "올해";
                break;
            default: // "month" 또는 기타 경우
                trendingPosts = postService.getMonthlyTrendingPosts();
                trendingPeriod = "이번 달";
                break;
        }

        // 로그인한 유저인지를 판별
        addSignedInUserToModel(request, model);

        model.addAttribute("posts", trendingPosts);
        model.addAttribute("trendingPeriod", trendingPeriod);

        return "main";
    }

    // 메인 페이지 - 최신
    @GetMapping("/recent")
    public String getHomePageAndRecentlyPosted(Model model) {
        List<PostCardDto> recentPosts = postService.getRecentPosts();

        // 로그인 상태를 확인하는 코드 필요
//        model.addAttribute("signedIn", userId);

        model.addAttribute("posts", recentPosts);
        model.addAttribute("recent", "recent");

        return "main";
    }

    // 메인 페이지 - 피드


    // 사용자 개인 블로그
    @GetMapping(value = {"/@{domain}", "/@{domain}/posts"})
    public String getPersonalPageAndPosts(Model model, @PathVariable String domain) {


        return "personal-blog-main";
    }
}
