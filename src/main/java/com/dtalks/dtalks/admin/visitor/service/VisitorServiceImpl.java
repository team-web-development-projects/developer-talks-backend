package com.dtalks.dtalks.admin.visitor.service;

import com.dtalks.dtalks.admin.visitor.entity.Visitor;
import com.dtalks.dtalks.admin.visitor.repository.VisitorRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VisitorServiceImpl implements VisitorService{
    private final VisitorRepository visitorRepository;

    @Override
    public Map<LocalDate, Integer> getDailyVisitorCounts(LocalDate startDate, LocalDate endDate) {
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
        String ip = request.getHeader("X-Forwarded-For");

        if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        return ip;
    }
}
