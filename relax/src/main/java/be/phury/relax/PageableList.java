package be.phury.relax;

import java.util.Iterator;
import java.util.List;

/**
 * Defines a pageable list of elements
 */
public class PageableList<T> implements Iterable<T> {
    private List<T> list;
    private Integer offset;
    private Integer limit;
    private Integer total;

    public static <T> PageableList<T> createPaging(List<T> list, Integer offset, Integer limit) {
        PageableList<T> pageable = new PageableList<>();
        pageable.setLimit(limit);
        pageable.setOffset(offset);
        pageable.setList(computeSubList(list, offset, limit));
        pageable.setTotal(list.size());
        return pageable;
    }

    private static <T> List<T> computeSubList(List<T> list, Integer offset, Integer limit) {
        Integer from = limit * (offset);
        Integer to = Math.min(limit*(offset+1), list.size());
        if (to < 0) {
            return list;
        }
        if (from < -1) {
            throw new IllegalArgumentException("offset must be < -1 but was " + from);
        }
        return list.subList(from, to);
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    @Override
    public Iterator<T> iterator() {
        return list.iterator();
    }
}
