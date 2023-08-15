package com.dtalks.dtalks.studyroom.service;

import com.dtalks.dtalks.exception.ErrorCode;
import com.dtalks.dtalks.exception.exception.CustomException;
import com.dtalks.dtalks.studyroom.dto.PostDto;
import com.dtalks.dtalks.studyroom.dto.PostRequestDto;
import com.dtalks.dtalks.studyroom.entity.Post;
import com.dtalks.dtalks.studyroom.entity.StudyRoom;
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

@RequiredArgsConstructor
@Service
public class PostServiceImpl implements PostService {

    private final StudyRoomRepository studyRoomRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    @Override
    @Transactional
    public PostDto addPost(PostRequestDto postRequestDto) {
        User user = userRepository.findByUserid(SecurityUtil.getCurrentUserId()).orElseThrow(
                () -> new CustomException(ErrorCode.VALIDATION_ERROR, "유저를 찾을 수 없습니다.")
        );
        StudyRoom studyRoom = studyRoomRepository.findById(postRequestDto.getStudyRoomId()).orElseThrow(
                () -> new CustomException(ErrorCode.VALIDATION_ERROR, "스터디룸을 찾을 수 없습니다.")
        );

        Post post = Post.toEntity(postRequestDto, user, studyRoom);
        Post savedPost = postRepository.save(post);

        return PostDto.toDto(savedPost);
    }

    @Override
    public Page<PostDto> getPostsByStudyRooms(Long studyRoomId, Pageable pageable) {
        return null;
    }

    @Override
    public PostDto getPost(Long postId) {
        return null;
    }

    @Override
    public PostDto changePost(PostRequestDto postRequestDto) {
        return null;
    }

    @Override
    public void removePost(Long postId) {

    }

    private boolean isUserJoinedStudyRoom(User user, StudyRoom studyRoom) {
        for(Study)
    }
}
