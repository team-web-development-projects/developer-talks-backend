package com.dtalks.dtalks.board.post.service;

import com.dtalks.dtalks.base.entity.Document;
import com.dtalks.dtalks.base.repository.DocumentRepository;
import com.dtalks.dtalks.board.post.dto.FileNameVO;
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
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
    private final String imagePath =  new File("").getAbsolutePath() + "/files/post/";

    @Override
    @Transactional
    public PostDto searchById(Long id) {
        Optional<Post> optionalPost = postRepository.findById(id);
        if (optionalPost.isEmpty()) {
            throw new CustomException(ErrorCode.POST_NOT_FOUND_ERROR, "존재하지 않는 게시글입니다.");
        }
        Post post = optionalPost.get();
        post.setViewCount(post.getViewCount() + 1);

        List<PostImage> imageList = imageRepository.findByPostId(id);
        List<byte[]> files = new ArrayList<>();
        if (imageList != null) {
            for (PostImage image : imageList) {
                String storeName = image.getDocument().getStoreName();
                try {
                    File file = new File(imagePath + storeName);
                    byte[] imageByteArray = FileCopyUtils.copyToByteArray(file);
                    files.add(imageByteArray);
                } catch (FileNotFoundException e) {
                    throw new CustomException(ErrorCode.FILE_NOT_FOUND_ERROR, "저장된 파일을 찾을수 없습니다.");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        PostDto postDto = PostDto.toDto(post);
        postDto.setFiles(files);
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
    public Long createPost(PostRequestDto postDto, List<MultipartFile> files) {
        User user = SecurityUtil.getUser();
        Post post = Post.toEntity(postDto, user);
        postRepository.save(post);

        if (files != null){
            for (MultipartFile file : files) {
                FileNameVO fileName = fileHandle(file, post.getId());

                Document document = Document.builder()
                        .inputName(fileName.getInputName())
                        .storeName(fileName.getStoreName())
                        .path(fileName.getSavePath().toString())
                        .build();
                documentRepository.save(document);

                PostImage postImage = PostImage.builder()
                        .post(post)
                        .document(document)
                        .build();
                imageRepository.save(postImage);

                try {
                    Files.write(fileName.getSavePath(), file.getBytes());
                } catch (Exception e) {
                    throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR, e.toString());
                }
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
        Optional<Post> optionalPost = postRepository.findById(postId);
        if (optionalPost.isEmpty()) {
            throw new CustomException(ErrorCode.POST_NOT_FOUND_ERROR, "존재하지 않는 게시글입니다.");
        }

        Post post = optionalPost.get();
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
                        File file = new File(imagePath + dbFile.getDocument().getStoreName());
                        file.delete();
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
                FileNameVO fileName = fileHandle(file, postId);
                Document document = Document.builder()
                        .inputName(fileName.getInputName())
                        .storeName(fileName.getStoreName())
                        .path(fileName.getSavePath().toString())
                        .build();
                documentRepository.save(document);

                PostImage postImage = PostImage.builder()
                        .post(post)
                        .document(document)
                        .build();
                imageRepository.save(postImage);

                try {
                    Files.write(fileName.getSavePath(), file.getBytes());
                } catch (Exception e) {
                    throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR, e.toString());
                }
            }
        }

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
            File file = new File(imagePath + image.getDocument().getStoreName());
            file.delete();
        }

        postRepository.delete(post);
    }

    private FileNameVO fileHandle(MultipartFile file, Long postId) {
        String format, tag, inputName, storeName;

        inputName = file.getOriginalFilename();
        format = getImageFormat(inputName);
        if (!(format.equals(".jpg") || format.equals(".png"))) {
            throw new CustomException(ErrorCode.FILE_FORMAT_ERROR, "파일 형식이 올바르지 않습니다.");
        }

        tag = "post_" + postId;
        storeName = tag + System.nanoTime() + format;
        Path savePath = Paths.get(imagePath + storeName);

        return new FileNameVO(inputName, storeName, savePath);
    }

    private String getImageFormat(String imageName) {
        String s[] = imageName.split("[.]");
        return "." + s[s.length-1].toLowerCase();
    }

}
