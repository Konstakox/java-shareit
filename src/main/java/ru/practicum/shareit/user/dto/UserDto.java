package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Null;

@Data
@Builder
public class UserDto {
    @Null(groups = MarkerUserDto.OnCreate.class)
    private Integer id;
    @NotBlank(groups = MarkerUserDto.OnCreate.class)
    private String name;
    @Email(groups = MarkerUserDto.OnCreate.class)
    @Email(groups = MarkerUserDto.OnUpdate.class)
    @NotBlank(groups = MarkerUserDto.OnCreate.class)
    private String email;
}
