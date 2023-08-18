package com.dtalks.dtalks.studyroom.service;

import com.dtalks.dtalks.exception.ErrorCode;
import com.dtalks.dtalks.exception.exception.CustomException;
import com.dtalks.dtalks.studyroom.dto.StudyRoomPostDto;
import com.dtalks.dtalks.studyroom.dto.StudyRoomPostRequestDto;
import com.dtalks.dtalks.studyroom.entity.StudyRoomPost;
import com.dtalks.dtalks.studyroom.entity.StudyRoom;
import com.dtalks.dtalks.studyroom.entity.StudyRoomUser;
import com.dtalks.dtalks.studyroom.repository.StudyRoomPostRepository;
import com.dtalks.dtalks.studyroom.repository.StudyRoomRepository;
import com.dtalks.dtalks.user.Util.SecurityUtil;
import com.dtalks.dtalks.user.entity.User;
import com.dtalks.dtalks.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class StudyRoomPostServiceImpl implements StudyRoomPostService {

    private final StudyRoomRepository studyRoomRepository;
    private final StudyRoomPostRepository studyRoomPostRepository;
    private final UserRepository userRepository;
    @Override
    @Transactional
    public StudyRoomPostDto addPost(Long studyRoomId, StudyRoomPostRequestDto studyRoomPostRequestDto) {
        User user = checkUser();
        StudyRoom studyRoom = checkStudyRoom(studyRoomId);
        checkUserJoinedStudyRoom(user, studyRoom);

        StudyRoomPost studyRoomPost = StudyRoomPost.toEntity(studyRoomPostRequestDto, user, studyRoom);
        StudyRoomPost savedStudyRoomPost = studyRoomPostRepository.save(studyRoomPost);

        return StudyRoomPostDto.toDto(savedStudyRoomPost);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<StudyRoomPostDto> getPostsByStudyRooms(Long studyRoomId, Pageable pageable) {
        User user = checkUser();
        StudyRoom studyRoom = checkStudyRoom(studyRoomId);
        checkUserJoinedStudyRoom(user, studyRoom);

        Page<StudyRoomPost> post = studyRoomPostRepository.findByStudyRoom(studyRoom, pageable);
        return post.map(StudyRoomPostDto::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public StudyRoomPostDto getPost(Long studyRoomId, Long postId) {
        User user = checkUser();
        StudyRoom studyRoom = checkStudyRoom(studyRoomId);
        checkUserJoinedStudyRoom(user, studyRoom);

        StudyRoomPost studyRoomPost = studyRoomPostRepository.findById(postId).orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND_ERROR, "해당 포스트를 찾을 수 없습니다."));
        if(!isStudyRoomHasPost(studyRoom, studyRoomPost)) throw new CustomException(ErrorCode.VALIDATION_ERROR, "해당 스터디룸에 포스트가 존재하지 않습니다");
        studyRoomPost.addViewCount();
        return StudyRoomPostDto.toDto(studyRoomPost);
    }

    @Override
    @Transactional
    public StudyRoomPostDto changePost(Long studyRoomId, Long postId, StudyRoomPostRequestDto studyRoomPostRequestDto) {
        User user = checkUser();
        StudyRoom studyRoom = checkStudyRoom(studyRoomId);
        checkUserJoinedStudyRoom(user, studyRoom);

        StudyRoomPost studyRoomPost = studyRoomPostRepository.findById(postId).orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND_ERROR, "해당 포스트를 찾을 수 없습니다."));
        checkUserPostOwner(user, studyRoomPost);

        studyRoomPost.setTitle(studyRoomPostRequestDto.getTitle());
        studyRoomPost.setContent(studyRoomPostRequestDto.getContent());
        studyRoomPost.setCategory(studyRoomPostRequestDto.getCategory());
        return StudyRoomPostDto.toDto(studyRoomPost);
    }

    @Override
    @Transactional
    public void removePost(Long studyRoomId, Long postId) {
        User user = checkUser();
        StudyRoom studyRoom = checkStudyRoom(studyRoomId);
        checkUserJoinedStudyRoom(user, studyRoom);

        StudyRoomPost studyRoomPost = studyRoomPostRepository.findById(postId).orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND_ERROR, "해당 포스트를 찾을 수 없습니다."));
        checkUserPostOwner(user, studyRoomPost);
        studyRoomPostRepository.delete(studyRoomPost);
    }

    private boolean isStudyRoomHasPost(StudyRoom studyRoom, StudyRoomPost studyRoomPost) {
        if(studyRoom.getStudyRoomPosts().contains(studyRoomPost)) return true;
        return false;
    }

    private User checkUser() {
        return userRepository.findByUserid(SecurityUtil.getCurrentUserId()).orElseThrow(() -> new CustomException(ErrorCode.VALIDATION_ERROR, "유저를 찾을 수 없습니다."));
    }

    private StudyRoom checkStudyRoom(Long studyRoomId) {
        return studyRoomRepository.findById(studyRoomId).orElseThrow(() -> new CustomException(ErrorCode.VALIDATION_ERROR, "스터디룸을 찾을 수 없습니다."));
    }

    private boolean checkUserJoinedStudyRoom(User user, StudyRoom studyRoom) {
        for(StudyRoomUser studyRoomUser: user.getStudyRoomUserList()) {
            if(studyRoomUser.getStudyRoom().getId() == studyRoom.getId()) return true;
        }
        throw new CustomException(ErrorCode.VALIDATION_ERROR, "해당 유저는 해당 스터디룸 가입자가 아닙니다.");
    }

    private boolean checkUserPostOwner(User user, StudyRoomPost studyRoomPost) {
        if(studyRoomPost.getUser().getId() == user.getId()) return true;
        throw new CustomException(ErrorCode.VALIDATION_ERROR, "해당 유저는 게시글 작성자가 아닙니다.");
    }
}
