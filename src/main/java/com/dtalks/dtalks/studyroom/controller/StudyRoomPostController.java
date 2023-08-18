package com.dtalks.dtalks.studyroom.controller;

import com.dtalks.dtalks.studyroom.dto.StudyRoomPostDto;
import com.dtalks.dtalks.studyroom.dto.StudyRoomPostRequestDto;
import com.dtalks.dtalks.studyroom.service.StudyRoomPostService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/study-rooms/posts")
public class StudyRoomPostController {

    private final StudyRoomPostService studyRoomPostService;

    @Operation(summary = "스터디룸 게시글 생성")
    @PostMapping(value = "/{studyRoomId}")
    public ResponseEntity<StudyRoomPostDto> studyRoomPostAdd(@PathVariable Long studyRoomId, @Valid @RequestBody StudyRoomPostRequestDto studyRoomPostRequestDto) {
        return ResponseEntity.ok(studyRoomPostService.addPost(studyRoomId, studyRoomPostRequestDto));
    }

    @Operation(summary = "스터디룸 게시글 전체 조회")
    @GetMapping(value = "/{studyRoomId}")
    public ResponseEntity<Page<StudyRoomPostDto>> studyRoomPostList(
            @PathVariable Long studyRoomId,
            @PageableDefault(size = 10, page = 0, sort = "createDate", direction = Sort.Direction.DESC) @ParameterObject Pageable pageable) {
        return ResponseEntity.ok(studyRoomPostService.getPostsByStudyRooms(studyRoomId, pageable));
    }

    @Operation(summary = "스터디룸 게시글 단건 조회")
    @GetMapping(value = "/{studyRoomId}/{postId}")
    public ResponseEntity<StudyRoomPostDto> studyRoomPostGet(@PathVariable Long studyRoomId, @PathVariable Long postId) {
        return ResponseEntity.ok(studyRoomPostService.getPost(studyRoomId, postId));
    }

    @Operation(summary = "스터디룸 게시글 수정")
    @PutMapping(value = "/{studyRoomId}/{postId}")
    public ResponseEntity<StudyRoomPostDto> studyRoomPostPut(@PathVariable Long studyRoomId, @PathVariable Long postId, @Valid @RequestBody StudyRoomPostRequestDto studyRoomPostRequestDto) {
        return ResponseEntity.ok(studyRoomPostService.changePost(studyRoomId, postId, studyRoomPostRequestDto));
    }

    @Operation(summary = "스터디룸 게시글 삭제")
    @DeleteMapping(value = "/{studyRoomId}/{postId}")
    public ResponseEntity<Void> studyRoomPostRemove(@PathVariable Long studyRoomId, @PathVariable Long postId) {
        studyRoomPostService.removePost(studyRoomId, postId);
        return ResponseEntity.ok().build();
    }
}
