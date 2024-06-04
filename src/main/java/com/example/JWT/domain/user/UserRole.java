package com.example.JWT.domain.user;


import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
@Entity
@SequenceGenerator(
        name = "USER_ROLE_SEQ_GENERATOR",
        sequenceName = "USER_ROLE_SEQ", // 실제 데이터베이스에 생성될 시퀀스 이름
        initialValue = 1, // 시작 값
        allocationSize = 1 // 증가치
)
public class UserRole {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "USER_ROLE_SEQ_GENERATOR")
    private Long userRoleId;

    @ManyToOne
    @JoinColumn(name = "user_seq_id")
    private Users user;

    @Enumerated(value = EnumType.STRING)
    private Role role;

    @Builder
    public UserRole(Users user, Role role) {
        this.user = user;
        this.role = role;
    }
}
