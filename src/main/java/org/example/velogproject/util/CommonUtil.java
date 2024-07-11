package org.example.velogproject.util;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.springframework.stereotype.Component;

@Component
public class CommonUtil {
    public String markdown(String content){
        Parser parser = Parser.builder().build();
        // 기존 마크다운으로 작성된 틀 파싱
        Node document = parser.parse(content);
        HtmlRenderer renderer = HtmlRenderer.builder().build();

        return renderer.render(document); // HTMl로 렌더링 텍스트 리턴
    }
}
