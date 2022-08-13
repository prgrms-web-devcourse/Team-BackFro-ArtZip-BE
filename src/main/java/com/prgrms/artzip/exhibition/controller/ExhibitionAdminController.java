package com.prgrms.artzip.exhibition.controller;

import com.prgrms.artzip.common.ApiResponse;
import com.prgrms.artzip.common.PageResponse;
import com.prgrms.artzip.exhibition.dto.request.ExhibitionCreateOrUpdateRequest;
import com.prgrms.artzip.exhibition.dto.request.ExhibitionSemiUpdateRequest;
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
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

  @GetMapping()
  public ResponseEntity<ApiResponse<PageResponse<ExhibitionInfoResponse>>> getExhibitions(
      @PageableDefault(
          sort = {"createdAt"},
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
    ExhibitionDetailInfoResponse exhibition = exhibitionAdminService
        .getExhibitionDetail(exhibitionId);
    return ResponseEntity.ok(ApiResponse
        .<ExhibitionDetailInfoResponse>builder()
        .message("전시회 상세 조회 완료")
        .status(HttpStatus.OK.value())
        .data(exhibition)
        .build());
  }

  @PostMapping()
  public ResponseEntity<Void> createExhibition(
      @Valid @ModelAttribute ExhibitionCreateOrUpdateRequest request,
      @RequestPart MultipartFile thumbnail
  ) {
    Long exhibitionId = exhibitionAdminService.createExhibition(request, thumbnail);
    HttpHeaders headers = new HttpHeaders();
    headers.setLocation(URI.create("/api/v1/admin/exhibitions/" + exhibitionId));
    return new ResponseEntity<>(headers, HttpStatus.SEE_OTHER);
  }

  @PutMapping("/{exhibitionId}")
  public ResponseEntity<?> updateExhibition(
      @PathVariable Long exhibitionId,
      @Valid @ModelAttribute ExhibitionCreateOrUpdateRequest request,
      @RequestPart(required = false) MultipartFile thumbnail
  ) {
    exhibitionAdminService.updateExhibition(exhibitionId, request, thumbnail);
    HttpHeaders headers = new HttpHeaders();
    headers.setLocation(URI.create("/api/v1/admin/exhibitions/" + exhibitionId));
    return new ResponseEntity<>(headers, HttpStatus.SEE_OTHER);
  }

  @PatchMapping("/{exhibitionId}/semi")
  public ResponseEntity<?> updateSemiExhibition(
      @PathVariable Long exhibitionId,
      @RequestBody ExhibitionSemiUpdateRequest request
  ) {
    exhibitionAdminService.semiUpdateExhibition(exhibitionId, request);
    HttpHeaders headers = new HttpHeaders();
    headers.setLocation(URI.create("/api/v1/admin/exhibitions/" + exhibitionId));
    return new ResponseEntity<>(headers, HttpStatus.SEE_OTHER);
  }

  @DeleteMapping("/{exhibitionId}")
  public ResponseEntity<?> deleteExhibition(
      @PathVariable Long exhibitionId
  ) {
    exhibitionAdminService.deleteExhibition(exhibitionId);
    HttpHeaders headers = new HttpHeaders();
    headers.setLocation(URI.create("/api/v1/admin/exhibitions"));
    return new ResponseEntity<>(headers, HttpStatus.SEE_OTHER);
  }
}
