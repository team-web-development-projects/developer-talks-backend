package com.dtalks.dtalks.studyroom.controller;

import com.dtalks.dtalks.studyroom.dto.PostDto;
import com.dtalks.dtalks.studyroom.dto.PostRequestDto;
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
    public ResponseEntity<PostDto> studyRoomPostAdd(@PathVariable Long studyRoomId, @Valid @RequestBody PostRequestDto postRequestDto) {
        return ResponseEntity.ok(studyRoomPostService.addPost(studyRoomId, postRequestDto));
    }

    @Operation(summary = "스터디룸 게시글 전체 조회")
    @GetMapping(value = "/{studyRoomId}")
    public ResponseEntity<Page<PostDto>> studyRoomPostList(
            @PathVariable Long studyRoomId,
            @PageableDefault(size = 10, page = 0, sort = "createDate", direction = Sort.Direction.DESC) @ParameterObject Pageable pageable) {
        return ResponseEntity.ok(studyRoomPostService.getPostsByStudyRooms(studyRoomId, pageable));
    }

    @Operation(summary = "스터디룸 게시글 단건 조회")
    @GetMapping(value = "/{studyRoomId}/{postId}")
    public ResponseEntity<PostDto> studyRoomPostGet(@PathVariable Long studyRoomId, @PathVariable Long postId) {
        return ResponseEntity.ok(studyRoomPostService.getPost(studyRoomId, postId));
    }

    @Operation(summary = "스터디룸 게시글 수정")
    @PutMapping(value = "/{studyRoomId}/{postId}")
    public ResponseEntity<PostDto> studyRoomPostPut(@PathVariable Long studyRoomId, @PathVariable Long postId, @Valid @RequestBody PostRequestDto postRequestDto) {
        return ResponseEntity.ok(studyRoomPostService.changePost(studyRoomId, postId, postRequestDto));
    }

    @Operation(summary = "스터디룸 게시글 삭제")
    @DeleteMapping(value = "/{studyRoomId}/{postId}")
    public ResponseEntity<Void> studyRoomPostRemove(@PathVariable Long studyRoomId, @PathVariable Long postId) {
        studyRoomPostService.removePost(studyRoomId, postId);
        return ResponseEntity.ok().build();
    }
}
