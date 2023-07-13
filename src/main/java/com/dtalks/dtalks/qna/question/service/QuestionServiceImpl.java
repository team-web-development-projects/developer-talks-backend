package com.dtalks.dtalks.qna.question.service;

import com.dtalks.dtalks.base.component.S3Uploader;
import com.dtalks.dtalks.base.entity.Document;
import com.dtalks.dtalks.base.repository.DocumentRepository;
import com.dtalks.dtalks.base.validation.FileValidation;
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
    public QuestionResponseDto searchById(Long id) {
        Optional<Question> optionalQuestion = questionRepository.findById(id);
        if (optionalQuestion.isEmpty()) {
            throw new CustomException(ErrorCode.POST_NOT_FOUND_ERROR, "해당하는 질문글이 존재하지 않습니다. ");
        }
        Question question = optionalQuestion.get();
        question.updateViewCount();

        List<QuestionImage> imageList = imageRepository.findByQuestionId(id);
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
        Page<Question> questionPage = questionRepository.findAllByOrderByIdDesc(pageable);
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
    public Long updateQuestion(Long questionId, QuestionDto questionDto) {
        Optional<Question> optionalQuestion = questionRepository.findById(questionId);
        if (optionalQuestion.isEmpty()) {
            throw new CustomException(ErrorCode.POST_NOT_FOUND_ERROR, "해당하는 질문글이 존재하지 않습니다. ");
        }
        Question question = optionalQuestion.get();
        String userId = question.getUser().getUserid();
        if (!userId.equals(SecurityUtil.getUser().getUserid())) {
            throw new CustomException(ErrorCode.PERMISSION_NOT_GRANTED_ERROR, "해당 질문글을 수정할 수 있는 권한이 없습니다. ");
        }
        //제목, 내용 update
        question.update(questionDto.getTitle(), questionDto.getContent());

        List<QuestionImage> existingImages = question.getImageList();
        List<MultipartFile> newFiles = questionDto.getFiles();

        //기존 파일 inputName 추출
        List<String> existingFileNames = existingImages.stream()
                .map(image -> image.getDocument().getInputName()).toList();

        //새로운 파일 OriginalFilename
        List<String> newFilesNames = newFiles.stream()
                .map(MultipartFile::getOriginalFilename).toList();

        //수정 질문글에 이미지 존재하는 경우
        if (!newFiles.isEmpty()) {

            // 새로운 파일을 순서대로 저장할 리스트 생성
            List<QuestionImage> updatedImages = new ArrayList<>();

            for (MultipartFile file : newFiles) {
                // 새로운 파일중 기존 파일에 없는 것 추가
                String fileName = file.getOriginalFilename();
                boolean fileExists = existingFileNames.contains(file.getOriginalFilename());

                if (!fileExists) {
                    FileValidation.imageValidation(fileName);
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
                            .build();
                    imageRepository.save(questionImage);

                    updatedImages.add(questionImage);

                }
            }
            //기존 파일중 삭제된 파일 S3와 DB에서 제거
            List<QuestionImage> removedImages = existingImages.stream()
                    .filter(image -> !newFilesNames.contains(image.getDocument().getInputName())).toList();

            for (QuestionImage removedImage : removedImages) {
                String path = removedImage.getDocument().getPath();
                s3Uploader.deleteFile(path); // Delete image from S3 storage
                imageRepository.delete(removedImage); // Delete image from the database
            }


            existingImages.removeIf(image -> !newFilesNames.contains(image.getDocument().getInputName()));

            // 기존 파일 순서 업데이트
            for (int i = 0; i < existingImages.size(); i++) {
                QuestionImage existingImage = existingImages.get(i);
                existingImage.setOrderNum((long) (i + 1)); // 1부터 순서 부여
                imageRepository.save(existingImage);
            }

            // 새로운 파일 순서 업데이트
            for (int i = 0; i < updatedImages.size(); i++) {
                QuestionImage updatedImage = updatedImages.get(i);
                updatedImage.setOrderNum((long) (existingImages.size() + i + 1)); // 기존 파일 수 + 1부터 순서 부여
                imageRepository.save(updatedImage);
            }

        } else {
            // 새로운 파일이 제공되지 않은 경우, 모든 기존 이미지 제거
            existingImages.forEach(image -> {
                String path = image.getDocument().getPath();
                s3Uploader.deleteFile(path); // S3에서 이미지 삭제
                imageRepository.delete(image); // 데이터베이스에서 이미지 삭제
            });
        }

        return questionId;
    }

    @Override
    @Transactional
    public void deleteQuestion(Long id) {
        Optional<Question> optionalQuestion = questionRepository.findById(id);
        if (optionalQuestion.isEmpty()) {
            throw new CustomException(ErrorCode.POST_NOT_FOUND_ERROR, "해당하는 질문글이 존재하지 않습니다. ");
        }
        Question question = optionalQuestion.get();
        if (!question.getAnswerList().isEmpty()) {
            throw new CustomException(ErrorCode.DELETE_NOT_PERMITTED_ERROR, "답변이 달린 질문은 삭제할 수 없습니다. ");
        }
        String userId = question.getUser().getUserid();
        if (!userId.equals(SecurityUtil.getUser().getUserid())) {
            throw new CustomException(ErrorCode.PERMISSION_NOT_GRANTED_ERROR, "해당 질문글을 삭제할 수 있는 권한이 없습니다. ");
        }

        List<QuestionImage> imageList = imageRepository.findByQuestionId(id);
        for (QuestionImage image : imageList) {
            s3Uploader.deleteFile(image.getDocument().getPath());
        }

        questionRepository.delete(question);
    }

}
