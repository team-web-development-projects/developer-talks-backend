package com.dtalks.dtalks.admin.inquiry.service;

import com.dtalks.dtalks.admin.inquiry.dto.InquiryDto;
import com.dtalks.dtalks.admin.inquiry.dto.InquiryResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface InquiryService {
    InquiryResponseDto searchById(Long id);

    Page<InquiryResponseDto> searchAllInquiry(Pageable pageable);

    Long createInquiry(InquiryDto inquiryDto);

    Long updateInquiry(InquiryDto inquiryDto, Long id);

    void deleteInquiry(Long id);


}
