package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Null;

@Data
@Builder
public class UserDto {
    @Null(groups = MarkerUserDto.OnCreate.class, message = "Пока не нужен ИД")
    private Integer id;
    @NotBlank(groups = MarkerUserDto.OnCreate.class, message = "Нет имени")
    private String name;
    @Email(groups = MarkerUserDto.OnCreate.class, message = "Некорректный емайл")
    @Email(groups = MarkerUserDto.OnUpdate.class, message = "Некорректный емайл")
    @NotBlank(groups = MarkerUserDto.OnCreate.class, message = "Не указан емайл")
    private String email;
}
