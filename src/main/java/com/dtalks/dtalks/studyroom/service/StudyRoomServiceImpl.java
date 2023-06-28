package com.dtalks.dtalks.studyroom.service;

import com.dtalks.dtalks.alarm.entity.Alarm;
import com.dtalks.dtalks.alarm.enums.AlarmType;
import com.dtalks.dtalks.alarm.repository.AlarmRepository;
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
import com.dtalks.dtalks.user.entity.Activity;
import com.dtalks.dtalks.user.entity.User;
import com.dtalks.dtalks.user.enums.ActivityType;
import com.dtalks.dtalks.user.repository.ActivityRepository;
import com.dtalks.dtalks.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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
    private final ActivityRepository activityRepository;
    private final AlarmRepository alarmRepository;

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

        activityRepository.save(Activity.createStudy(user, studyRoom, ActivityType.STUDY_CREATE));
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
        for(StudyRoomUser studyRoomUser: studyRoomUsers) {
            if(studyRoomUser.getStudyRoomLevel().equals(StudyRoomLevel.LEADER)) {
                if(studyRoomUser.getUser().getUserid().equals(SecurityUtil.getUser().getUserid())) {
                    studyRoomRepository.delete(studyRoom);

                    List<Activity> studyActivities = activityRepository.findByStudyRoomId(id);
                    for (Activity activity : studyActivities) {
                        activity.setStudyRoom(null);
                    }
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
        activityRepository.save(Activity.createStudy(user, studyRoom, ActivityType.STUDY_JOIN_REQUEST));

        Optional<StudyRoomUser> leader = studyRoomUserRepository.findByStudyRoomIdAndStudyRoomLevel(studyRoom.getId(), StudyRoomLevel.LEADER);
        Optional<StudyRoomUser> sub_leader = studyRoomUserRepository.findByStudyRoomIdAndStudyRoomLevel(studyRoom.getId(), StudyRoomLevel.SUB_LEADER);

        String message = "\'" + studyRoom.getTitle() + "\'에 가입 신청이 들어왔습니다.";
        alarmRepository.save(Alarm.createAlarm(leader.get().getUser(), AlarmType.STUDY_JOIN_REQUEST, message, "/study-rooms/" + studyRoom.getId()));

        if (sub_leader.isPresent()) {
            alarmRepository.save(Alarm.createAlarm(sub_leader.get().getUser(), AlarmType.STUDY_JOIN_REQUEST, message, "/study-rooms/" + studyRoom.getId()));
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
            alarmRepository.save(Alarm.createAlarm(requestUser, AlarmType.STUDY_REQUEST_DENIED, "스터디 가입 신청이 거절되었습니다.", "/study-rooms/" + studyRoom.getId()));
            return StudyRoomResponseDto.toDto(studyRoom);
        }

        if (studyRoom.getJoinCount() >= studyRoom.getJoinableCount()) {
            throw new CustomException(ErrorCode.VALIDATION_ERROR, "스터디룸 가입 정원이 가득 찼습니다.");
        }
        studyRoom.addJoinCount();
        requestStudyRoomUser.setStatus(true);
        StudyRoom savedStudyRoom = studyRoomRepository.save(studyRoom);
        studyRoomUserRepository.save(requestStudyRoomUser);

        String message = "\'" + studyRoom.getTitle() + "\' 가입 신청이 승인되었습니다.";
        alarmRepository.save(Alarm.createAlarm(requestUser, AlarmType.STUDY_REQUEST_ACCEPTED, message, "/study-rooms/" + studyRoom.getId()));

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

                    activityRepository.save(Activity.createStudy(user, studyRoom, ActivityType.QUIT_STUDY));
                    Optional<StudyRoomUser> leader = studyRoomUserRepository.findByStudyRoomIdAndStudyRoomLevel(studyRoom.getId(), StudyRoomLevel.LEADER);
                    Optional<StudyRoomUser> sub_leader = studyRoomUserRepository.findByStudyRoomIdAndStudyRoomLevel(studyRoom.getId(), StudyRoomLevel.SUB_LEADER);

                    String message = "\'" + studyRoom.getTitle() + "\'에서 멤버가 나갔습니다.";
                    alarmRepository.save(Alarm.createAlarm(leader.get().getUser(), AlarmType.STUDY_MEMBER_QUIT, message, "/study-rooms/" + studyRoom.getId()));
                    if (sub_leader.isPresent()) {
                        alarmRepository.save(Alarm.createAlarm(sub_leader.get().getUser(), AlarmType.STUDY_MEMBER_QUIT, message, "/study-rooms/" + studyRoom.getId()));
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

        String message = "\'" + studyRoom.getTitle() + "\'에서 추방되었습니다.";
        alarmRepository.save(Alarm.createAlarm(expeledUser, AlarmType.STUDY_EXPELLED, message, "/study-rooms/" + studyRoom.getId()));

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

        String memberAlarmMessage = "";
        if(studyRoomLevel.equals(StudyRoomLevel.LEADER)) {
            ownerStudyRoomUser.setStudyRoomLevel(StudyRoomLevel.NORMAL);
            studyRoomUser.setStudyRoomLevel(StudyRoomLevel.LEADER);

            String ownerAlarmMessage = "\'" + studyRoom.getTitle() + "\'에서 권한이 멤버로 변경되었습니다.";
            memberAlarmMessage = "\'" + studyRoom.getTitle() + "\'에서 권한이 방장으로 변경되었습니다.";
            alarmRepository.save(Alarm.createAlarm(ownerStudyRoomUser.getUser(), AlarmType.STUDY_LEVEL_UPDATE, ownerAlarmMessage, "/study-rooms/" + studyRoom.getId()));
        }
        else {
            studyRoomUser.setStudyRoomLevel(studyRoomLevel);
            memberAlarmMessage = "\'" + studyRoom.getTitle() + "\'에서 권한이 변경됐습니다.";
        }

        alarmRepository.save(Alarm.createAlarm(studyRoomUser.getUser(), AlarmType.STUDY_LEVEL_UPDATE, memberAlarmMessage, "/study-rooms/" + studyRoom.getId()));

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
