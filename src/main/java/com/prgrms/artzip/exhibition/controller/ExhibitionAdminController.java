package com.prgrms.artzip.exhibition.controller;

import com.prgrms.artzip.common.ApiResponse;
import com.prgrms.artzip.common.PageResponse;
import com.prgrms.artzip.exhibition.dto.request.ExhibitionCreateRequest;
import com.prgrms.artzip.exhibition.dto.response.ExhibitionDetailInfoResponse;
import com.prgrms.artzip.exhibition.dto.response.ExhibitionInfoResponse;
import com.prgrms.artzip.exhibition.service.ExhibitionAdminService;
import io.swagger.annotations.Api;
import java.net.URI;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
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

  @GetMapping()
  public ResponseEntity<ApiResponse<PageResponse<ExhibitionInfoResponse>>> getExhibitions(
      @PageableDefault(
          sort = {"created_at"},
          direction = Direction.DESC
      ) Pageable pageable
  ) {
    PageResponse<ExhibitionInfoResponse> exhibitions = PageResponse
        .<ExhibitionInfoResponse>builder()
        .page(exhibitionAdminService.getExhibitions((pageable)))
        .build();
    return ResponseEntity.ok(ApiResponse
        .<PageResponse<ExhibitionInfoResponse>>builder()
        .message("전시회 리스트 조회 완료")
        .status(HttpStatus.OK.value())
        .data(exhibitions)
        .build());
  }

  @GetMapping("/{exhibitionId}")
  public ResponseEntity<ApiResponse<ExhibitionDetailInfoResponse>> getExhibition(
      @PathVariable Long exhibitionId
  ) {
    ExhibitionDetailInfoResponse exhibition = exhibitionAdminService.getExhibition(exhibitionId);
    return ResponseEntity.ok(ApiResponse
        .<ExhibitionDetailInfoResponse>builder()
        .message("전시회 상세 조회 완료")
        .status(HttpStatus.OK.value())
        .data(exhibition)
        .build());
  }
}
