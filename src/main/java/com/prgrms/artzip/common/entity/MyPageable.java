package com.prgrms.artzip.common.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class MyPageable {
  @ApiModelProperty(value = "페이지 번호", example = "0")
  private Integer page;

  @ApiModelProperty(value = "페이지 크기", allowableValues = "range[0, 100]", example = "10")
  private Integer size;
}
