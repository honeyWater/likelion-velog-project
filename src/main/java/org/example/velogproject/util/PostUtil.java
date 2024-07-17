package org.example.velogproject.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
public class PostUtil {

    @Value("${app.upload.dir}")
    private String uploadDir;

    // 게시글 설명 생성 메서드
    public String generateDescription(String content) {
        if (content.length() > 147) {
            return content.substring(0, 147) + "...";
        }
        return content;
    }

    // 임시저장 후 redirectUrl 세팅
    public String setRedirectUrl(String str, Long postId) {
        if (str.equals("justTemp")) {
            return "/write?id=" + postId;
        } else if (str.equals("forPublish")) {
            return "/publish?id=" + postId;
        }

        return "/error";
    }

    // 파일 이름 생성 메서드
    public String generateFileName(String originalFilename) {
        return UUID.randomUUID() + "_" + originalFilename;
    }

    // 파일 저장 메서드
    public void saveFile(MultipartFile file, String fileName, String subDir) throws IOException {
        String filePath = uploadDir + subDir + fileName;
        File destination = new File(filePath);
        file.transferTo(destination);
    }

    // 썸네일 업로드 처리 메서드
    public String uploadThumbnail(MultipartFile file) throws IOException {
        String fileName = generateFileName(file.getOriginalFilename());
        saveFile(file, fileName, "thumbnail_image/");
        return fileName;
    }

    // 게시글 내용에서 이미지 경로 추출
    public List<String> extractImagePaths(String content) {
        List<String> imagePaths = new ArrayList<>();

        // 정규표현식 패턴: ![image](/post_image/파일명.확장자)
        Pattern pattern = Pattern.compile("!\\[image]\\(/post_image/([^\\)]+)\\)");
        Matcher matcher = pattern.matcher(content);

        while (matcher.find()) {
            imagePaths.add(matcher.group(1));
        }

        return imagePaths;
    }

    // 이미지 파일 삭제 메서드
    public void deleteImageFile(String filePath) {
        File file = new File(uploadDir + filePath);
        if (file.exists()) {
            boolean fileDeleted = file.delete();
            if (fileDeleted) {
                log.info("File deleted: {}", filePath);
            } else {
                log.info("Failed to delete file: {}", filePath);
            }
        } else {
            log.info("File not found: {}", filePath);
        }
    }
}
