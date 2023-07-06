package com.dtalks.dtalks.user.service;

import com.dtalks.dtalks.user.dto.RecentActivityDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserActivityService {
    Page<RecentActivityDto> getRecentActivities(String nickname, Pageable pageable);
}
