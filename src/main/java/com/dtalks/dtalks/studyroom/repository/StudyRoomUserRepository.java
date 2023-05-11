package com.dtalks.dtalks.studyroom.repository;

import com.dtalks.dtalks.studyroom.entity.StudyRoomUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyRoomUserRepository extends JpaRepository<StudyRoomUser, Long> {
}
