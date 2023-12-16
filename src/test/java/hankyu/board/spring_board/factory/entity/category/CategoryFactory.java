package hankyu.board.spring_board.factory.entity.category;

import hankyu.board.spring_board.domain.category.entity.Category;

public class CategoryFactory {

    public static Category createCategory() {
        return new Category("name");
    }

}
