package org.example.velogproject.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.example.velogproject.domain.User;
import org.example.velogproject.dto.BlogUserDto;
import org.example.velogproject.dto.PostCardDto;
import org.example.velogproject.jwt.util.JwtTokenizer;
import org.example.velogproject.service.PostService;
import org.example.velogproject.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class PostController {
    private final JwtTokenizer jwtTokenizer;
    private final UserService userService;
    private final PostService postService;

    // 로그인한 유저를 model 에 추가하는 메서드
    private User addSignedInUserToModel(HttpServletRequest request, Model model) {
        Optional<User> user = jwtTokenizer.getAccessToken(request)
            .map(jwtTokenizer::getUserIdFromToken)
            .flatMap(userService::getUserById);

        if (user.isPresent()) {
            User existedUser = user.get();
            model.addAttribute("signedIn", existedUser);
            return existedUser;
        }

        return null;
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
    public String getHomePageAndRecentlyPosted(HttpServletRequest request, Model model) {
        List<PostCardDto> recentPosts = postService.getRecentPosts();

        // 로그인한 유저인지를 판별
        addSignedInUserToModel(request, model);

        model.addAttribute("posts", recentPosts);
        model.addAttribute("recent", "recent");

        return "main";
    }

    // 메인 페이지 - 피드


    // 사용자 개인 블로그
    @GetMapping(value = {"/@{domain}", "/@{domain}/", "/@{domain}/posts"})
    public String getPersonalPageAndPosts(HttpServletRequest request, Model model, @PathVariable String domain) {
        BlogUserDto userByDomain = userService.getUserByDomain(domain);
        if (userByDomain == null) {
            return "redirect:/error";
        }

        // 로그인한 유저인지를 판별
        User loginUser = addSignedInUserToModel(request, model);

        // 로그인한 유저가 블로그 주인이냐에 따라서 비공개 게시물도 가져와야 함
        Long userByDomainId = userByDomain.getId();
        List<PostCardDto> posts;

        if (loginUser == null || !loginUser.getId().equals(userByDomainId)) {
            posts = postService.getPublishedPostsNotInPrivate(userByDomainId);
        } else {
            posts = postService.getPublishedPostsAlsoInPrivate(userByDomainId);
        }

        model.addAttribute("domain", domain);
        model.addAttribute("posts", posts);
        model.addAttribute("userByDomain", userByDomain);

        return "personal-blog-main";
    }
}
