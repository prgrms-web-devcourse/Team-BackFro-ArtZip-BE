package com.prgrms.artzip.exhibition.controller;

import com.prgrms.artzip.exhibition.dto.request.ExhibitionCreateRequest;
import com.prgrms.artzip.exhibition.service.ExhibitionAdminService;
import io.swagger.annotations.Api;
import java.net.URI;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Api(tags = {"전시회 관리자 API"})
@RequestMapping("/api/v1/admin/exhibitions")
@RequiredArgsConstructor
@RestController
public class ExhibitionAdminController {
  private final ExhibitionAdminService exhibitionAdminService;

  @PostMapping()
  public ResponseEntity<Void> createExhibition(
      @Valid @ModelAttribute ExhibitionCreateRequest request,
      @RequestPart MultipartFile thumbnail
  ) {
    Long exhibitionId = exhibitionAdminService.createExhibition(request, thumbnail);
    return ResponseEntity.created(URI.create("/api/v1/admin/exhibitions/" + exhibitionId)).build();
  }
}
