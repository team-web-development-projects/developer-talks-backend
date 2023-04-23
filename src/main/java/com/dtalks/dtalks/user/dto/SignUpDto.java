package com.dtalks.dtalks.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SignUpDto{

    private String email;

    private String nickname;

    private String username;

    private String password;
}
