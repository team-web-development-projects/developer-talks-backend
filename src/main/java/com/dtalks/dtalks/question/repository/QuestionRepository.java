package com.dtalks.dtalks.question.repository;

import com.dtalks.dtalks.question.entity.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    Page<Question> findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(String keyword1, String keyword2 ,Pageable pageable);
    Page<Question> findAllByOrderByIdDesc(Pageable pageable);
}
