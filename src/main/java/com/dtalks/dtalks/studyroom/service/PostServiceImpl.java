package com.dtalks.dtalks.studyroom.service;

import com.dtalks.dtalks.exception.ErrorCode;
import com.dtalks.dtalks.exception.exception.CustomException;
import com.dtalks.dtalks.studyroom.dto.PostDto;
import com.dtalks.dtalks.studyroom.dto.PostRequestDto;
import com.dtalks.dtalks.studyroom.entity.Post;
import com.dtalks.dtalks.studyroom.entity.StudyRoom;
import com.dtalks.dtalks.studyroom.entity.StudyRoomUser;
import com.dtalks.dtalks.studyroom.repository.PostRepository;
import com.dtalks.dtalks.studyroom.repository.StudyRoomRepository;
import com.dtalks.dtalks.user.Util.SecurityUtil;
import com.dtalks.dtalks.user.entity.User;
import com.dtalks.dtalks.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;

@RequiredArgsConstructor
@Service
public class PostServiceImpl implements PostService {

    private final StudyRoomRepository studyRoomRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    @Override
    @Transactional
    public PostDto addPost(PostRequestDto postRequestDto) {
        HashMap<String, Object> map = checkValidation(postRequestDto.getStudyRoomId());
        Post post = Post.toEntity(postRequestDto, (User)map.get("user"), (StudyRoom)map.get("studyRoom"));
        Post savedPost = postRepository.save(post);

        return PostDto.toDto(savedPost);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PostDto> getPostsByStudyRooms(Long studyRoomId, Pageable pageable) {
        HashMap<String, Object> map = checkValidation(studyRoomId);
        Page<Post> post = postRepository.findByStudyRoom((StudyRoom)map.get("studyRoom"), pageable);
        return post.map(PostDto::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public PostDto getPost(Long studyRoomId, Long postId) {
        HashMap<String, Object> map = checkValidation(studyRoomId);

        return null;
    }

    @Override
    public PostDto changePost(PostRequestDto postRequestDto) {
        return null;
    }

    @Override
    public void removePost(Long postId) {

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
        if(isUserJoinedStudyRoom(user, studyRoom)) throw new CustomException(ErrorCode.VALIDATION_ERROR, "해당 유저는 스터디룸 가입자가 아닙니다.");
        map.put("user", user);
        map.put("studyRoom", studyRoom);

        return map;
    }

    private boolean isUserJoinedStudyRoom(User user, StudyRoom studyRoom) {
        for(StudyRoomUser studyRoomUser: user.getStudyRoomUserList()) {
            if(studyRoomUser.getStudyRoom().getId() == studyRoom.getId()) return true;
            return false;
        }
    }
}
