package com.dtalks.dtalks.board.post.service;

import com.dtalks.dtalks.base.component.S3Uploader;
import com.dtalks.dtalks.base.entity.Document;
import com.dtalks.dtalks.base.repository.DocumentRepository;
import com.dtalks.dtalks.base.validation.FileValidation;
import com.dtalks.dtalks.board.post.entity.FavoritePost;
import com.dtalks.dtalks.board.post.entity.Post;
import com.dtalks.dtalks.board.post.entity.PostImage;
import com.dtalks.dtalks.board.post.entity.RecommendPost;
import com.dtalks.dtalks.board.post.repository.FavoritePostRepository;
import com.dtalks.dtalks.board.post.repository.PostImageRepository;
import com.dtalks.dtalks.board.post.repository.RecommendPostRepository;
import com.dtalks.dtalks.exception.exception.CustomException;
import com.dtalks.dtalks.board.post.dto.PostDto;
import com.dtalks.dtalks.board.post.dto.PostRequestDto;
import com.dtalks.dtalks.board.post.repository.PostRepository;
import com.dtalks.dtalks.exception.ErrorCode;
import com.dtalks.dtalks.user.Util.SecurityUtil;
import com.dtalks.dtalks.user.entity.Activity;
import com.dtalks.dtalks.user.entity.User;
import com.dtalks.dtalks.user.enums.ActivityType;
import com.dtalks.dtalks.user.repository.ActivityRepository;
import com.dtalks.dtalks.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.*;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final FavoritePostRepository favoritePostRepository;
    private final RecommendPostRepository recommendPostRepository;
    private final ActivityRepository activityRepository;

    private final PostImageRepository imageRepository;
    private final DocumentRepository documentRepository;
    private final S3Uploader s3Uploader;
    private final String imagePath =  new File("").getAbsolutePath() + "posts";

    @Override
    @Transactional
    public PostDto searchById(Long id) {
        Post post = findPost(id);
        post.setViewCount(post.getViewCount() + 1);

        List<PostImage> imageList = imageRepository.findByPostId(id);
        List<String> urls = new ArrayList<>();
        if (imageList != null) {
            for (PostImage image : imageList) {
                urls.add(image.getDocument().getUrl());
            }
        }
        PostDto postDto = PostDto.toDto(post);
        postDto.setUrls(urls);
        return postDto;
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
        User user = findUser(userId);
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
    public Long createPost(PostRequestDto postDto, List<MultipartFile> files) {
        User user = SecurityUtil.getUser();
        Post post = Post.toEntity(postDto, user);
        postRepository.save(post);

        if (files != null){
            for (MultipartFile file : files) {
                FileValidation.imageValidation(file.getOriginalFilename());
                String path = S3Uploader.createFilePath(file.getOriginalFilename(), imagePath);

                Document document = Document.builder()
                        .inputName(file.getOriginalFilename())
                        .url(s3Uploader.fileUpload(file, path))
                        .path(path)
                        .build();
                documentRepository.save(document);

                PostImage postImage = PostImage.builder()
                        .post(post)
                        .document(document)
                        .build();
                imageRepository.save(postImage);
            }
        }

        Activity activity = Activity.builder()
                .post(post)
                .type(ActivityType.POST)
                .user(user)
                .build();

        activityRepository.save(activity);
        return post.getId();
    }

    @Override
    @Transactional
    public Long updatePost(PostRequestDto postDto,  List<MultipartFile> files, Long postId) {
        Post post = findPost(postId);
        String userId = post.getUser().getUserid();
        if (!userId.equals(SecurityUtil.getUser().getUserid())) {
            throw new CustomException(ErrorCode.PERMISSION_NOT_GRANTED_ERROR, "해당 게시글을 수정할 수 있는 권한이 없습니다.");
        }

        post.update(postDto.getTitle(), postDto.getContent());

        List<PostImage> dbFiles = imageRepository.findByPostId(postId);
        List<MultipartFile> newDBFiles = new ArrayList<>();
        if (dbFiles == null) {
            if (files != null) {
                for (MultipartFile file : files) {
                    newDBFiles.add(file);
                }
            }
        } else {
            if (files == null) {
                for (PostImage image : dbFiles) {
                    imageRepository.delete(image);
                }
            } else {
                List<String> dbInputNameList = new ArrayList<>();
                for (PostImage dbFile : dbFiles) {
                    Optional<Document> document = documentRepository.findById(dbFile.getDocument().getId());
                    if (document.isEmpty()) {
                        throw new CustomException(ErrorCode.FILE_NOT_FOUND_ERROR, "저장된 파일을 찾을수 없습니다.");
                    }
                    String inputName = document.get().getInputName();
                    boolean isDeleted = true;
                    for (MultipartFile file : files) {
                        if (file.getOriginalFilename().equals(inputName)) {
                            isDeleted = false;
                            break;
                        }
                    }
                    if (isDeleted) {
                        imageRepository.delete(dbFile);
                        s3Uploader.deleteFile(dbFile.getDocument().getPath());
                    } else {
                        dbInputNameList.add(inputName);
                    }
                }

                for (MultipartFile file : files) {
                    String originalFilename = file.getOriginalFilename();
                    if (!dbInputNameList.contains(originalFilename)) {
                        newDBFiles.add(file);
                    }
                }
            }
        }

        if (!newDBFiles.isEmpty()) {
            for (MultipartFile file : newDBFiles) {
                FileValidation.imageValidation(file.getOriginalFilename());
                String path = S3Uploader.createFilePath(file.getOriginalFilename(), imagePath);

                Document document = Document.builder()
                        .inputName(file.getOriginalFilename())
                        .url(path)
                        .path(path)
                        .build();
                documentRepository.save(document);

                PostImage postImage = PostImage.builder()
                        .post(post)
                        .document(document)
                        .build();
                imageRepository.save(postImage);
            }
        }

        return postId;
    }

    @Override
    @Transactional
    public void deletePost(Long postId) {
        Post post = findPost(postId);
        String userId = post.getUser().getUserid();
        if (!userId.equals(SecurityUtil.getUser().getUserid())) {
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
            if (activity.getType().equals(ActivityType.COMMENT)) {
                activity.setComment(null);
            }
            activity.setPost(null);
        }

        List<PostImage> imageList = imageRepository.findByPostId(postId);
        for (PostImage image : imageList) {
            s3Uploader.deleteFile(image.getDocument().getPath());
        }

        postRepository.delete(post);
    }

    @Transactional(readOnly = true)
    private User findUser(String userid) {
        Optional<User> user = userRepository.findByUserid(userid);
        if (user.isEmpty()) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND_ERROR, "존재하지 않는 사용자입니다.");
        }
        return user.get();
    }

    @Transactional(readOnly = true)
    private Post findPost(Long postId) {
        Optional<Post> post = postRepository.findById(postId);
        if (post.isEmpty()) {
            throw new CustomException(ErrorCode.POST_NOT_FOUND_ERROR, "존재하지 않는 게시글입니다.");
        }
        return post.get();
    }
}
