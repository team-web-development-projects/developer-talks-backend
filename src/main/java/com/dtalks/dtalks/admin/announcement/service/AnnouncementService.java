package com.dtalks.dtalks.admin.announcement.service;

import com.dtalks.dtalks.admin.announcement.dto.AnnounceDto;
import com.dtalks.dtalks.admin.announcement.dto.AnnouncementResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AnnouncementService {

    AnnouncementResponseDto searchById(Long id);

    Page<AnnouncementResponseDto> searchAllAnnouncement(Pageable pageable);

    Long createAnnouncement(AnnounceDto announceDto);

    Long updateAnnouncement(AnnounceDto announceDto, Long id);

    void deleteAnnouncement(Long id);
}
