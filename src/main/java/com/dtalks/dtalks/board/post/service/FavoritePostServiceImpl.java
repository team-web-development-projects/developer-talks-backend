package com.dtalks.dtalks.board.post.service;

import com.dtalks.dtalks.board.post.dto.PostDto;
import com.dtalks.dtalks.board.post.entity.FavoritePost;
import com.dtalks.dtalks.board.post.entity.Post;
import com.dtalks.dtalks.board.post.repository.CustomPostRepository;
import com.dtalks.dtalks.board.post.repository.FavoritePostRepository;
import com.dtalks.dtalks.board.post.repository.PostRepository;
import com.dtalks.dtalks.exception.ErrorCode;
import com.dtalks.dtalks.exception.exception.*;
import com.dtalks.dtalks.user.Util.SecurityUtil;
import com.dtalks.dtalks.user.entity.User;
import com.dtalks.dtalks.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FavoritePostServiceImpl implements FavoritePostService {

    private final FavoritePostRepository favoritePostRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CustomPostRepository customPostRepository;

    @Override
    @Transactional
    public Integer favorite(Long postId) {
        Optional<User> optionalUser = Optional.ofNullable(userRepository.getByUserid(SecurityUtil.getCurrentUserId()));
        if (optionalUser.isEmpty()) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND_ERROR, "존재하지 않는 사용자입니다.");
        }

        Optional<Post> optionalPost = postRepository.findById(postId);
        if (optionalPost.isEmpty()) {
            throw new CustomException(ErrorCode.POST_NOT_FOUND_ERROR, "존재하지 않는 게시글입니다.");
        }

        Post post = optionalPost.get();
        User user = optionalUser.get();

        if (user == post.getUser()) {
            throw new CustomException(ErrorCode.PERMISSION_NOT_GRANTED_ERROR, "작성한 글에는 즐겨찾기가 불가능합니다.");
        }

        if (favoritePostRepository.findByPostIdAndUserId(postId, user.getId()).isPresent()) {
            throw new CustomException(ErrorCode.ALREADY_EXISTS_ERROR, "이미 즐겨찾기로 지정되어 있습니다.");
        }

        FavoritePost favoritePost = FavoritePost.toEntity(post, user);
        favoritePostRepository.save(favoritePost);

        post.setFavoriteCount(post.getFavoriteCount() + 1);
        return post.getFavoriteCount();
    }

    @Override
    @Transactional
    public Integer unFavorite(Long postId) {
        Optional<User> optionalUser = Optional.ofNullable(userRepository.getByUserid(SecurityUtil.getCurrentUserId()));
        if (optionalUser.isEmpty()) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND_ERROR, "존재하지 않는 사용자입니다.");
        }

        Optional<Post> optionalPost = postRepository.findById(postId);
        if (optionalPost.isEmpty()) {
            throw new CustomException(ErrorCode.POST_NOT_FOUND_ERROR, "존재하지 않는 게시글입니다.");
        }

        User user = optionalUser.get();
        Post post = optionalPost.get();

        Optional<FavoritePost> optionalFavoritePost = favoritePostRepository.findByPostIdAndUserId(postId, user.getId());
        if (optionalFavoritePost.isEmpty()) {
            throw new CustomException(ErrorCode.FAVORITE_POST_NOT_FOUND_ERROR, "해당 게시글은 즐겨찾기로 지정되어 있지 않습니다.");
        }

        FavoritePost favoritePost = optionalFavoritePost.get();
        favoritePostRepository.delete(favoritePost);

        post.setFavoriteCount(post.getFavoriteCount() - 1);
        return post.getFavoriteCount();
    }

    @Override
    public Page<PostDto> searchFavoritePostsByUser(String userId, Pageable pageable) {
        Optional<User> optionalUser = Optional.ofNullable(userRepository.getByUserid(SecurityUtil.getCurrentUserId()));
        if (optionalUser.isEmpty()) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND_ERROR, "존재하지 않는 사용자입니다.");
        }
        User user = optionalUser.get();
        Page<Post> posts = customPostRepository.searchFavoritePost(user.getId(), pageable);
        return posts.map(PostDto::toDto);
    }

    @Override
    public boolean checkFavorite(Long postId) {
        Optional<User> optionalUser = Optional.ofNullable(userRepository.getByUserid(SecurityUtil.getCurrentUserId()));
        if (optionalUser.isEmpty()) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND_ERROR, "존재하지 않는 사용자입니다.");
        }

        User user = optionalUser.get();
        return favoritePostRepository.existsByPostIdAndUserId(postId, user.getId());
    }
}
