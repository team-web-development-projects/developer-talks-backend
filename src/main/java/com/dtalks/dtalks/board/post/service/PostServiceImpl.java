package com.dtalks.dtalks.board.post.service;

import com.dtalks.dtalks.base.component.S3Uploader;
import com.dtalks.dtalks.base.entity.Document;
import com.dtalks.dtalks.base.repository.DocumentRepository;
import com.dtalks.dtalks.base.validation.FileValidation;
import com.dtalks.dtalks.board.comment.repository.CommentRepository;
import com.dtalks.dtalks.board.post.dto.PutRequestDto;
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
import com.dtalks.dtalks.user.entity.User;
import com.dtalks.dtalks.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final FavoritePostRepository favoritePostRepository;
    private final RecommendPostRepository recommendPostRepository;
    private final CommentRepository commentRepository;

    private final PostImageRepository imageRepository;
    private final DocumentRepository documentRepository;
    private final S3Uploader s3Uploader;
    private final String imagePath =  "posts";

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
        postDto.setImageUrls(urls);
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
        LocalDateTime time = LocalDateTime.now().minusDays(7);
        LocalDateTime goe = time.withHour(0).withMinute(0).withSecond(0);
        List<Post> top5Posts = postRepository.findTop5ByCreateDateGreaterThanEqualOrderByRecommendCountDesc(goe);
        return top5Posts.stream().map(PostDto::toDto).toList();
    }

    @Override
    @Transactional
    public Long createPost(PostRequestDto postDto) {
        User user = SecurityUtil.getUser();
        Post post = Post.toEntity(postDto, user);
        postRepository.save(post);

        List<MultipartFile> files = postDto.getFiles();
        if (files != null){
            boolean setThumbnail = false;
            for (MultipartFile file : files) {
                FileValidation.imageValidation(file.getOriginalFilename());
                String path = S3Uploader.createFilePath(file.getOriginalFilename(), imagePath);

                Document document = Document.builder()
                        .inputName(file.getOriginalFilename())
                        .url(s3Uploader.fileUpload(file, path))
                        .path(path)
                        .build();
                documentRepository.save(document);

                if (!setThumbnail) {
                    post.setThumbnailUrl(document.getUrl());
                    setThumbnail = true;
                }

                PostImage postImage = PostImage.builder()
                        .post(post)
                        .document(document)
                        .build();
                imageRepository.save(postImage);
            }
        }
        return post.getId();
    }

    @Override
    @Transactional
    public Long updatePost(PutRequestDto putRequestDto, Long postId) {
        Post post = findPost(postId);
        String userId = post.getUser().getUserid();
        if (!userId.equals(SecurityUtil.getUser().getUserid())) {
            throw new CustomException(ErrorCode.PERMISSION_NOT_GRANTED_ERROR, "해당 게시글을 수정할 수 있는 권한이 없습니다.");
        }

        post.update(putRequestDto.getTitle(), putRequestDto.getContent());

        List<String> imgUrls = putRequestDto.getImgUrls();
        List<MultipartFile> files = putRequestDto.getFiles();

        List<PostImage> dbFiles = imageRepository.findByPostId(postId);

        // db 값에서 이미지 url로 넘어온 값 중에 같은 url이 없으면 삭제된 파일이므로 db에서 삭제
        for (PostImage dbFile : dbFiles) {
            Document document = documentRepository.findById(dbFile.getDocument().getId()).orElseThrow(() -> {
                throw new CustomException(ErrorCode.FILE_NOT_FOUND_ERROR, "저장된 파일을 찾을수 없습니다.");
            });
            String documentUrl = document.getUrl();
            // 넘어온 기존 게시글 이미지 url이 없다면 기존 이미지 다 삭제
            boolean isDeleted = true;
            if (imgUrls != null) {
                for (String url : imgUrls) {
                    if (url.equals(documentUrl)) {
                        isDeleted = false;
                        break;
                    }
                }
            }

            if (isDeleted) {
                imageRepository.delete(dbFile);
                s3Uploader.deleteFile(document.getPath());
            }
        }

        if (files != null) {
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

        String thumbnail = null;
        // 기존 이미지가 있거나 새롭게 저장된 이미지가 있으면 썸네일 확인 및 변경
        if ((imgUrls != null && !imgUrls.isEmpty()) || files != null) {
            Optional<PostImage> top1Image = imageRepository.findTop1ByPostId(postId);
            thumbnail = top1Image.get().getDocument().getUrl();
        }
        post.setThumbnailUrl(thumbnail);
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

        boolean commentExists = commentRepository.existsByPostId(postId);
        if (commentExists) {
            throw new CustomException(ErrorCode.POST_NOT_DELETABLE, "댓글이 존재하는 게시글은 삭제할 수 없습니다.");
        }

        List<FavoritePost> favoritePostList = favoritePostRepository.findByPostId(post.getId());
        for (FavoritePost favoritePost : favoritePostList) {
            favoritePostRepository.delete(favoritePost);
        }

        List<RecommendPost> recommendPostList = recommendPostRepository.findByPostId(post.getId());
        for (RecommendPost recommendPost : recommendPostList) {
            recommendPostRepository.delete(recommendPost);
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
