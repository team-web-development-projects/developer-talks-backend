package com.dtalks.dtalks.board.post.service;

import com.dtalks.dtalks.board.post.dto.PostDto;
import com.dtalks.dtalks.board.post.dto.PostRequestDto;
import com.dtalks.dtalks.board.post.dto.PutRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PostService {
    PostDto searchById(Long id, Boolean adminPage);
    Page<PostDto> searchAllPost(Pageable pageable);
    Page<PostDto> searchPostsByUser(String nickname, Pageable pageable);
    Page<PostDto> searchByWord(String keyword, Pageable pageable);

    List<PostDto> search5BestPosts();

    Long createPost(PostRequestDto postDto);
    Long updatePost(PutRequestDto postDto, Long id);
    void deletePost(Long id);

}
