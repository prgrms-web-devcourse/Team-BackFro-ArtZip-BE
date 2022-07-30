package com.prgrms.artzip.common.util;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("/")
public class GlobalController {
  @GetMapping()
  public String checkConnection() {
    return "ArtZip Server Connection Success";
  }
}
