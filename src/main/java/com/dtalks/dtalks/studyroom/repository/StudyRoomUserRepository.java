package com.dtalks.dtalks.studyroom.repository;

import com.dtalks.dtalks.studyroom.entity.StudyRoom;
import com.dtalks.dtalks.studyroom.entity.StudyRoomUser;
import com.dtalks.dtalks.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudyRoomUserRepository extends JpaRepository<StudyRoomUser, Long> {

    public List<StudyRoomUser> findAllByUser(User user);

    public List<StudyRoomUser> findAllByStudyRoom(StudyRoom studyRoom);

    public Optional<StudyRoomUser> findByStudyRoomAndUser(StudyRoom studyRoom, User user);
}
