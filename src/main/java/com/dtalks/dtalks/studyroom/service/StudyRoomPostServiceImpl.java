package com.dtalks.dtalks.studyroom.service;

import com.dtalks.dtalks.exception.ErrorCode;
import com.dtalks.dtalks.exception.exception.CustomException;
import com.dtalks.dtalks.studyroom.dto.PostDto;
import com.dtalks.dtalks.studyroom.dto.PostRequestDto;
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

import java.util.HashMap;

@Slf4j
@RequiredArgsConstructor
@Service
public class StudyRoomPostServiceImpl implements StudyRoomPostService {

    private final StudyRoomRepository studyRoomRepository;
    private final StudyRoomPostRepository studyRoomPostRepository;
    private final UserRepository userRepository;
    @Override
    @Transactional
    public PostDto addPost(Long studyRoomId, PostRequestDto postRequestDto) {
        HashMap<String, Object> map = checkValidation(studyRoomId);
        StudyRoomPost studyRoomPost = StudyRoomPost.toEntity(postRequestDto, (User) map.get("user"), (StudyRoom) map.get("studyRoom"));
        StudyRoomPost savedStudyRoomPost = studyRoomPostRepository.save(studyRoomPost);

        return PostDto.toDto(savedStudyRoomPost);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PostDto> getPostsByStudyRooms(Long studyRoomId, Pageable pageable) {
        HashMap<String, Object> map = checkValidation(studyRoomId);
        Page<StudyRoomPost> post = studyRoomPostRepository.findByStudyRoom((StudyRoom) map.get("studyRoom"), pageable);
        return post.map(PostDto::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public PostDto getPost(Long studyRoomId, Long postId) {
        HashMap<String, Object> map = checkValidation(studyRoomId);
        StudyRoomPost studyRoomPost = studyRoomPostRepository.findById(postId).orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND_ERROR, "해당 포스트를 찾을 수 없습니다."));
        if(!isStudyRoomHasPost((StudyRoom) map.get("studyRoom"), studyRoomPost)) throw new CustomException(ErrorCode.VALIDATION_ERROR, "해당 스터디룸에 포스트가 존재하지 않습니다");
        studyRoomPost.addViewCount();
        return PostDto.toDto(studyRoomPost);
    }

    @Override
    @Transactional
    public PostDto changePost(Long studyRoomId, Long postId, PostRequestDto postRequestDto) {
        HashMap<String, Object> map = checkValidation(studyRoomId);
        StudyRoomPost studyRoomPost = studyRoomPostRepository.findById(postId).orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND_ERROR, "해당 포스트를 찾을 수 없습니다."));
        if(!isUserPostOwner((User) map.get("user"), studyRoomPost)) throw new CustomException(ErrorCode.VALIDATION_ERROR, "해당 유저는 포스트의 주인이 아닙니다.");
        studyRoomPost.setTitle(postRequestDto.getTitle());
        studyRoomPost.setContent(postRequestDto.getContent());
        studyRoomPost.setCategory(postRequestDto.getCategory());
        return PostDto.toDto(studyRoomPost);
    }

    @Override
    @Transactional
    public void removePost(Long studyRoomId, Long postId) {
        HashMap<String, Object> map = checkValidation(studyRoomId);
        StudyRoomPost studyRoomPost = studyRoomPostRepository.findById(postId).orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND_ERROR, "해당 포스트를 찾을 수 없습니다."));
        if(!isUserPostOwner((User) map.get("user"), studyRoomPost)) throw new CustomException(ErrorCode.VALIDATION_ERROR, "해당 유저는 포스트의 주인이 아닙니다.");
        studyRoomPostRepository.delete(studyRoomPost);
    }

    private boolean isStudyRoomHasPost(StudyRoom studyRoom, StudyRoomPost studyRoomPost) {
        if(studyRoom.getStudyRoomPosts().contains(studyRoomPost)) return true;
        return false;
    }
    @Transactional(readOnly = true)
    private HashMap<String, Object> checkValidation(Long studyRoomId) {
        HashMap<String, Object> map = new HashMap<>();
        User user = userRepository.findByUserid(SecurityUtil.getCurrentUserId()).orElseThrow(
                () -> new CustomException(ErrorCode.VALIDATION_ERROR, "유저를 찾을 수 없습니다.")
        );
        StudyRoom studyRoom = studyRoomRepository.findById(studyRoomId).orElseThrow(
                () -> new CustomException(ErrorCode.VALIDATION_ERROR, "스터디룸을 찾을 수 없습니다.")
        );
        if(!isUserJoinedStudyRoom(user, studyRoom)) throw new CustomException(ErrorCode.VALIDATION_ERROR, "해당 유저는 스터디룸 가입자가 아닙니다.");
        map.put("user", user);
        map.put("studyRoom", studyRoom);

        return map;
    }

    private boolean isUserJoinedStudyRoom(User user, StudyRoom studyRoom) {
        for(StudyRoomUser studyRoomUser: user.getStudyRoomUserList()) {
            if(studyRoomUser.getStudyRoom().getId() == studyRoom.getId()) return true;
        }
        return false;
    }

    private boolean isUserPostOwner(User user, StudyRoomPost studyRoomPost) {
        if(studyRoomPost.getUser().getId() == user.getId()) return true;
        return false;
    }
}
