package org.example.velogproject.controller;

import lombok.RequiredArgsConstructor;
import org.example.velogproject.domain.Post;
import org.example.velogproject.dto.PostCardDto;
import org.example.velogproject.service.PostService;
import org.example.velogproject.service.UserService;
import org.example.velogproject.util.UserContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class PostController {
    private final UserService userService;
    private final PostService postService;

    // 메인 페이지 - 월간 트렌딩
    @GetMapping(value = {"/", "/trending/month"})
    public String getHomePageAndMonthlyTrending(Model model) {
        List<PostCardDto> monthlyTrendingPosts = postService.getMonthlyTrendingPosts();

        String userId = UserContext.getUser();
        if (userId != null) {
            model.addAttribute("signedIn", userId);
        }

        model.addAttribute("posts", monthlyTrendingPosts);
        model.addAttribute("trending", "trending");
        model.addAttribute("trendingMonth", "trendingMonth");

        return "main";
    }

    // 메인 페이지 - 주간 트렌딩
    @GetMapping("/trending/week")
    public String getHomePageAndWeeklyTrending(Model model) {
        List<PostCardDto> weeklyTrendingPosts = postService.getWeeklyTrendingPosts();

        String userId = UserContext.getUser();
        if (userId != null) {
            model.addAttribute("signedIn", userId);
        }

        model.addAttribute("posts", weeklyTrendingPosts);
        model.addAttribute("trending", "trending");
        model.addAttribute("trendingMonth", "trendingMonth");

        return "main";
    }

    // 메인 페이지 - 일간 트렌딩
    @GetMapping("/trending/day")
    public String getHomePageAndDailyTrending(Model model) {
        List<PostCardDto> dailyTrendingPosts = postService.getDailyTrendingPosts();

        String userId = UserContext.getUser();
        if (userId != null) {
            model.addAttribute("signedIn", userId);
        }

        model.addAttribute("posts", dailyTrendingPosts);
        model.addAttribute("trending", "trending");
        model.addAttribute("trendingDay", "trendingDay");

        return "main";
    }

    // 메인 페이지 - 연간 트렌딩
    @GetMapping("/trending/year")
    public String getHomePageAndYearlyTrending(Model model) {
        List<PostCardDto> yearlyTrendingPosts = postService.getYearlyTrendingPosts();

        String userId = UserContext.getUser();
        if (userId != null) {
            model.addAttribute("signedIn", userId);
        }

        model.addAttribute("posts", yearlyTrendingPosts);
        model.addAttribute("trending", "trending");
        model.addAttribute("trendingYear", "trendingYear");

        return "main";
    }

    // 메인 페이지 - 최신
    @GetMapping("/recent")
    public String getHomePageAndRecentlyPosted(Model model) {
        List<PostCardDto> recentPosts = postService.getRecentPosts();

        String userId = UserContext.getUser();
        if (userId != null) {
            model.addAttribute("signedIn", userId);
        }

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
