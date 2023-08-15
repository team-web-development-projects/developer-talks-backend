package com.dtalks.dtalks.studyroom.service;

import com.dtalks.dtalks.studyroom.dto.PostDto;
import com.dtalks.dtalks.studyroom.dto.PostRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostService {

    PostDto addPost(PostRequestDto postRequestDto);
    Page<PostDto> getPostsByStudyRooms(Long studyRoomId, Pageable pageable);
    PostDto getPost(Long postId);
    PostDto changePost(PostRequestDto postRequestDto);
    void removePost(Long postId);
}
