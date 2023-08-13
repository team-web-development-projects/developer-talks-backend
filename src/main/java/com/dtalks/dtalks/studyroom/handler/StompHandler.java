package com.dtalks.dtalks.studyroom.handler;

import com.dtalks.dtalks.exception.ErrorCode;
import com.dtalks.dtalks.exception.exception.CustomException;
import com.dtalks.dtalks.studyroom.service.ChatService;
import com.dtalks.dtalks.user.entity.User;
import com.dtalks.dtalks.user.service.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Component
public class StompHandler implements ChannelInterceptor {

    private final TokenService tokenService;
    private final ChatService chatService;


    @Override
    @Transactional
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        String token = accessor.getFirstNativeHeader("X-AUTH-TOKEN");
        log.info("stomp handler: " + accessor.getCommand() + "\n" + accessor.getFirstNativeHeader("X-AUTH-TOKEN"));

        if(accessor.getCommand() != StompCommand.DISCONNECT && !tokenService.validateToken(token)) {
            log.info("토큰값이 올바르지 않습니다.");
            throw new CustomException(ErrorCode.VALIDATION_ERROR, "토큰값이 올바르지 않습니다.");
        }

        if(accessor.getCommand() == StompCommand.SEND && accessor.getDestination() != null) {
            String text = getText(message);
            Long destination = getDestination(accessor);
            log.info(accessor.getDestination() + "\n" + text);
            Authentication authentication = tokenService.getAuthentication(token);
            accessor.setUser(authentication);
            User user = tokenService.getUserByToken(token);
            chatService.createChatMessage(destination, text, user);
        }

        return message;
    }

    private String getText(Message<?> message) {
        String text = new String((byte[]) message.getPayload());
        return text.substring(1, text.length()-1);
    }

    private Long getDestination(StompHeaderAccessor accessor) {
        String destination[] = accessor.getDestination().split("/");
        return Long.parseLong(destination[destination.length-1]);
    }
}
