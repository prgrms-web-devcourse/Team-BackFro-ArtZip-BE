package com.prgrms.artzip.common;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
public class PageResponse {
  private List<Object> content;
  private Long numberOfElement;
  private Long offset;
  private Long pageSize;
  private Long totalElements;
  private Long totalPage;

  @Builder
  public PageResponse(List<Object> content, Long numberOfElement, Long offset,
      Long pageSize, Long totalElements, Long totalPage) {
    this.content = content;
    this.numberOfElement = numberOfElement;
    this.offset = offset;
    this.pageSize = pageSize;
    this.totalElements = totalElements;
    this.totalPage = totalPage;
  }
}
