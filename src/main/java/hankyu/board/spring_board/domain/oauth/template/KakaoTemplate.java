package hankyu.board.spring_board.domain.oauth.template;

import lombok.Builder;
import org.springframework.beans.factory.annotation.Value;

public class KakaoTemplate {

    @Value("elastic.ip.address")
    private static String address;

    @Builder
    public static class Feed {
        @Builder.Default //초기화 표현식을 기본값으로 설정 추가
        private String object_type = "feed";

        // private Object item_content;
        private Content content;
        // private Social social;
        private String button_title;
        private Button[] buttons;
    }

    @Builder
    public static class Text {
        @Builder.Default
        private String object_type = "text";

        private String text;
        private Link link = Link.builder().build();
        private String button_title;
    }

    @Builder
    public static class Location {
        @Builder.Default
        private String object_type = "location";

        private String address;
        private String address_title;
        private Content content;
    }

    // 공용

    @Builder
    public static class Content {
        private String title;
        private String description;

        @Builder.Default
        private int image_width = 600;

        @Builder.Default
        private int image_height = 400;

        @Builder.Default
        private String image_url = "";

        @Builder.Default
        private Link link = Link.builder().build();
    }

    @Builder
    public static class Link {
        @Builder.Default
        private String web_url = "http://"+address+":8080/swagger-ui/index.html";

        @Builder.Default
        private String mobile_web_url = "http://"+address+":8080/swagger-ui/index.html";

        // private String android_execution_params = "";
        // private String ios_execution_params = "";
    }

    @Builder
    public static class Button {
        private String title;
        private Link link;
    }

    public static class Social {
    }
}
