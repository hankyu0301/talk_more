package hankyu.board.spring_board.factory.dto.category;

import hankyu.board.spring_board.dto.category.CategoryCreateRequest;

public class CategoryCreateRequestFactory {

    public static CategoryCreateRequest createCategoryCreateRequest() {
        return new CategoryCreateRequest("category", null);
    }
}
