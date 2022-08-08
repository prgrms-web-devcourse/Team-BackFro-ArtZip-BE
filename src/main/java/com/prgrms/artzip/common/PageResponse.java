package com.prgrms.artzip.common;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

@Getter
public class PageResponse<T> {
  private List<T> content;
  private int numberOfElements;
  private int pageNumber;
  private long offset;
  private int pageSize;
  private long totalElements;
  private int totalPage;

  @Builder
  public PageResponse(Page<T> page) {
    this.content = page.getContent();
    this.numberOfElements = page.getNumberOfElements();
    this.pageNumber = page.getPageable().getPageNumber();
    this.offset = page.getPageable().getOffset();
    this.pageSize = page.getPageable().getPageSize();
    this.totalElements = page.getTotalElements();
    this.totalPage = page.getTotalPages();
  }
}
