package com.dtalks.dtalks.admin.visitor.service;

import jakarta.servlet.http.HttpServletRequest;

import java.time.LocalDate;
import java.util.Map;

public interface VisitorService {

    Map<LocalDate, Integer> getDailyVisitorCounts(LocalDate startDate, LocalDate endDate);

    void increaseVisitorCount(HttpServletRequest request);
}
