package com.dtalks.dtalks.admin.announcement.repository;

import com.dtalks.dtalks.admin.announcement.entity.Announcement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {

}
