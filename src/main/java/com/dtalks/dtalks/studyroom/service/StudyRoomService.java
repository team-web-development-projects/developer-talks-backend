package com.dtalks.dtalks.studyroom.service;

import com.dtalks.dtalks.studyroom.dto.StudyRoomRequestDto;
import com.dtalks.dtalks.studyroom.dto.StudyRoomResponseDto;

public interface StudyRoomService {
    public StudyRoomResponseDto createStudyRoom(StudyRoomRequestDto studyRoomRequestDto);
    public StudyRoomResponseDto findStudyRoomById(Long id);
}
