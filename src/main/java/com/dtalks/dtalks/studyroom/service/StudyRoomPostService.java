package com.dtalks.dtalks.studyroom.service;

import com.dtalks.dtalks.studyroom.dto.PostDto;
import com.dtalks.dtalks.studyroom.dto.PostRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface StudyRoomPostService {

    PostDto addPost(Long studyRoomId, PostRequestDto postRequestDto);
    Page<PostDto> getPostsByStudyRooms(Long studyRoomId, Pageable pageable);
    PostDto getPost(Long studyRoomId, Long postId);
    PostDto changePost(Long studyRoomId, Long postId, PostRequestDto postRequestDto);
    void removePost(Long studyRoomId, Long postId);
}
