package com.dtalks.dtalks.user.service;

import com.dtalks.dtalks.user.dto.RecentActivityDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;

public interface UserActivityService {
    Page<RecentActivityDto> getRecentActivities(UserDetails userDetails, String nickname, Pageable pageable);
}
