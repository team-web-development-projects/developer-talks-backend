package com.dtalks.dtalks.admin.inquiry.controller;

import com.dtalks.dtalks.admin.inquiry.dto.InquiryDto;
import com.dtalks.dtalks.admin.inquiry.dto.InquiryResponseDto;
import com.dtalks.dtalks.admin.inquiry.service.InquiryService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/inquiries")
@RequiredArgsConstructor
public class InquiryController {
    private final InquiryService inquiryService;

    @Operation(summary = "특정 문의사항 id로 조회")
    @GetMapping("/{id}")
    public ResponseEntity<InquiryResponseDto> searchById(@PathVariable Long id) {
        return ResponseEntity.ok(inquiryService.searchById(id));
    }

    @Operation(summary = "모든 문의사항 조회")
    @GetMapping("/all")
    public ResponseEntity<Page<InquiryResponseDto>> searchAll(@PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(inquiryService.searchAllInquiry(pageable));
    }

    @Operation(summary = "문의사항 작성")
    @PostMapping
    public ResponseEntity<Long> createInquiry(@Valid @RequestBody InquiryDto inquiryDto) {
        return ResponseEntity.ok(inquiryService.createInquiry(inquiryDto));
    }

    @Operation(summary = "문의사항 수정")
    @PutMapping("/{id}")
    public ResponseEntity<Long> updateInquiry(@Valid @RequestBody InquiryDto inquiryDto, @PathVariable Long id) {
        return ResponseEntity.ok(inquiryService.updateInquiry(inquiryDto, id));
    }

    @Operation(summary = "문의사항 삭제")
    @DeleteMapping("/{id}")
    public void deleteAnnouncement(@PathVariable Long id) {
        inquiryService.deleteInquiry(id);
    }
}
