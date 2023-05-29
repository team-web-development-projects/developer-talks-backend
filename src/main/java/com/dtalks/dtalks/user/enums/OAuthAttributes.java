package com.dtalks.dtalks.user.enums;

import com.dtalks.dtalks.user.dto.UserDto;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;

public enum OAuthAttributes {
    GOOGLE("google", (attributes) -> {
        UserDto userDto = new UserDto();
        userDto.setEmail((String) attributes.get("email"));
        userDto.setUserid((String) attributes.get("email"));
        return userDto;
    });

    private final String registrationId;
    private final Function<Map<String, Object>, UserDto> of;

    OAuthAttributes(String registrationId,  Function<Map<String, Object>, UserDto> of) {
        this.registrationId = registrationId;
        this.of = of;
    }

    public static UserDto extract(String registrationId, Map<String, Object> attributes) {
        return Arrays.stream(values())
                .filter(provider -> registrationId.equals(provider.registrationId))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new)
                .of.apply(attributes);
    }
}
