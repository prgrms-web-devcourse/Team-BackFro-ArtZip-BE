package com.prgrms.artzip.exhibition.controller;

import com.prgrms.artzip.exhibition.dto.request.ExhibitionCreateRequest;
import com.prgrms.artzip.exhibition.service.ExhibitionAdminService;
import java.net.URI;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/v1/admin/exhibitions")
@RequiredArgsConstructor
@RestController
public class ExhibitionAdminController {
  private final ExhibitionAdminService exhibitionAdminService;

  @PostMapping()
  public ResponseEntity<Void> createExhibition(@Valid @RequestBody ExhibitionCreateRequest request) {
    Long exhibitionId = exhibitionAdminService.createExhibition(request);
    return ResponseEntity.created(URI.create("/api/v1/admin/exhibitions/" + exhibitionId)).build();
  }
}
