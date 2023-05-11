package com.dtalks.dtalks.studyroom.controller;

import com.dtalks.dtalks.studyroom.dto.StudyRoomRequestDto;
import com.dtalks.dtalks.studyroom.dto.StudyRoomResponseDto;
import com.dtalks.dtalks.studyroom.service.StudyRoomService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "studyRooms")
@RestController
@RequestMapping("/study-rooms")
public class StudyRoomController {

    private final Logger LOGGER = LoggerFactory.getLogger(StudyRoomController.class);

    private final StudyRoomService studyRoomService;

    @Autowired
    public StudyRoomController(StudyRoomService studyRoomService) {
        this.studyRoomService = studyRoomService;
    }

    @PostMapping
    public ResponseEntity<StudyRoomResponseDto> createStudyRoom(@Valid @RequestBody StudyRoomRequestDto studyRoomRequestDto) {
        LOGGER.info("createStudyRoom controller 호출됨");
        StudyRoomResponseDto studyRoomResponseDto = studyRoomService.createStudyRoom(studyRoomRequestDto);
        return ResponseEntity.ok(studyRoomResponseDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StudyRoomResponseDto> findStudyRoomById(@PathVariable Long id) {
        LOGGER.info("findStudyRoomById controller 호출됨");
        return ResponseEntity.ok(studyRoomService.findStudyRoomById(id));
    }
}
