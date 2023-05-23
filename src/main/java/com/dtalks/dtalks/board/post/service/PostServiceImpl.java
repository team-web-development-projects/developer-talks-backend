package com.dtalks.dtalks.board.post.service;

import com.dtalks.dtalks.board.post.entity.FavoritePost;
import com.dtalks.dtalks.board.post.entity.Post;
import com.dtalks.dtalks.board.post.entity.RecommendPost;
import com.dtalks.dtalks.board.post.repository.FavoritePostRepository;
import com.dtalks.dtalks.board.post.repository.RecommendPostRepository;
import com.dtalks.dtalks.exception.exception.CustomException;
import com.dtalks.dtalks.board.post.dto.PostDto;
import com.dtalks.dtalks.board.post.dto.PostRequestDto;
import com.dtalks.dtalks.board.post.repository.PostRepository;
import com.dtalks.dtalks.exception.ErrorCode;
import com.dtalks.dtalks.user.Util.SecurityUtil;
import com.dtalks.dtalks.user.entity.Activity;
import com.dtalks.dtalks.user.entity.User;
import com.dtalks.dtalks.user.enumeration.ActivityType;
import com.dtalks.dtalks.user.repository.ActivityRepository;
import com.dtalks.dtalks.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final FavoritePostRepository favoritePostRepository;
    private final RecommendPostRepository recommendPostRepository;
    private final ActivityRepository activityRepository;

    @Override
    @Transactional
    public PostDto searchById(Long id) {
        Optional<Post> optionalPost = postRepository.findById(id);
        if (optionalPost.isEmpty()) {
            throw new CustomException(ErrorCode.POST_NOT_FOUND_ERROR, "존재하지 않는 게시글입니다.");
        }
        Post post = optionalPost.get();
        post.setViewCount(post.getViewCount() + 1);
        return PostDto.toDto(post);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PostDto> searchAllPost(Pageable pageable) {
        Page<Post> postsPage = postRepository.findAll(pageable);
        return postsPage.map(PostDto::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PostDto> searchPostsByUser(String userId, Pageable pageable) {
        Optional<User> optionalUser = Optional.ofNullable(userRepository.getByUserid(userId));
        if (optionalUser.isEmpty()) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND_ERROR, "존재하지 않는 사용자입니다.");
        }
        User user = optionalUser.get();
        Page<Post> posts = postRepository.findByUserId(user.getId(), pageable);
        return posts.map(PostDto::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PostDto> searchByWord(String keyword, Pageable pageable) {
        Page<Post> posts = postRepository.findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(keyword, keyword, pageable);
        return posts.map(PostDto::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PostDto> search5BestPosts() {
        List<Post> top5Posts = postRepository.findTop5ByOrderByRecommendCountDesc();
        return top5Posts.stream().map(PostDto::toDto).toList();
    }

    @Override
    @Transactional
    public Long createPost(PostRequestDto postDto) {
        Optional<User> user = Optional.ofNullable(userRepository.getByUserid(SecurityUtil.getCurrentUserId()));
        if (user.isEmpty()) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND_ERROR, "존재하지 않는 사용자입니다.");
        }
        Post post = Post.toEntity(postDto, user.get());
        postRepository.save(post);

        Activity activity = Activity.builder()
                .post(post)
                .type(ActivityType.POST)
                .user(user.get())
                .build();

        activityRepository.save(activity);
        return post.getId();
    }

    @Override
    @Transactional
    public Long updatePost(PostRequestDto postDto, Long postId) {
        Optional<Post> optionalPost = postRepository.findById(postId);
        if (optionalPost.isEmpty()) {
            throw new CustomException(ErrorCode.POST_NOT_FOUND_ERROR, "존재하지 않는 게시글입니다.");
        }

        Post post = optionalPost.get();
        String userId = post.getUser().getUserid();
        if (!userId.equals(SecurityUtil.getCurrentUserId())) {
            throw new CustomException(ErrorCode.PERMISSION_NOT_GRANTED_ERROR, "해당 게시글을 수정할 수 있는 권한이 없습니다.");
        }

        post.update(postDto.getTitle(), postDto.getContent());
        return postId;
    }

    @Override
    @Transactional
    public void deletePost(Long postId) {
        Optional<Post> optionalPost = postRepository.findById(postId);
        if (optionalPost.isEmpty()) {
            throw new CustomException(ErrorCode.POST_NOT_FOUND_ERROR, "존재하지 않는 게시글입니다.");
        }

        Post post = optionalPost.get();
        String userId = post.getUser().getUserid();
        if (!userId.equals(SecurityUtil.getCurrentUserId())) {
            throw new CustomException(ErrorCode.PERMISSION_NOT_GRANTED_ERROR, "해당 게시글을 삭제할 수 있는 권한이 없습니다.");
        }

        List<FavoritePost> favoritePostList = favoritePostRepository.findByPostId(post.getId());
        for (FavoritePost favoritePost : favoritePostList) {
            favoritePostRepository.delete(favoritePost);
        }

        List<RecommendPost> recommendPostList = recommendPostRepository.findByPostId(post.getId());
        for (RecommendPost recommendPost : recommendPostList) {
            recommendPostRepository.delete(recommendPost);
        }

        // 게시글이면 삭제, 댓글이면 연관관계들만 끊고 기록에는 남아있도록. 프론트에서 활동 클릭시 없는 게시글이라고 뜨게 하면 됨
        List<Activity> postList = activityRepository.findByPostId(post.getId());
        for (Activity activity : postList) {
            if (activity.getType().equals(ActivityType.POST)) {
                activityRepository.delete(activity);
            } else {
                activity.setPost(null);
                activity.setComment(null);
            }
        }

        postRepository.delete(post);
    }

}
