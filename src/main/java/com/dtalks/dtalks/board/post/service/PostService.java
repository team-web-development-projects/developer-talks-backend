package com.dtalks.dtalks.board.post.service;

import com.dtalks.dtalks.board.post.dto.PostDto;
import com.dtalks.dtalks.board.post.dto.PostRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PostService {
    PostDto searchById(Long id);
    Page<PostDto> searchAllPost(Pageable pageable);
    Page<PostDto> searchPostsByUser(String userId, Pageable pageable);
    Page<PostDto> searchByWord(String keyword, Pageable pageable);

    List<PostDto> search5BestPosts();

    Long createPost(PostRequestDto postDto, List<MultipartFile> files);
    Long updatePost(PostRequestDto postDto, List<MultipartFile> files, Long id);
    void deletePost(Long id);

}
