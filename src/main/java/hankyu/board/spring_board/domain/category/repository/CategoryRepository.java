package hankyu.board.spring_board.domain.category.repository;

import hankyu.board.spring_board.domain.category.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {

}
