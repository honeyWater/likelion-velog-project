package org.example.velogproject.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.velogproject.domain.Comment;
import org.example.velogproject.domain.Post;
import org.example.velogproject.domain.User;
import org.example.velogproject.dto.BlogUserDto;
import org.example.velogproject.dto.PostCardDto;
import org.example.velogproject.dto.PostPublishDto;
import org.example.velogproject.jwt.util.JwtTokenizer;
import org.example.velogproject.service.PostService;
import org.example.velogproject.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Slf4j
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

        User existedUser = null;
        if (user.isPresent()) {
            existedUser = user.get();
        }

        model.addAttribute("signedIn", existedUser);
        return existedUser;
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

    // 게시글 상세
    @GetMapping("/@{domain}/{slug}")
    public String getPostDetail(@PathVariable String domain, @PathVariable String slug,
                                HttpServletRequest request, Model model) {
        BlogUserDto userByDomain = userService.getUserByDomain(domain);
        Optional<Post> postBySlug = postService.findBySlug(slug);

        if (userByDomain == null || postBySlug.isEmpty() ||
            !slug.equals(postBySlug.get().getSlug())) {
            return "error";
        }
        Post post = postBySlug.get();

//        Comment comment = (Comment) post.getComments();

        // 로그인한 유저인지를 판별
        User loginUser = addSignedInUserToModel(request, model);
        // 해당 게시글의 주인이 로그인한 유저라면 수정, 삭제가 가능하도록
        if (loginUser != null && loginUser.getId().equals(userByDomain.getId())) {
            model.addAttribute("sameUser", "sameUser");
        }

        model.addAttribute("userByDomain", userByDomain);
        model.addAttribute("post", post);

        return "post-detail";
    }

    // 게시글 작성 및 수정 - 권한이 있어야 이용 가능 (permitAll X)
    @GetMapping("/write")
    public String getWriteForm(@RequestParam(name = "id", required = false) Long id, Model model) {
        if (id == null) {
            // 새 게시글 작성
            model.addAttribute("post", new Post());
        } else {
            // 기존 게시글 수정
            Optional<Post> existedPost = postService.getPostById(id);
            if (existedPost.isPresent()) {
                Post post = existedPost.get();
                model.addAttribute("post", post);
            } else {
                // 게시글이 존재하지 않을 경우 에러 페이지 처리
                return "redirect:/error";
            }
        }
        return "write-form";
    }

    // 게시글 작성 후 출간 폼 반환
    @GetMapping("/publish")
    public String getPublishForm(@RequestParam(name = "id") Long id, Model model) {
        Optional<Post> post = postService.getPostById(id);

        post.ifPresent(value -> model.addAttribute("domain", value.getUser().getDomain()));
        model.addAttribute("post", postService.getPostPublishDtoById(id));
        return "publish-form";
    }
}
