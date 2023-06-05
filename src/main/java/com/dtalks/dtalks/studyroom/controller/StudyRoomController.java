package com.dtalks.dtalks.studyroom.controller;

import com.dtalks.dtalks.studyroom.dto.StudyRoomJoinResponseDto;
import com.dtalks.dtalks.studyroom.dto.StudyRoomRequestDto;
import com.dtalks.dtalks.studyroom.dto.StudyRoomResponseDto;
import com.dtalks.dtalks.studyroom.service.StudyRoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "studyRooms")
@RestController
public class StudyRoomController {

    private final Logger LOGGER = LoggerFactory.getLogger(StudyRoomController.class);

    private final StudyRoomService studyRoomService;

    @Autowired
    public StudyRoomController(StudyRoomService studyRoomService) {
        this.studyRoomService = studyRoomService;
    }

    @Operation(summary = "스터디룸 생성")
    @PostMapping(value = "/study-room")
    public ResponseEntity<StudyRoomResponseDto> createStudyRoom(@Valid @RequestBody StudyRoomRequestDto studyRoomRequestDto) {
        LOGGER.info("createStudyRoom controller 호출됨");
        StudyRoomResponseDto studyRoomResponseDto = studyRoomService.createStudyRoom(studyRoomRequestDto);
        return ResponseEntity.ok(studyRoomResponseDto);
    }

    @Operation(summary = "id를 이용한 스터디룸 조회")
    @GetMapping("/study-room/{id}")
    public ResponseEntity<StudyRoomResponseDto> findStudyRoomById(@PathVariable Long id) {
        LOGGER.info("findStudyRoomById controller 호출됨");
        return ResponseEntity.ok(studyRoomService.findStudyRoomById(id));
    }

    @Operation(summary = "스터디룸 리스트 조회")
    @GetMapping("/study-rooms")
    public ResponseEntity<Page<StudyRoomResponseDto>> findAll(
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC, page = 0) @ParameterObject Pageable pageable
    ) {
        return ResponseEntity.ok(studyRoomService.findAll(pageable));
    }

    @Operation(summary = "스터디룸 업데이트")
    @PutMapping("/study-room/{id}")
    public ResponseEntity<StudyRoomResponseDto> updateStudyRoom(@PathVariable Long id,
                                                                @Valid @RequestBody StudyRoomRequestDto studyRoomRequestDto) {
        return ResponseEntity.ok(studyRoomService.updateStudyRoom(id, studyRoomRequestDto));
    }

    @Operation(summary = "스터디룸 삭제")
    @DeleteMapping("/study-room/{id}")
    public ResponseEntity deleteStudyRoom(@PathVariable Long id) {
        studyRoomService.deleteStudyRoom(id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "스터디룸 가입")
    @PostMapping("/study-room/join/{id}")
    public ResponseEntity<StudyRoomResponseDto> joinStudyRoom(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(studyRoomService.joinStudyRoom(id));
    }

    @Operation(summary = "스터디룸 가입 신청 리스트")
    @GetMapping("study-rooms/requests")
    public ResponseEntity<Page<StudyRoomJoinResponseDto>> requestStudyRoom(
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC, page = 0) @ParameterObject Pageable pageable
    ) {
        return ResponseEntity.ok(studyRoomService.studyRoomRequestList(pageable));
    }

    @Operation(summary = "스터디룸 가입 승인")
    @PostMapping("study-room/accept/{studyRoomId}/{studyRoomUserId}")
    public ResponseEntity<StudyRoomResponseDto> acceptStudyRoom(@PathVariable Long studyRoomId, @PathVariable Long studyRoomUserId) {
        return ResponseEntity.ok(studyRoomService.acceptJoinStudyRoom(studyRoomId, studyRoomUserId));
    }

    @Operation(summary = "스터디룸 탈퇴")
    @DeleteMapping("study-room/exit/{studyRoomId}")
    public ResponseEntity exitStudyRoom(@PathVariable Long studyRoomId) {
        studyRoomService.deleteStudyRoomUser(studyRoomId);
        return ResponseEntity.ok().build();
    }
}
