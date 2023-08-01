package com.dtalks.dtalks.admin.announcement.service;

import com.dtalks.dtalks.admin.announcement.dto.AnnounceDto;
import com.dtalks.dtalks.admin.announcement.dto.AnnouncementResponseDto;
import com.dtalks.dtalks.admin.announcement.entity.Announcement;
import com.dtalks.dtalks.admin.announcement.repository.AnnouncementRepository;
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
public class AnnouncementServiceImpl implements AnnouncementService {
    private final AnnouncementRepository announcementRepository;

    @Override
    @Transactional
    public AnnouncementResponseDto searchById(Long announcementId) {
        Announcement announcement = findAnnouncement(announcementId);
        announcement.updateViewCount();

        return AnnouncementResponseDto.toDto(announcement);
    }

    @Override
    @Transactional
    public Page<AnnouncementResponseDto> searchAllAnnouncement(Pageable pageable) {
        Page<Announcement> announcementPage = announcementRepository.findAll(pageable);
        return announcementPage.map(AnnouncementResponseDto::toDto);
    }

    @Override
    @Transactional
    public Long createAnnouncement(AnnounceDto announceDto) {
        User user = SecurityUtil.getUser();
        if (!user.isAdmin()) {
            throw new CustomException(ErrorCode.PERMISSION_NOT_GRANTED_ERROR, "관리자 권한이 아닙니다. ");
        }
        Announcement announcement = Announcement.toEntity(announceDto, user);
        announcementRepository.save(announcement);
        return announcement.getId();
    }

    @Override
    @Transactional
    public Long updateAnnouncement(AnnounceDto announceDto, Long id) {
        User user = SecurityUtil.getUser();
        if (!user.isAdmin()) {
            throw new CustomException(ErrorCode.PERMISSION_NOT_GRANTED_ERROR, "관리자 권한이 아닙니다. ");
        }
        Announcement announcement = findAnnouncement(id);
        announcement.update(announceDto.getTitle(), announceDto.getContent());

        return announcement.getId();
    }

    @Override
    @Transactional
    public void deleteAnnouncement(Long id) {
        Announcement announcement = findAnnouncement(id);
        User user = SecurityUtil.getUser();

        if (!user.isAdmin()) {
            throw new CustomException(ErrorCode.PERMISSION_NOT_GRANTED_ERROR, "삭제 권한이 없습니다. ");
        }
        announcementRepository.delete(announcement);
    }

    @Transactional(readOnly = true)
    protected Announcement findAnnouncement(Long announcementId) {
        return announcementRepository.findById(announcementId).orElseThrow(() -> new CustomException(ErrorCode.ANNOUNCEMENT_NOT_FOUND_ERROR, "공지사항이 존재하지 않습니다. "));
    }
}
