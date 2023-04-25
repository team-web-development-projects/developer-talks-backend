package com.dtalks.dtalks.post.service;

import com.dtalks.dtalks.post.dto.PostDto;
import com.dtalks.dtalks.post.entity.Post;
import com.dtalks.dtalks.post.repository.PostRepository;
import com.dtalks.dtalks.exception.ErrorCode;
import com.dtalks.dtalks.exception.exception.PostNotFoundException;
import com.dtalks.dtalks.exception.exception.UserNotFoundException;
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
            throw new PostNotFoundException(ErrorCode.POST_NOT_FOUND_ERROR, "게시글이 존재하지 않습니다.");
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
    public Long createPost(PostDto postDto) {
        User user = userRepository.getByNickname(postDto.getNickname());
        if (user == null) {
            throw new UserNotFoundException(ErrorCode.USER_NOT_FOUND_ERROR, "유저가 존재하지 않습니다.");
        }
        Post post = Post.toEntity(postDto, user);
        postRepository.save(post);
        return post.getId();
    }

    @Override
    @Transactional
    public Long updatePost(PostDto postDto, Long id) {
        Optional<Post> optionalPost = postRepository.findById(id);
        if (optionalPost.isEmpty()) {
            throw new PostNotFoundException(ErrorCode.POST_NOT_FOUND_ERROR, "게시글이 존재하지 않습니다.");
        }

        Post post = optionalPost.get();
        post.update(postDto.getTitle(), postDto.getContent());
        return id;
    }

    @Override
    @Transactional
    public void deletePost(Long id) {
        Optional<Post> post = postRepository.findById(id);
        if (post.isEmpty()) {
            throw new PostNotFoundException(ErrorCode.POST_NOT_FOUND_ERROR, "게시글이 존재하지 않습니다.");
        }
        postRepository.delete(post.get());
    }
}
