package com.dtalks.dtalks.studyroom.service;

import com.dtalks.dtalks.exception.ErrorCode;
import com.dtalks.dtalks.exception.exception.CustomException;
import com.dtalks.dtalks.studyroom.dto.StudyRoomJoinResponseDto;
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
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
            throw new CustomException(ErrorCode.USER_NOT_FOUND_ERROR, "존재하지 않는 사용자입니다.");
        }

        StudyRoom studyRoom = StudyRoom.toEntity(studyRoomRequestDto);
        StudyRoomUser studyRoomUser = StudyRoomUser.toEntity(user.get(), studyRoom, StudyRoomLevel.LEADER, true);
        List<StudyRoomUser> studyRoomUsers = new ArrayList<>();
        studyRoomUsers.add(studyRoomUser);
        studyRoom.setStudyRoomUsers(studyRoomUsers);
        StudyRoom savedStudyroom = studyRoomRepository.save(studyRoom);

        StudyRoomResponseDto studyRoomResponseDto = StudyRoomResponseDto.toDto(savedStudyroom);

        return studyRoomResponseDto;
    }

    @Override
    @Transactional(readOnly = true)
    public StudyRoomResponseDto findStudyRoomById(Long id) {
        Optional<StudyRoom> studyRoom = studyRoomRepository.findById(id);
        if(studyRoom.isEmpty()) {
            throw new CustomException(ErrorCode.STUDYROOM_NOT_FOUND_ERROR, "존재하지 않는 스터디룸입니다.");
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
            throw new CustomException(ErrorCode.STUDYROOM_NOT_FOUND_ERROR, "존재하지 않는 스터디룸입니다.");
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
            throw new CustomException(ErrorCode.STUDYROOM_NOT_FOUND_ERROR, "존재하지 않는 스터디룸입니다.");
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
        throw new CustomException(ErrorCode.PERMISSION_NOT_GRANTED_ERROR, "해당 게시글을 삭제할 수 있는 권한이 없습니다.");
    }

    @Override
    @Transactional
    public StudyRoomResponseDto joinStudyRoom(Long id) {
        Optional<StudyRoom> optionalStudyRoom = studyRoomRepository.findById(id);
        if(optionalStudyRoom.isEmpty()) {
            throw new CustomException(ErrorCode.STUDYROOM_NOT_FOUND_ERROR, "존재하지 않는 스터디룸입니다.");
        }

        StudyRoom studyRoom = optionalStudyRoom.get();

        if(studyRoom.getJoinCount() >= studyRoom.getJoinableCount()) {
            throw new CustomException(ErrorCode.VALIDATION_ERROR, "가입 정원이 가득찼습니다.");
        }

        User user = userRepository.getByUserid(SecurityUtil.getCurrentUserId());

        // 이미 가입했는지 확인
        for(StudyRoomUser studyRoomUser: studyRoom.getStudyRoomUsers()) {
            if(studyRoomUser.getUser().getUserid().equals(user.getUserid())) {
                throw new CustomException(ErrorCode.VALIDATION_ERROR, "이미 가입중인 상태입니다.");
            }
        }
        StudyRoomUser studyRoomUser = new StudyRoomUser();
        studyRoomUser.setStudyRoomLevel(StudyRoomLevel.NORMAL);
        studyRoomUser.setUser(user);
        studyRoomUser.setStudyRoom(studyRoom);
        if(studyRoom.isAutoJoin()) {
            studyRoom.addJoinCount();
            studyRoomUser.setStatus(true);
        }
        else {
            studyRoomUser.setStatus(false);
        }

        studyRoom.addStudyRoomUser(studyRoomUser);

        StudyRoom savedStudyRoom = studyRoomRepository.save(studyRoom);
        return StudyRoomResponseDto.toDto(savedStudyRoom);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudyRoomJoinResponseDto> studyRoomRequestList() {
        User leader = userRepository.getByUserid(SecurityUtil.getCurrentUserId());
        List<StudyRoomUser> studyRoomUsers = studyRoomUserRepository.findAllByUser(leader);
        List<StudyRoomJoinResponseDto> studyRoomJoinResponseDtos = new ArrayList<>();

        for(StudyRoomUser studyRoomUser: studyRoomUsers) {
            StudyRoomLevel level = studyRoomUser.getStudyRoomLevel();
            if(level.equals(StudyRoomLevel.LEADER) || level.equals(StudyRoomLevel.SUB_LEADER)) {
                List<StudyRoomUser> studyRoomUserRequests = studyRoomUserRepository.findAllByStudyRoom(studyRoomUser.getStudyRoom());
                for(StudyRoomUser studyRoomUserRequest: studyRoomUserRequests) {
                    if(!studyRoomUserRequest.isStatus()) {
                        studyRoomJoinResponseDtos.add(StudyRoomJoinResponseDto.toDto(studyRoomUser.getStudyRoom(), studyRoomUserRequest, studyRoomUserRequest.getUser()));
                    }
                }
            }
        }
        return studyRoomJoinResponseDtos;
    }

    @Override
    @Transactional
    public StudyRoomResponseDto acceptJoinStudyRoom(Long studyRoomId, Long studyRoomUserId) {
        User user = userRepository.getByUserid(SecurityUtil.getCurrentUserId());
        Optional<StudyRoom> optionalStudyRoom = studyRoomRepository.findById(studyRoomId);
        if(optionalStudyRoom.isEmpty()) {
            throw new CustomException(ErrorCode.STUDYROOM_NOT_FOUND_ERROR, "존재하지 않는 스터디룸 입니다.");
        }
        StudyRoom studyRoom = optionalStudyRoom.get();
        Optional<StudyRoomUser> optionalStudyRoomUser = studyRoomUserRepository.findByStudyRoomAndUser(studyRoom, user);
        if(optionalStudyRoomUser.isEmpty()) {
            throw new CustomException(ErrorCode.VALIDATION_ERROR, "해당 유저는 권한이 없습니다.");
        }
        StudyRoomUser studyRoomUser = optionalStudyRoomUser.get();
        if(!(studyRoomUser.getStudyRoomLevel().equals(StudyRoomLevel.LEADER) || studyRoomUser.getStudyRoomLevel().equals(StudyRoomLevel.SUB_LEADER))) {
            throw new CustomException(ErrorCode.VALIDATION_ERROR, "해당 유저는 권한이 없습니다.");
        }

        Optional<StudyRoomUser> optionalRequestStudyRoomUser = studyRoomUserRepository.findById(studyRoomUserId);
        if(optionalRequestStudyRoomUser.isEmpty()) {
            throw new CustomException(ErrorCode.VALIDATION_ERROR, "해당 유저는 가입신청 상태가 아닙니다.");
        }

        StudyRoomUser requestStudyRoomUser = optionalRequestStudyRoomUser.get();
        if(requestStudyRoomUser.isStatus()) {
            throw new CustomException(ErrorCode.VALIDATION_ERROR, "해당 유저는 이미 가입중입니다.");
        }
        if(studyRoom.getJoinCount() >= studyRoom.getJoinableCount()) {
            throw new CustomException(ErrorCode.VALIDATION_ERROR, "스터디룸 가입 정원이 가득 찼습니다.");
        }
        studyRoom.addJoinCount();
        StudyRoom savedStudyRoom = studyRoomRepository.save(studyRoom);
        requestStudyRoomUser.setStatus(true);
        studyRoomUserRepository.save(requestStudyRoomUser);
        return StudyRoomResponseDto.toDto(savedStudyRoom);
    }

    @Override
    @Transactional
    public void deleteStudyRoomUser(Long id) {
        User user = userRepository.getByUserid(SecurityUtil.getCurrentUserId());
        StudyRoom studyRoom = studyRoomRepository.findById(id).get();
        List<StudyRoomUser> studyRoomUsers = studyRoom.getStudyRoomUsers();
        for(StudyRoomUser studyRoomUser: studyRoomUsers) {
            if(user.getUserid().equals(studyRoomUser.getUser().getUserid())) {
                if(isLeader(studyRoomUser)) {
                    throw new CustomException(ErrorCode.VALIDATION_ERROR, "방장은 탈퇴할 수 없습니다.");
                }
                else {
                    studyRoom.subJoinCount();
                    studyRoomUsers.remove(studyRoomUser);
                    studyRoom.setStudyRoomUsers(studyRoomUsers);
                    studyRoomRepository.save(studyRoom);
                }
            }
        }
        throw new CustomException(ErrorCode.USER_NOT_FOUND_ERROR, "해당 유저는 스터디룸 가입자가 아닙니다.");
    }

    public boolean isLeader(StudyRoomUser studyRoomUser) {
        if(studyRoomUser.getStudyRoomLevel().equals(StudyRoomLevel.LEADER))
            return true;
        return false;
    }
}
