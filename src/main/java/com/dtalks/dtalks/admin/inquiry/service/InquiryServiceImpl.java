package com.dtalks.dtalks.admin.inquiry.service;

import com.dtalks.dtalks.admin.inquiry.dto.InquiryDto;
import com.dtalks.dtalks.admin.inquiry.dto.InquiryResponseDto;
import com.dtalks.dtalks.admin.inquiry.entity.Inquiry;
import com.dtalks.dtalks.admin.inquiry.repository.InquiryRepository;
import com.dtalks.dtalks.exception.ErrorCode;
import com.dtalks.dtalks.exception.exception.CustomException;
import com.dtalks.dtalks.user.Util.SecurityUtil;
import com.dtalks.dtalks.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InquiryServiceImpl implements InquiryService {

    private final InquiryRepository inquiryRepository;

    @Override
    @Transactional(readOnly = true)
    public InquiryResponseDto searchById(Long id) {
        Inquiry inquiry = findInquiry(id);
        inquiry.updateViewCount();

        return InquiryResponseDto.toDto(inquiry);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<InquiryResponseDto> searchAllInquiry(Pageable pageable) {
        Page<Inquiry> inquiryPage = inquiryRepository.findAll(pageable);
        return inquiryPage.map(InquiryResponseDto::toDto);
    }

    @Override
    @Transactional
    public Long createInquiry(InquiryDto inquiryDto) {
        User user = SecurityUtil.getUser();
        Inquiry inquiry = Inquiry.toEntity(inquiryDto, user);
        inquiryRepository.save(inquiry);

        return inquiry.getId();
    }

    @Override
    @Transactional
    public Long updateInquiry(InquiryDto inquiryDto, Long id) {
        User user = SecurityUtil.getUser();
        String userid = user.getUserid();

        Inquiry inquiry = findInquiry(id);
        if (!userid.equals(user.getUserid())) {
            throw new CustomException(ErrorCode.PERMISSION_NOT_GRANTED_ERROR, "해당 문의사항을 수정할 수 있는 권한이 없습니다. ");
        }
        inquiry.update(inquiryDto.getTitle(), inquiryDto.getContent(), inquiryDto.getIsPrivate());

        return inquiry.getId();
    }

    @Override
    @Transactional
    public void deleteInquiry(Long id) {
        User user = SecurityUtil.getUser();
        String userid = user.getUserid();

        Inquiry inquiry = findInquiry(id);
        if (!userid.equals(user.getUserid())) {
            throw new CustomException(ErrorCode.PERMISSION_NOT_GRANTED_ERROR, "해당 문의사항을 수정할 수 있는 권한이 없습니다. ");
        }

        inquiryRepository.delete(inquiry);
    }

    @Transactional(readOnly = true)
    protected Inquiry findInquiry(Long inquiryId) {
        return inquiryRepository.findById(inquiryId).orElseThrow(() -> new CustomException(ErrorCode.INQUIRY_NOT_FOUND_ERROR, "문의사항이 존재하지 않습니다. "));
    }
}
