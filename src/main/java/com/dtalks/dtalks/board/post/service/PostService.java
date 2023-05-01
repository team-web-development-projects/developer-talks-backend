package com.dtalks.dtalks.board.post.service;

import com.dtalks.dtalks.board.post.dto.PostDto;
import com.dtalks.dtalks.board.post.dto.PostRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;

public interface PostService {
    PostDto searchById(Long id);
    Page<PostDto> searchAllPost(Pageable pageable);

    Long createPost(PostRequestDto postDto);
    Long updatePost(PostRequestDto postDto, Long id);
    void deletePost(Long id);
}
