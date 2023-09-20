package hankyu.board.spring_board.helper;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HierarchyConverterTest {

    private static class MyEntity {
        private Long id;
        private String name;
        private Long parentId;

        public MyEntity(Long id, String name, Long parentId) {
            this.id = id;
            this.name = name;
            this.parentId = parentId;
        }

        public Long getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public Long getParentId() {
            return parentId;
        }
    }

    private static class MyDto {
        private Long id;
        private String name;
        private List<MyDto> children;

        public MyDto(Long id, String name) {
            this.id = id;
            this.name = name;
            this.children = new ArrayList<>();
        }

        public Long getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public List<MyDto> getChildren() {
            return children;
        }
    }

    @Test
    void testConvertToHierarchy() {
        // 테스트용 데이터를 생성합니다.
        List<MyEntity> entities = List.of(
                new MyEntity(1L, "Entity 1", null),
                new MyEntity(2L, "Entity 2", 1L),
                new MyEntity(3L, "Entity 3", 1L),
                new MyEntity(4L, "Entity 4", 2L),
                new MyEntity(5L, "Entity 5", 2L),
                new MyEntity(6L, "Entity 6", 3L)
        );

        HierarchyConverter converter = HierarchyConverter.create(
                entities,
                e -> new MyDto(e.getId(), e.getName()),
                MyEntity::getId,
                MyEntity::getParentId,
                MyDto::getChildren
        );

        // Hierarchy를 생성합니다.
        List<MyDto> hierarchy = converter.convertToHierarchy();

        // 검증을 수행합니다.
        assertEquals(1, hierarchy.size());
        MyDto root = hierarchy.get(0);
        assertEquals(2, root.getChildren().size());
        MyDto child1 = root.getChildren().get(0);
        assertEquals(2, child1.getChildren().size());
        MyDto child2 = root.getChildren().get(1);
        assertEquals(1, child2.getChildren().size());
    }
}
