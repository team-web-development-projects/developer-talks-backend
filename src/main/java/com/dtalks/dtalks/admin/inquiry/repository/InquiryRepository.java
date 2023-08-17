package com.dtalks.dtalks.admin.inquiry.repository;

import com.dtalks.dtalks.admin.inquiry.entity.Inquiry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InquiryRepository extends JpaRepository<Inquiry, Long> {
}
