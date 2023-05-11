package com.dtalks.dtalks.board.post.service;

import com.dtalks.dtalks.board.post.dto.PostDto;
import com.dtalks.dtalks.board.post.dto.PostRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PostService {
    PostDto searchById(Long id);
    Page<PostDto> searchAllPost(Pageable pageable);
    Page<PostDto> searchPostsByUser(Long userId, Pageable pageable);
    Page<PostDto> searchByWord(String keyword, Pageable pageable);

    Long createPost(PostRequestDto postDto);
    Long updatePost(PostRequestDto postDto, Long id);
    void deletePost(Long id);

    void updateViewCount(Long id);
}
