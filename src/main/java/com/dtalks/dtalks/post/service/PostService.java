package com.dtalks.dtalks.post.service;

import com.dtalks.dtalks.post.dto.PostRequestDto;
import com.dtalks.dtalks.post.dto.PostResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;

public interface PostService {
    PostResponseDto searchById(Long id);
    Page<PostResponseDto> searchAllPost(Pageable pageable);

    Long createPost(PostRequestDto postDto, UserDetails userDetails);
    Long updatePost(PostRequestDto postDto, Long id, UserDetails user);
    void deletePost(Long id, UserDetails user);
}
