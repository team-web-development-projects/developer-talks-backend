package com.dtalks.dtalks.qna.question.service;

import com.dtalks.dtalks.base.component.S3Uploader;
import com.dtalks.dtalks.base.entity.Document;
import com.dtalks.dtalks.base.repository.DocumentRepository;
import com.dtalks.dtalks.base.validation.FileValidation;
import com.dtalks.dtalks.board.post.dto.NewImageDto;
import com.dtalks.dtalks.board.post.dto.OldImageDto;
import com.dtalks.dtalks.board.post.dto.PutRequestDto;
import com.dtalks.dtalks.exception.ErrorCode;
import com.dtalks.dtalks.exception.exception.CustomException;
import com.dtalks.dtalks.qna.question.entity.Question;
import com.dtalks.dtalks.qna.question.entity.QuestionImage;
import com.dtalks.dtalks.qna.question.repository.QuestionImageRepository;
import com.dtalks.dtalks.qna.question.repository.QuestionRepository;
import com.dtalks.dtalks.qna.question.dto.QuestionDto;
import com.dtalks.dtalks.qna.question.dto.QuestionResponseDto;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class QuestionServiceImpl implements QuestionService {

    private final UserRepository userRepository;
    private final QuestionRepository questionRepository;
    private final DocumentRepository documentRepository;
    private final QuestionImageRepository imageRepository;
    private final S3Uploader s3Uploader;
    private final String imagePath = "questions";


    @Override
    @Transactional
    public QuestionResponseDto searchById(Long questionId) {
        Question question = findQuestion(questionId);

        question.updateViewCount();

        List<QuestionImage> imageList = imageRepository.findByQuestionIdOrderByOrderNum(questionId);
        List<String> urls = new ArrayList<>();

        if (imageList != null) {
            for (QuestionImage image : imageList) {
                urls.add(image.getDocument().getUrl());
            }
        }
        QuestionResponseDto questionResponseDto = QuestionResponseDto.toDto(question);
        questionResponseDto.setImageUrls(urls);

        return questionResponseDto;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<QuestionResponseDto> searchAllQuestion(Pageable pageable) {
        Page<Question> questionPage = questionRepository.findAll(pageable);
        return questionPage.map(QuestionResponseDto::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<QuestionResponseDto> searchQuestionsByUser(String userId, Pageable pageable) {
        Optional<User> optionalUser = userRepository.findByUserid(userId);
        if (optionalUser.isEmpty()) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND_ERROR, "존재하지 않는 사용자입니다.");
        }
        User user = optionalUser.get();
        Page<Question> questions = questionRepository.findByUserId(user.getId(), pageable);
        return questions.map(QuestionResponseDto::toDto);
    }


    @Override
    @Transactional(readOnly = true)
    public Page<QuestionResponseDto> searchByKeyword(String keyword, Pageable pageable) {
        Page<Question> questionPage = questionRepository.findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(keyword, keyword, pageable);
        if (questionPage.isEmpty()) {
            throw new CustomException(ErrorCode.POST_NOT_FOUND_ERROR, "해당하는 질문글이 존재하지 않습니다. ");
        }
        return questionPage.map(QuestionResponseDto::toDto);
    }

    @Override
    @Transactional
    public List<QuestionResponseDto> search5BestQuestions() {
        LocalDateTime startDateTime = LocalDateTime.now().minusDays(7).withHour(0).withMinute(0).withSecond(0);
        List<Question> top5Questions = questionRepository.findTop5ByCreateDateGreaterThanEqualOrderByRecommendCountDesc(startDateTime);
        return top5Questions.stream().map(QuestionResponseDto::toDto).toList();
    }

    @Override
    @Transactional
    public Long createQuestion(QuestionDto questionDto) {

        User user = SecurityUtil.getUser();
        Question question = Question.toEntity(questionDto, user);
        questionRepository.save(question);

        List<MultipartFile> files = questionDto.getFiles();
        if (files != null) {
            Long orderNum = 1L;
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
                    question.setThumbnailUrl(document.getUrl());
                    setThumbnail = true;
                }

                QuestionImage questionImage = QuestionImage.builder()
                        .question(question)
                        .document(document)
                        .orderNum(orderNum++)
                        .build();
                imageRepository.save(questionImage);
            }
        }
        return question.getId();
    }

    @Override
    @Transactional
    public Long updateQuestion(Long questionId, PutRequestDto putRequestDto) {
        Question question = findQuestion(questionId);

        String userId = question.getUser().getUserid();
        if (!userId.equals(SecurityUtil.getUser().getUserid())) {
            throw new CustomException(ErrorCode.PERMISSION_NOT_GRANTED_ERROR, "해당 질문글을 수정할 수 있는 권한이 없습니다. ");
        }
        //제목, 내용 update
        question.update(putRequestDto.getTitle(), putRequestDto.getContent());

        List<OldImageDto> imgUrls = putRequestDto.getImgUrls();
        List<NewImageDto> files = putRequestDto.getFiles();

        List<QuestionImage> dbFiles = imageRepository.findByQuestionId(questionId);
        List<String> deletableUrls = new ArrayList<>();

        // db 값에서 이미지 url로 넘어온 값 중에 같은 url이 없으면 삭제된 파일이므로 db에서 삭제
        for (QuestionImage dbFile : dbFiles) {
            Document document = documentRepository.findById(dbFile.getDocument().getId()).orElseThrow(() -> {
                throw new CustomException(ErrorCode.FILE_NOT_FOUND_ERROR, "저장된 파일을 찾을수 없습니다.");
            });
            String documentUrl = document.getUrl();
            // 넘어온 기존 게시글 이미지 url이 없다면 기존 이미지 다 삭제
            boolean isDeleted = true;
            Long orderNum = dbFile.getOrderNum();
            if (imgUrls != null) {
                for (OldImageDto oldImage : imgUrls) {
                    if (oldImage.getUrl().equals(documentUrl)) {
                        isDeleted = false;
                        orderNum = oldImage.getOrderNum();
                        break;
                    }
                }
            }

            if (isDeleted) {
                imageRepository.delete(dbFile);
                deletableUrls.add(document.getPath());
            } else {
                dbFile.setOrderNum(orderNum);
            }
        }

        if (files != null) {
            for (NewImageDto newImage : files) {
                MultipartFile file = newImage.getFile();
                FileValidation.imageValidation(file.getOriginalFilename());
                String path = S3Uploader.createFilePath(file.getOriginalFilename(), imagePath);

                Document document = Document.builder()
                        .inputName(file.getOriginalFilename())
                        .url(s3Uploader.fileUpload(file, path))
                        .path(path)
                        .build();
                documentRepository.save(document);



                QuestionImage questionImage = QuestionImage.builder()
                        .question(question)
                        .document(document)
                        .orderNum(newImage.getOrderNum())
                        .build();
                imageRepository.save(questionImage);
            }
        }

        String thumbnail = null;
        // 기존 이미지가 있거나 새롭게 저장된 이미지가 있으면 썸네일 확인 및 변경
        if ((imgUrls != null && !imgUrls.isEmpty()) || files != null) {
            Optional<QuestionImage> top1Image = imageRepository.findTop1ByQuestionId(questionId);
            thumbnail = top1Image.get().getDocument().getUrl();
        }
        question.setThumbnailUrl(thumbnail);

        for (String url : deletableUrls) {
            s3Uploader.deleteFile(url);
        }

        return questionId;
    }

    @Override
    @Transactional
    public void deleteQuestion(Long questionId) {
        Question question = findQuestion(questionId);

        if (!question.getAnswerList().isEmpty()) {
            throw new CustomException(ErrorCode.DELETE_NOT_PERMITTED_ERROR, "답변이 달린 질문은 삭제할 수 없습니다. ");
        }
        String userId = question.getUser().getUserid();
        if (!userId.equals(SecurityUtil.getUser().getUserid())) {
            throw new CustomException(ErrorCode.PERMISSION_NOT_GRANTED_ERROR, "해당 질문글을 삭제할 수 있는 권한이 없습니다. ");
        }

        List<QuestionImage> imageList = imageRepository.findByQuestionId(questionId);
        for (QuestionImage image : imageList) {
            s3Uploader.deleteFile(image.getDocument().getPath());
        }

        questionRepository.delete(question);
    }

    @Transactional(readOnly = true)
    protected Question findQuestion(Long questionId){
        Optional<Question> question = questionRepository.findById(questionId);
        if (question.isEmpty()) {
            throw new CustomException(ErrorCode.POST_NOT_FOUND_ERROR, "해당하는 질문글이 존재하지 않습니다. ");
        }
        return question.get();
    }

}
