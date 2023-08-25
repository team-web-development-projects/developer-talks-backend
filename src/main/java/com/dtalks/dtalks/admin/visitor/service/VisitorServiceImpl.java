package com.dtalks.dtalks.admin.visitor.service;

import com.dtalks.dtalks.admin.visitor.entity.Visitor;
import com.dtalks.dtalks.admin.visitor.repository.VisitorRepository;
import com.dtalks.dtalks.exception.ErrorCode;
import com.dtalks.dtalks.exception.exception.CustomException;
import com.dtalks.dtalks.user.Util.SecurityUtil;
import com.dtalks.dtalks.user.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class VisitorServiceImpl implements VisitorService{
    private final VisitorRepository visitorRepository;

    @Override
    public Map<LocalDate, Integer> getDailyVisitorCounts(LocalDate startDate, LocalDate endDate) {
        User user = SecurityUtil.getUser();
        if (!user.isAdmin()) {
            throw new CustomException(ErrorCode.PERMISSION_NOT_GRANTED_ERROR, "관리자 권한이 아닙니다. ");
        }

        Map<LocalDate, Integer> dailyCounts = new HashMap<>();
        LocalDate currentDate = startDate;

        while (!currentDate.isAfter(endDate)) {
            int count = getVisitorCountForDate(currentDate);
            dailyCounts.put(currentDate, count);
            currentDate = currentDate.plusDays(1);
        }

        return dailyCounts;
    }

    @Override
    @Transactional
    public void increaseVisitorCount(HttpServletRequest request) {

        String ipAddress = getClientIpAddr(request);
        LocalDate today = LocalDate.now();

        Optional<Visitor> visitorOptional = visitorRepository.findByDate(today);

        if (visitorOptional.isPresent()) {
            Visitor visitor = visitorOptional.get();

            // 같은 날짜에 같은 IP 주소가 없으면 count 증가 및 IP 주소 추가
            if (!visitor.getIpAddresses().contains(ipAddress)) {
                visitor.addIpAddress(ipAddress);
                visitor.increaseCount();
                visitorRepository.save(visitor);
            }
        } else {
            Visitor visitor = new Visitor();
            visitor.setDate(today);
            visitor.addIpAddress(ipAddress);
            visitor.setCount(1);
            visitorRepository.save(visitor);
        }
    }

    private int getVisitorCountForDate(LocalDate date) {
        Optional<Visitor> visitorOptional = visitorRepository.findByDate(date);

        return visitorOptional.map(Visitor::getCount).orElse(0);
    }

    private static String getClientIpAddr(HttpServletRequest request) {

        Set<String> headerNamesToCheck = new HashSet<>(Arrays.asList("Proxy-Client-IP", "WL-Proxy-Client-IP", "HTTP_CLIENT_IP", "HTTP_X_FORWARDED_FOR"));
        Set<String> headerNames = new HashSet<>(Collections.list(request.getHeaderNames()));

        headerNames.retainAll(headerNamesToCheck);

        String ip = null;

        for (String headerName : headerNames) {
            String headerValue = request.getHeader(headerName);
            if (isInvalidIp(headerValue)) {
                ip = headerValue;
                break;
            }
        }

        if (isInvalidIp(ip)) {
            ip = request.getRemoteAddr();
        }

        return ip;
    }

    private static boolean isInvalidIp(String ip) {
        return ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip);
    }
}
