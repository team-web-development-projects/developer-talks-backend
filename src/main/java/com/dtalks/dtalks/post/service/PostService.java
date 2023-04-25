package com.dtalks.dtalks.post.service;

import com.dtalks.dtalks.post.dto.PostDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostService {
    PostDto searchById(Long id);
    Page<PostDto> searchAllPost(Pageable pageable);

    Long createPost(PostDto postDto);
    Long updatePost(PostDto postDto, Long id);
    void deletePost(Long id);
}
