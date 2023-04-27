package com.dtalks.dtalks.post.service;

import com.dtalks.dtalks.post.dto.PostDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;

public interface PostService {
    PostDto searchById(Long id);
    Page<PostDto> searchAllPost(Pageable pageable);

    Long createPost(PostDto postDto);
    Long updatePost(PostDto postDto, Long id, UserDetails user);
    void deletePost(Long id, UserDetails user);
}
