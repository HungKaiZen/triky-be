package vn.tayjava.controller.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class PageResponseAbstract {
    private int pageNumber;
    private int size;
    private long totalPages;
    private long totalElements;
}
