package com.dtalks.dtalks.studyroom.service;

import com.dtalks.dtalks.exception.ErrorCode;
import com.dtalks.dtalks.exception.exception.CustomException;
import com.dtalks.dtalks.notification.dto.NotificationRequestDto;
import com.dtalks.dtalks.notification.entity.Notification;
import com.dtalks.dtalks.notification.enums.NotificationType;
import com.dtalks.dtalks.notification.enums.ReadStatus;
import com.dtalks.dtalks.notification.repository.NotificationRepository;
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
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StudyRoomServiceImpl implements StudyRoomService{

    private final Logger LOGGER = LoggerFactory.getLogger(StudyRoomServiceImpl.class);

    private final StudyRoomRepository studyRoomRepository;
    private final StudyRoomUserRepository studyRoomUserRepository;
    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;
    private final ChatService chatService;

    private final ApplicationEventPublisher applicationEventPublisher;
    private final MessageSource messageSource;

    @Override
    @Transactional
    public StudyRoomResponseDto createStudyRoom(StudyRoomRequestDto studyRoomRequestDto) {
        LOGGER.info("createStudyRoom service 호출됨");
        User user = SecurityUtil.getUser();
        StudyRoom studyRoom = StudyRoom.toEntity(studyRoomRequestDto);
        StudyRoomUser studyRoomUser = StudyRoomUser.toEntity(user, studyRoom, StudyRoomLevel.LEADER, true);
        List<StudyRoomUser> studyRoomUsers = new ArrayList<>();

        studyRoomUsers.add(studyRoomUser);
        studyRoom.setStudyRoomUsers(studyRoomUsers);
        StudyRoom savedStudyroom = studyRoomRepository.save(studyRoom);
        chatService.createRoom(savedStudyroom.getId());

        StudyRoomResponseDto studyRoomResponseDto = StudyRoomResponseDto.toDto(savedStudyroom);

        return studyRoomResponseDto;
    }

    @Override
    @Transactional(readOnly = true)
    public StudyRoomResponseDto findStudyRoomById(Long studyRoomId) {
        Optional<StudyRoom> studyRoom = studyRoomRepository.findById(studyRoomId);
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
        if(studyRoomUsers.size() > 1) {
            throw new CustomException(ErrorCode.VALIDATION_ERROR, "본인 이외의 스터디원이 남아있으면 삭제가 불가능합니다.");
        }

        List<NotificationType> types = Arrays.asList(NotificationType.STUDY_JOIN_REQUEST, NotificationType.STUDY_MEMBER_AUTO_JOIN,
                NotificationType.STUDY_MEMBER_QUIT, NotificationType.STUDY_LEVEL_UPDATE, NotificationType.STUDY_REQUEST_ACCEPTED);
        List<Notification> deletableNoti = notificationRepository.findByRefIdAndTypeIn(studyRoom.getId(), types);
        for (Notification notification : deletableNoti) {
            if (notification.getReadStatus().equals(ReadStatus.READ)) {
                notification.readDataDeleteSetting();
            } else {
                notificationRepository.deleteById(notification.getId());
            }
        }

        for(StudyRoomUser studyRoomUser: studyRoomUsers) {
            if(studyRoomUser.getStudyRoomLevel().equals(StudyRoomLevel.LEADER)) {
                if(studyRoomUser.getUser().getUserid().equals(SecurityUtil.getUser().getUserid())) {
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

        User user = SecurityUtil.getUser();

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

        Optional<StudyRoomUser> leader = studyRoomUserRepository.findByStudyRoomIdAndStudyRoomLevel(studyRoom.getId(), StudyRoomLevel.LEADER);
        Optional<StudyRoomUser> sub_leader = studyRoomUserRepository.findByStudyRoomIdAndStudyRoomLevel(studyRoom.getId(), StudyRoomLevel.SUB_LEADER);

        NotificationType type = NotificationType.STUDY_JOIN_REQUEST;;
        String message = messageSource.getMessage("notification.study.request", new Object[]{studyRoom.getTitle(), user.getNickname()}, null);
        if (studyRoom.isAutoJoin()) {
            type = NotificationType.STUDY_MEMBER_AUTO_JOIN;
            message = messageSource.getMessage("notification.study.join", new Object[]{studyRoom.getTitle(), user.getNickname()}, null);
        }

        applicationEventPublisher.publishEvent(NotificationRequestDto.toDto(studyRoom.getId(), studyRoom.getId(), leader.get().getUser(),
                type, message));
        if (sub_leader.isPresent()) {
            applicationEventPublisher.publishEvent(NotificationRequestDto.toDto(studyRoom.getId(), studyRoom.getId(), sub_leader.get().getUser(),
                    type, message));
        }

        return StudyRoomResponseDto.toDto(savedStudyRoom);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<StudyRoomJoinResponseDto> studyRoomRequestList(Pageable pageable) {
        User leader = SecurityUtil.getUser();
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
        int start = (int)pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), studyRoomJoinResponseDtos.size());
        return new PageImpl<>(studyRoomJoinResponseDtos.subList(start, end), pageable, studyRoomJoinResponseDtos.size());
    }

    @Override
    @Transactional
    public StudyRoomResponseDto acceptJoinStudyRoom(Long studyRoomId, Long studyRoomUserId, boolean status) {
        User user = SecurityUtil.getUser();
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

        User requestUser = requestStudyRoomUser.getUser();
        if(!status) {
            studyRoom.getStudyRoomUsers().remove(requestStudyRoomUser);
            requestUser.getStudyRoomUserList().remove(requestStudyRoomUser);
            studyRoomUserRepository.delete(requestStudyRoomUser);
            applicationEventPublisher.publishEvent(NotificationRequestDto.toDto(studyRoom.getId(), null, requestUser,
                    NotificationType.STUDY_REQUEST_DENIED, messageSource.getMessage("notification.study.request.denied", new Object[]{studyRoom.getTitle()}, null)));
            return StudyRoomResponseDto.toDto(studyRoom);
        }

        if (studyRoom.getJoinCount() >= studyRoom.getJoinableCount()) {
            throw new CustomException(ErrorCode.VALIDATION_ERROR, "스터디룸 가입 정원이 가득 찼습니다.");
        }
        studyRoom.addJoinCount();
        requestStudyRoomUser.setStatus(true);
        StudyRoom savedStudyRoom = studyRoomRepository.save(studyRoom);
        studyRoomUserRepository.save(requestStudyRoomUser);

        applicationEventPublisher.publishEvent(NotificationRequestDto.toDto(studyRoom.getId(), studyRoom.getId(), requestUser,
                NotificationType.STUDY_REQUEST_ACCEPTED, messageSource.getMessage("notification.study.request.accepted", new Object[]{studyRoom.getTitle()}, null)));
        return StudyRoomResponseDto.toDto(savedStudyRoom);
    }

    @Override
    @Transactional
    public void deleteStudyRoomUser(Long id) {
        User user = SecurityUtil.getUser();
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

                    Optional<StudyRoomUser> leader = studyRoomUserRepository.findByStudyRoomIdAndStudyRoomLevel(studyRoom.getId(), StudyRoomLevel.LEADER);
                    Optional<StudyRoomUser> sub_leader = studyRoomUserRepository.findByStudyRoomIdAndStudyRoomLevel(studyRoom.getId(), StudyRoomLevel.SUB_LEADER);

                    applicationEventPublisher.publishEvent(NotificationRequestDto.toDto(studyRoom.getId(), studyRoom.getId(), leader.get().getUser(),
                            NotificationType.STUDY_MEMBER_QUIT, messageSource.getMessage("notification.study.member.quit", new Object[]{studyRoom.getTitle(), user.getNickname()}, null)));
                    if (sub_leader.isPresent()) {
                        applicationEventPublisher.publishEvent(NotificationRequestDto.toDto(studyRoom.getId(), studyRoom.getId(), sub_leader.get().getUser(),
                                NotificationType.STUDY_MEMBER_QUIT, messageSource.getMessage("notification.study.member.quit", new Object[]{studyRoom.getTitle(), user.getNickname()}, null)));
                    }
                    studyRoomUserRepository.delete(studyRoomUser);
                    return;
                }
            }
        }
        throw new CustomException(ErrorCode.USER_NOT_FOUND_ERROR, "해당 유저는 스터디룸 가입자가 아닙니다.");
    }

    @Override
    @Transactional
    public StudyRoomResponseDto expelStudyRoomUser(Long studyRoomId, String nickname) {
        User user = SecurityUtil.getUser();
        Optional<StudyRoom> optionalStudyRoom = studyRoomRepository.findById(studyRoomId);
        if(optionalStudyRoom.isEmpty()) {
            throw new CustomException(ErrorCode.STUDYROOM_NOT_FOUND_ERROR, "존재하지 않는 스터디룸 입니다.");
        }

        StudyRoom studyRoom = optionalStudyRoom.get();
        Optional<StudyRoomUser> optionalStudyRoomUser = studyRoomUserRepository.findByStudyRoomAndUser(studyRoom, user);
        if(optionalStudyRoomUser.isEmpty()) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND_ERROR, "당신은 스터디룸 가입자가 아닙니다.");
        }

        StudyRoomUser studyRoomUser = optionalStudyRoomUser.get();
        if(!isLeader(studyRoomUser)) {
            throw new CustomException(ErrorCode.VALIDATION_ERROR, "당신은 스터디룸 방장이 아닙니다.");
        }

        Optional<User> optionalUser = userRepository.findByNickname(nickname);
        if (optionalUser.isEmpty()) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND_ERROR, "유저를 찾을 수 없습니다.");
        }

        User expeledUser = optionalUser.get();
        if(user.getNickname().equals(expeledUser.getNickname())) {
            throw new CustomException(ErrorCode.VALIDATION_ERROR, "자기 자신을 강퇴할 수 없습니다.");
        }

        Optional<StudyRoomUser> optionalExpelStudyRoomUser = studyRoomUserRepository.findByStudyRoomAndUser(studyRoom, expeledUser);
        if(optionalExpelStudyRoomUser.isEmpty()) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND_ERROR, "강퇴할 유저는 스터디룸 가입자가 아닙니다.");
        }

        StudyRoomUser expelStudyRoomUser = optionalExpelStudyRoomUser.get();
        studyRoom.getStudyRoomUsers().remove(expelStudyRoomUser);

        applicationEventPublisher.publishEvent(NotificationRequestDto.toDto(studyRoom.getId(), null, expeledUser,
                NotificationType.STUDY_EXPELLED, messageSource.getMessage("notification.study.member.expelled", new Object[]{studyRoom.getTitle()}, null)));

        studyRoomUserRepository.delete(expelStudyRoomUser);
        studyRoom.subJoinCount();
        return StudyRoomResponseDto.toDto(studyRoom);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<StudyRoomResponseDto> JoinedStudyRoomList(Pageable pageable) {
        User user = SecurityUtil.getUser();
        List<StudyRoomUser> studyRoomUsers = studyRoomUserRepository.findAllByUser(user);
        List<StudyRoomResponseDto> studyRoomResponseDtos = new ArrayList<>();

        for(StudyRoomUser studyRoomUser: studyRoomUsers) {
            studyRoomResponseDtos.add(StudyRoomResponseDto.toDto(studyRoomUser.getStudyRoom()));
        }

        int start = (int)pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), studyRoomResponseDtos.size());

        return new PageImpl<>(studyRoomResponseDtos.subList(start, end), pageable, studyRoomResponseDtos.size());
    }

    @Override
    @Transactional
    public StudyRoomResponseDto changeAuthority(Long studyRoomId, Long studyRoomUserId, StudyRoomLevel studyRoomLevel) {
        User user = SecurityUtil.getUser();

        Optional<StudyRoom> optionalStudyRoom = studyRoomRepository.findById(studyRoomId);
        if(optionalStudyRoom.isEmpty()) {
            throw new CustomException(ErrorCode.STUDYROOM_NOT_FOUND_ERROR, "존재하지 않는 스터디룸 입니다.");
        }

        StudyRoom studyRoom = optionalStudyRoom.get();

        Optional<StudyRoomUser> optionalOwnerStudyRoomUser = studyRoomUserRepository.findByStudyRoomAndUser(studyRoom, user);
        if(optionalOwnerStudyRoomUser.isEmpty()) {
            throw new CustomException(ErrorCode.VALIDATION_ERROR, "요청자는 스터디룸 가입자가 아닙니다.");
        }

        StudyRoomUser ownerStudyRoomUser = optionalOwnerStudyRoomUser.get();
        if(!ownerStudyRoomUser.getStudyRoomLevel().equals(StudyRoomLevel.LEADER)) {
            throw new CustomException(ErrorCode.VALIDATION_ERROR, "요청자는 리더가 아닙니다.");
        }

        Optional<StudyRoomUser> optionalStudyRoomUser = studyRoomUserRepository.findById(studyRoomUserId);
        if(optionalStudyRoomUser.isEmpty()) {
            throw new CustomException(ErrorCode.VALIDATION_ERROR, "해당 유저는 스터디룸 가입 상태가 아닙니다.");
        }

        StudyRoomUser studyRoomUser = optionalStudyRoomUser.get();
        if(!studyRoomUser.isStatus()) {
            throw new CustomException(ErrorCode.VALIDATION_ERROR, "해당 유저는 스터디룸 가입 상태가 아닙니다.");
        }

        if(studyRoomLevel.equals(StudyRoomLevel.LEADER)) {
            ownerStudyRoomUser.setStudyRoomLevel(StudyRoomLevel.NORMAL);
            studyRoomUser.setStudyRoomLevel(StudyRoomLevel.LEADER);

            applicationEventPublisher.publishEvent(NotificationRequestDto.toDto(studyRoom.getId(), studyRoom.getId(), ownerStudyRoomUser.getUser(),
                    NotificationType.STUDY_LEVEL_UPDATE, messageSource.getMessage("notification.study.authority.change", new Object[]{studyRoom.getTitle(), "스터디원"}, null)));
        }
        else {
            studyRoomUser.setStudyRoomLevel(studyRoomLevel);
        }

        String authority = studyRoomLevel.equals(StudyRoomLevel.LEADER) ? "방장" : (studyRoomLevel.equals(StudyRoomLevel.SUB_LEADER) ? "부방장" : "스터디원");
        applicationEventPublisher.publishEvent(NotificationRequestDto.toDto(studyRoom.getId(), studyRoom.getId(), studyRoomUser.getUser(),
                NotificationType.COMMENT, messageSource.getMessage("notification.study.authority.change", new Object[]{studyRoom.getTitle(), authority}, null)));

        studyRoomUserRepository.save(ownerStudyRoomUser);
        studyRoomUserRepository.save(studyRoomUser);

        return StudyRoomResponseDto.toDto(studyRoomRepository.findById(studyRoomId).get());
    }

    public boolean isLeader(StudyRoomUser studyRoomUser) {
        if(studyRoomUser.getStudyRoomLevel().equals(StudyRoomLevel.LEADER))
            return true;
        return false;
    }
}
