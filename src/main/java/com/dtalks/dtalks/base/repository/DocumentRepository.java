package com.dtalks.dtalks.base.repository;

import com.dtalks.dtalks.base.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentRepository extends JpaRepository<Document, Long> {
}
