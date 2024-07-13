package org.example.velogproject.util;

import org.commonmark.node.AbstractVisitor;
import org.commonmark.node.Image;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.text.TextContentRenderer;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Component;

@Component
public class CommonUtil {

    // content 에서 HTML 문법 및 마크다운을 제거하고 150 자로 자른 문자열을 반환
    public String removeHtmlAndMarkdown(String content) {
        Parser parser = Parser.builder().build();
        Node document = parser.parse(content);

        // 이미지 태그 제거
        document.accept(new AbstractVisitor() {
            @Override
            public void visit(Image image) {
                // 부모 노드에서 이미지 노드를 제거한다.
                if (image.getParent() != null) {
                    image.unlink();
                }
            }
        });

        // 마크다운을 텍스트로 변환
        TextContentRenderer renderer = TextContentRenderer.builder().build();
        String markdownToText = renderer.render(document);

        // HTML 태그 제거
        String plainTextContent = Jsoup.parse(markdownToText).text();

        // 150 자로 자르기
        if (plainTextContent.length() > 147) {
            return plainTextContent.substring(0, 147) + "...";
        } else {
            return plainTextContent;
        }
    }
}
