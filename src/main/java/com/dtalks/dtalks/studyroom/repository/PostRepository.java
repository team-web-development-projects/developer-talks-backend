package com.dtalks.dtalks.studyroom.repository;

import com.dtalks.dtalks.studyroom.entity.Post;
import com.dtalks.dtalks.studyroom.entity.StudyRoom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findByStudyRoom(StudyRoom studyRoom, Pageable pageable);
}
