package hankyu.board.spring_board.factory.entity.category;

import hankyu.board.spring_board.entity.category.Category;

public class CategoryFactory {

    public static Category createCategory() {
        return new Category("name", null);
    }

}
