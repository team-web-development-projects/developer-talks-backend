package com.dtalks.dtalks.admin.post.service;

import com.dtalks.dtalks.admin.post.dto.AdminPostDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AdminPostService {
    Page<AdminPostDto> getAllPosts(Pageable pageable, Boolean forbidden);
    void forbidPost(Long id);
    void restorePost(Long id);
}
