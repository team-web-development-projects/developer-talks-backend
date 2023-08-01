package com.dtalks.dtalks.admin.announcement.controller;

import com.dtalks.dtalks.admin.announcement.dto.AnnounceDto;
import com.dtalks.dtalks.admin.announcement.dto.AnnouncementResponseDto;
import com.dtalks.dtalks.admin.announcement.service.AnnouncementService;
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
@RequestMapping("/announcements")
@RequiredArgsConstructor
public class AnnouncementController {
    private final AnnouncementService announcementService;

    @Operation(summary = "특정 공지문 id로 조회")
    @GetMapping("/{id}")
    public ResponseEntity<AnnouncementResponseDto> searchById(@PathVariable Long id) {
        return ResponseEntity.ok(announcementService.searchById(id));
    }

    @Operation(summary = "모든 공지문 조회")
    @GetMapping("/all")
    public ResponseEntity<Page<AnnouncementResponseDto>> searchAll(@PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(announcementService.searchAllAnnouncement(pageable));
    }

    @Operation(summary = "공지문 작성")
    @PostMapping
    public ResponseEntity<Long> createAnnouncement(@Valid @RequestBody AnnounceDto announceDto) {
        return ResponseEntity.ok(announcementService.createAnnouncement(announceDto));
    }

    @Operation(summary = "공지문 수정")
    @PutMapping("/{id}")
    public ResponseEntity<Long> updateAnnouncement(@Valid @RequestBody AnnounceDto announceDto, @PathVariable Long id) {
        return ResponseEntity.ok(announcementService.updateAnnouncement(announceDto, id));
    }

    @Operation(summary = "공지문 삭제")
    @DeleteMapping("/{id}")
    public void deleteAnnouncement(@PathVariable Long id) {
        announcementService.deleteAnnouncement(id);
    }
}
