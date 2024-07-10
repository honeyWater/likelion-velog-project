package org.example.velogproject.util;

import org.example.velogproject.repository.PostRepository;

import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Pattern;

public class SlugUtils {
    private static final Pattern NONLATIN = Pattern.compile("[^\\w가-힣-]");
    private static final Pattern WHITESPACE = Pattern.compile("\\s");
    private static final Pattern EDGESDHASHES = Pattern.compile("(^-|-$)");

    public static String toSlug(String input){
        // 공백을 하이픈으로 변경
        String noWhiteSpace = WHITESPACE.matcher(input).replaceAll("-");
        // 유니코드 정규화
        String normalized = Normalizer.normalize(noWhiteSpace, Normalizer.Form.NFC);
        // 한글 및 영문, 숫자, 하이픈 이외의 문자 제거
        String slug = NONLATIN.matcher(normalized).replaceAll("");
        // 시작과 끝에 있는 하이픈을 제거
        slug = EDGESDHASHES.matcher(slug).replaceAll("");

        return slug.toLowerCase(Locale.KOREAN);
    }

    public static String generateUniqueSlug(String title, PostRepository postRepository){
        // 입력한 제목을 슬러그로 변환
        String slug = toSlug(title);
        // 초기 슬러그 설정
        String uniqueSlug = slug;
        int count = 1;

        // 슬러그가 유일한지 확인하고, 겹치는 경우 숫자를 추가하여 유일한 슬러그 생성
        while (postRepository.existsBySlug(uniqueSlug)){
            uniqueSlug = slug + "-" + count;
            count++;
        }

        return uniqueSlug;
    }
}
