package com.dtalks.dtalks.studyroom.service;

import com.dtalks.dtalks.studyroom.dto.StudyRoomPostDto;
import com.dtalks.dtalks.studyroom.dto.StudyRoomPostRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface StudyRoomPostService {

    StudyRoomPostDto addPost(Long studyRoomId, StudyRoomPostRequestDto studyRoomPostRequestDto);
    Page<StudyRoomPostDto> getPostsByStudyRooms(Long studyRoomId, Pageable pageable);
    StudyRoomPostDto getPost(Long studyRoomId, Long postId);
    StudyRoomPostDto changePost(Long studyRoomId, Long postId, StudyRoomPostRequestDto studyRoomPostRequestDto);
    void removePost(Long studyRoomId, Long postId);
}
