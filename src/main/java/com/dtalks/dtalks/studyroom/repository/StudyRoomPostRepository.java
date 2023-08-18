package com.dtalks.dtalks.studyroom.repository;

import com.dtalks.dtalks.studyroom.entity.StudyRoomPost;
import com.dtalks.dtalks.studyroom.entity.StudyRoom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyRoomPostRepository extends JpaRepository<StudyRoomPost, Long> {
    Page<StudyRoomPost> findByStudyRoom(StudyRoom studyRoom, Pageable pageable);
}
