package com.dtalks.dtalks.user.service;

import com.dtalks.dtalks.user.dto.UserDto;
import com.dtalks.dtalks.user.entity.User;
import com.dtalks.dtalks.user.enums.OAuthAttributes;
import com.dtalks.dtalks.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class OAuthServiceImpl implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;
    private final Logger LOGGER = LoggerFactory.getLogger(OAuthServiceImpl.class);

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        LOGGER.info("loadUser 호출됨");
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        String userNameAttributeName = userRequest.getClientRegistration()
                .getProviderDetails()
                .getUserInfoEndpoint()
                .getUserNameAttributeName();

        Map<String, Object> attributes = oAuth2User.getAttributes();
        UserDto userDto = OAuthAttributes.extract(registrationId, attributes);
        userDto.setRegistrationId(registrationId);
        saveOrUpdate(userDto);

        Map<String, Object> customAttribute =customAttribute(attributes, userNameAttributeName, userDto, registrationId);

        return new DefaultOAuth2User(
                Collections.singletonList(new SimpleGrantedAuthority("USER")),
                customAttribute,
                userNameAttributeName
        );
    }

    private Map customAttribute(Map attributes, String userNameAttributeName, UserDto userDto, String registrationId) {
        Map<String, Object> customAttribute = new LinkedHashMap<>();
        customAttribute.put(userNameAttributeName, attributes.get(userNameAttributeName));
        customAttribute.put("email", userDto.getEmail());
        customAttribute.put("userid", userDto.getEmail());
        customAttribute.put("registrationId", registrationId);
        customAttribute.put("nickname", userDto.getNickname());
        return customAttribute;
    }

    private User saveOrUpdate(UserDto userDto) {
        User user = userRepository.getByEmail(userDto.getEmail());
        if(user == null) {
            user = userDto.toUser();
        }
        else {
            user.setEmail(userDto.getEmail());
            user.setNickname(userDto.getNickname());
            user.setUserid(userDto.getEmail());
        }
        return userRepository.save(user);
    }
}
