package com.dtalks.dtalks.message.entity;

import com.dtalks.dtalks.base.entity.BaseTimeEntity;
import com.dtalks.dtalks.message.dto.MessageDto;
import com.dtalks.dtalks.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Message extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    private String text;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    private User receiver;

    private boolean deletedBySender;

    private boolean deletedByReceiver;



    public static Message toEntity(MessageDto messageDto, User sender, User receiver) {
        return Message.builder()
                .text(messageDto.getText())
                .sender(sender)
                .receiver(receiver)
                .deletedBySender(false)
                .deletedByReceiver(false)
                .build();
    }

    public void setDeletedBySender() {
        this.deletedBySender = true;
    }

    public void setDeletedByReceiver() {
        this.deletedByReceiver = true;
    }
    public boolean isDeleted() {
        return isDeletedByReceiver() && isDeletedBySender();
    }
}
