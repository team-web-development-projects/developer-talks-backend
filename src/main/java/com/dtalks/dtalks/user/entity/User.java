package com.dtalks.dtalks.user.entity;

import com.dtalks.dtalks.base.entity.Document;
import com.dtalks.dtalks.board.post.entity.FavoritePost;
import com.dtalks.dtalks.board.post.entity.RecommendPost;
import com.dtalks.dtalks.qna.answer.entity.Answer;
import com.dtalks.dtalks.qna.question.entity.Question;
import com.dtalks.dtalks.base.entity.BaseTimeEntity;
import com.dtalks.dtalks.board.comment.entity.Comment;
import com.dtalks.dtalks.board.post.entity.Post;
import com.dtalks.dtalks.studyroom.entity.StudyRoomUser;
import com.dtalks.dtalks.studyroom.enums.Skill;
import com.dtalks.dtalks.user.dto.UserDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "users")
public class User extends BaseTimeEntity implements UserDetails{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = true)
    @Size(min = 5, max = 30)
    private String userid;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(unique = true)
    @Size(min = 1, max = 30)
    private String nickname;

    private String registrationId;

    @ElementCollection(fetch = FetchType.EAGER)
    @Builder.Default
    private List<String> roles = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @Builder.Default
    private List<Skill> skills = new ArrayList<>();

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private Boolean isActive;

    private String status;

    @OneToOne
    private Document profileImage;

    @OneToMany(mappedBy = "user", cascade = CascadeType.MERGE, orphanRemoval = true)
    private List<Post> postList = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.MERGE, orphanRemoval = true)
    private List<FavoritePost> favoritePostList = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.MERGE, orphanRemoval = true)
    private List<RecommendPost> recommendPostList = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.MERGE, orphanRemoval = true)
    private List<Comment> commentList = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.MERGE, orphanRemoval = true)
    private List<Question> questionList = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.MERGE, orphanRemoval = true)
    private List<Answer> answerList = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StudyRoomUser> studyRoomUserList = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.MERGE, orphanRemoval = true)
    private List<Activity> activityList = new ArrayList<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Override
    public String getUsername() {
        return this.userid;
    }

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public static User toUser(UserDto userDto) {
        return User.builder()
                .email(userDto.getEmail())
                .userid(userDto.getUserid())
                .nickname(userDto.getNickname())
                .registrationId(userDto.getRegistrationId())
                .roles(Collections.singletonList("USER"))
                .isActive(userDto.isActive())
                .build();
    }
}
