package com.dtalks.dtalks.board.post.service;

import com.dtalks.dtalks.board.post.entity.Post;
import com.dtalks.dtalks.exception.exception.PermissionNotGrantedException;
import com.dtalks.dtalks.board.post.dto.PostDto;
import com.dtalks.dtalks.board.post.dto.PostRequestDto;
import com.dtalks.dtalks.board.post.repository.PostRepository;
import com.dtalks.dtalks.exception.ErrorCode;
import com.dtalks.dtalks.exception.exception.PostNotFoundException;
import com.dtalks.dtalks.exception.exception.UserNotFoundException;
import com.dtalks.dtalks.user.Util.SecurityUtil;
import com.dtalks.dtalks.user.entity.User;
import com.dtalks.dtalks.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class PostServiceImpl implements PostService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;

    @Autowired
    public PostServiceImpl(UserRepository userRepository, PostRepository postRepository) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public PostDto searchById(Long id) {
        Optional<Post> post = postRepository.findById(id);
        if (post.isEmpty()) {
            throw new PostNotFoundException(ErrorCode.POST_NOT_FOUND_ERROR, "해당하는 게시글이 존재하지 않습니다.");
        }
        return PostDto.toDto(post.get());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PostDto> searchAllPost(Pageable pageable) {
        Page<Post> postsPage = postRepository.findAllByOrderByIdDesc(pageable);
        return postsPage.map(PostDto::toDto);
    }

    @Override
    @Transactional
    public Long createPost(PostRequestDto postDto) {
        Optional<User> user = Optional.ofNullable(userRepository.getByUserid(SecurityUtil.getCurrentUserId()));
        if (user.isEmpty()) {
            throw new UserNotFoundException(ErrorCode.USER_NOT_FOUND_ERROR, "해당하는 유저가 존재하지 않습니다.");
        }
        Post post = Post.toEntity(postDto, user.get());
        postRepository.save(post);
        return post.getId();
    }

    @Override
    @Transactional
    public Long updatePost(PostRequestDto postDto, Long postId) {
        Optional<Post> optionalPost = postRepository.findById(postId);
        if (optionalPost.isEmpty()) {
            throw new PostNotFoundException(ErrorCode.POST_NOT_FOUND_ERROR, "해당하는 게시글이 존재하지 않습니다.");
        }

        Post post = optionalPost.get();
        String userId = post.getUser().getUserid();
        if (!userId.equals(SecurityUtil.getCurrentUserId())) {
            throw new PermissionNotGrantedException(ErrorCode.PERMISSION_NOT_GRANTED_ERROR, "해당 게시글을 수정할 수 있는 권한이 없습니다.");
        }

        post.update(postDto.getTitle(), postDto.getContent());
        return postId;
    }

    @Override
    @Transactional
    public void deletePost(Long postId) {
        Optional<Post> optionalPost = postRepository.findById(postId);
        if (optionalPost.isEmpty()) {
            throw new PostNotFoundException(ErrorCode.POST_NOT_FOUND_ERROR, "해당하는 게시글이 존재하지 않습니다.");
        }

        Post post = optionalPost.get();
        String userId = post.getUser().getUserid();
        if (!userId.equals(SecurityUtil.getCurrentUserId())) {
            throw new PermissionNotGrantedException(ErrorCode.PERMISSION_NOT_GRANTED_ERROR, "해당 게시글을 삭제할 수 있는 권한이 없습니다.");
        }

        postRepository.delete(post);
    }
}