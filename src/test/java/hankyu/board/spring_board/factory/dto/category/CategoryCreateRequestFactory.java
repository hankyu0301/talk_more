package hankyu.board.spring_board.factory.dto.category;

import hankyu.board.spring_board.domain.category.dto.CategoryCreateRequest;

public class CategoryCreateRequestFactory {

    public static CategoryCreateRequest createCategoryCreateRequest() {
        return new CategoryCreateRequest("category");
    }
}
