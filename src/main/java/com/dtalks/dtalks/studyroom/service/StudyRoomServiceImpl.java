package com.dtalks.dtalks.studyroom.service;

import com.dtalks.dtalks.exception.ErrorCode;
import com.dtalks.dtalks.exception.exception.PermissionNotGrantedException;
import com.dtalks.dtalks.exception.exception.StudyRoomNotFoundException;
import com.dtalks.dtalks.exception.exception.UserNotFoundException;
import com.dtalks.dtalks.studyroom.dto.StudyRoomRequestDto;
import com.dtalks.dtalks.studyroom.dto.StudyRoomResponseDto;
import com.dtalks.dtalks.studyroom.entity.StudyRoom;
import com.dtalks.dtalks.studyroom.entity.StudyRoomUser;
import com.dtalks.dtalks.studyroom.enums.StudyRoomLevel;
import com.dtalks.dtalks.studyroom.repository.StudyRoomRepository;
import com.dtalks.dtalks.studyroom.repository.StudyRoomUserRepository;
import com.dtalks.dtalks.user.Util.SecurityUtil;
import com.dtalks.dtalks.user.entity.User;
import com.dtalks.dtalks.user.repository.UserRepository;
import com.dtalks.dtalks.user.service.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StudyRoomServiceImpl implements StudyRoomService{

    private final Logger LOGGER = LoggerFactory.getLogger(StudyRoomServiceImpl.class);

    private final StudyRoomRepository studyRoomRepository;
    private final StudyRoomUserRepository studyRoomUserRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public StudyRoomResponseDto createStudyRoom(StudyRoomRequestDto studyRoomRequestDto) {
        LOGGER.info("createStudyRoom service 호출됨");
        Optional<User> user = Optional.ofNullable(userRepository.getByUserid(SecurityUtil.getCurrentUserId()));

        if(user.isEmpty()) {
            throw new UserNotFoundException(ErrorCode.USER_NOT_FOUND_ERROR, "존재하지 않는 사용자입니다.");
        }

        StudyRoom studyRoom = StudyRoom.toEntity(studyRoomRequestDto);
        StudyRoomUser studyRoomUser = StudyRoomUser.toEntity(user.get(), studyRoom, StudyRoomLevel.LEADER, true);
        List<StudyRoomUser> studyRoomUsers = new ArrayList<>();
        studyRoomUsers.add(studyRoomUser);
        studyRoom.setStudyRoomUsers(studyRoomUsers);
        studyRoomRepository.save(studyRoom);

        Optional<StudyRoom> studyRoom1 = studyRoomRepository.findById(studyRoom.getId());
        StudyRoomResponseDto studyRoomResponseDto = StudyRoomResponseDto.toDto(studyRoom1.get());

        return studyRoomResponseDto;
    }

    @Override
    @Transactional(readOnly = true)
    public StudyRoomResponseDto findStudyRoomById(Long id) {
        Optional<StudyRoom> studyRoom = studyRoomRepository.findById(id);
        if(studyRoom.isEmpty()) {
            throw new StudyRoomNotFoundException(ErrorCode.STUDYROOM_NOT_FOUND_ERROR, "존재하지 않는 스터디룸입니다.");
        }

        return StudyRoomResponseDto.toDto(studyRoom.get());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<StudyRoomResponseDto> findAll(Pageable pageable) {
        Page<StudyRoom> studyRooms = studyRoomRepository.findAll(pageable);
        return studyRooms.map(StudyRoomResponseDto::toDto);
    }

    @Override
    @Transactional
    public StudyRoomResponseDto updateStudyRoom(Long id, StudyRoomRequestDto studyRoomRequestDto) {
        Optional<StudyRoom> studyRoom = studyRoomRepository.findById(id);
        if(studyRoom.isEmpty()) {
            throw new StudyRoomNotFoundException(ErrorCode.STUDYROOM_NOT_FOUND_ERROR, "존재하지 않는 스터디룸입니다.");
        }

        StudyRoom studyRoomResponse = studyRoom.get();
        studyRoomResponse.setTitle(studyRoomRequestDto.getTitle());
        studyRoomResponse.setContent(studyRoomRequestDto.getContent());
        studyRoomResponse.setSkills(studyRoomRequestDto.getSkills());
        studyRoomResponse.setAutoJoin(studyRoomRequestDto.isAutoJoin());
        studyRoomResponse.setJoinableCount(studyRoomRequestDto.getJoinableCount());

        studyRoomRepository.save(studyRoomResponse);

        return StudyRoomResponseDto.toDto(studyRoomResponse);
    }

    @Override
    @Transactional
    public void deleteStudyRoom(Long id) {
        Optional<StudyRoom> optionalStudyRoom = studyRoomRepository.findById(id);
        if(optionalStudyRoom.isEmpty()) {
            throw new StudyRoomNotFoundException(ErrorCode.STUDYROOM_NOT_FOUND_ERROR, "존재하지 않는 스터디룸입니다.");
        }
        StudyRoom studyRoom = optionalStudyRoom.get();
        List<StudyRoomUser> studyRoomUsers = studyRoom.getStudyRoomUsers();
        for(StudyRoomUser studyRoomUser: studyRoomUsers) {
            if(studyRoomUser.getStudyRoomLevel().equals(StudyRoomLevel.LEADER)) {
                if(studyRoomUser.getUser().getUserid().equals(SecurityUtil.getCurrentUserId())) {
                    studyRoomRepository.delete(studyRoom);
                    return;
                }
            }
        }
        throw new PermissionNotGrantedException(ErrorCode.PERMISSION_NOT_GRANTED_ERROR, "해당 게시글을 삭제할 수 있는 권한이 없습니다.");
    }
}
