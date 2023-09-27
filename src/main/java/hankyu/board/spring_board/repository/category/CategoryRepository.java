package hankyu.board.spring_board.repository.category;

import hankyu.board.spring_board.entity.category.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {

}
