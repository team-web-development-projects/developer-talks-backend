package com.dtalks.dtalks.studyroom.handler;

import com.dtalks.dtalks.exception.ErrorCode;
import com.dtalks.dtalks.exception.exception.CustomException;
import com.dtalks.dtalks.studyroom.service.ChatService;
import com.dtalks.dtalks.user.service.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class StompHandler implements ChannelInterceptor {

    private final TokenService tokenService;
    private final ChatService chatService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        log.info("stomp handler: " + accessor.getCommand() + "\n" + accessor.getFirstNativeHeader("X-AUTH-TOKEN"));
        log.info(accessor.getDestination() + "\n" + accessor.getMessage());

        if(!tokenService.validateToken(accessor.getFirstNativeHeader("X-AUTH-TOKEN"))) {
            throw new CustomException(ErrorCode.VALIDATION_ERROR, "토큰값이 올바르지 않습니다.");
        }

        if(accessor.getCommand() == StompCommand.SEND && accessor.getDestination() != null && accessor.getMessage() != null) {
            String destination[] = accessor.getDestination().split("/");
            chatService.createChatMessage(Long.parseLong(destination[destination.length-1]), accessor.getMessage());
        }


        return message;
    }
}
