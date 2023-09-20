package hankyu.board.spring_board.helper;

import hankyu.board.spring_board.exception.helper.CannotConvertHelperException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class HierarchyConverter<E, D> {
    private List<E> entities;
    private Function<E, D> toDto;
    private Function<E, Long> getId;
    private Function<E, Long> getParentId;
    private Function<D, List<D>> getChildren;

    public static <E,D> HierarchyConverter<E, D> create(List<E> entities, Function<E, D> toDto, Function<E, Long> getId, Function<E, Long> getParentId, Function<D, List<D>> getChildren) {
        return new HierarchyConverter<>(entities, toDto, getId, getParentId, getChildren);
    }
    private HierarchyConverter(List<E> entities, Function<E, D> toDto, Function<E, Long> getId, Function<E, Long> getParentId, Function<D, List<D>> getChildren) {
        this.entities = entities;
        this.toDto = toDto;
        this.getId = getId;
        this.getParentId = getParentId;
        this.getChildren = getChildren;
    }

    public List<D> convertToHierarchy() {
        // id를 Dto 자체와 매핑하는 맵을 생성.
        Map<Long, D> map = new HashMap<>();
        // 계층 구조를 구성하기 위한 최상위 Dto 목록을 만든다.
        List<D> roots = new ArrayList<>();
        for (E entity : entities) {
            Long id = getId.apply(entity);
            D dto = toDto.apply(entity);
            map.put(id, dto);
            Long parentId = getParentId.apply(entity);
            if (parentId == null) {
                roots.add(dto);
            } else {
                try {
                    D parentDto = map.get(parentId);
                    getChildren.apply(parentDto).add(dto);
                } catch (NullPointerException e) {
                    throw new CannotConvertHelperException(e.getMessage());
                }
            }
        }
        return roots;
    }

}