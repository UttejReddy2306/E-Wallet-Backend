package org.gfg;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateUserRequest {
    @NotBlank
    private String name;

    @Email
    @NotNull
    private String email;

    private String mobile;

    @Min(18)
    private Integer age;

    public User toUser(){
        return User.builder()
                .age(this.age)
                .name(this.name)
                .email(this.email)
                .mobile(this.mobile)
                .build();
    }
}
