//package com.dtalks.dtalks.notification.service;
//
//import com.dtalks.dtalks.notification.dto.NotificationRequestDto;
//import lombok.RequiredArgsConstructor;
//import org.springframework.scheduling.annotation.Async;
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.annotation.Propagation;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.transaction.event.TransactionPhase;
//import org.springframework.transaction.event.TransactionalEventListener;
//
//@Component
//@RequiredArgsConstructor
//public class NotificationListener {
//    private final SseEmitters sseEmitters;
//
////    @TransactionalEventListener
////    @Async
//    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT) //커밋완료 후 작업
//    @Transactional(propagation = Propagation.REQUIRES_NEW)// 새로운 트랜잭션으로 구성
//    public void handleNotification(NotificationRequestDto dto) {
//        sseEmitters.send(dto.getRefId(), dto.getReceiver(), dto.getType(), dto.getMessage(), dto.getUrl());
//    }
//
//}
