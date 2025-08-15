package ru.practicum.users.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.users.dto.NewUserRequest;
import ru.practicum.users.dto.UserDto;
import ru.practicum.users.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class AdminUserController {

    private final UserService userService;

    @GetMapping
    public List<UserDto> getUsers(@RequestParam (required = false) List<Long> ids,
                                    @PositiveOrZero @RequestParam  (defaultValue = "0") int from,
                                    @Positive @RequestParam (defaultValue = "10") int size) {
        log.debug("GET admin/users");
        log.info("Admin: Получение информации о пользователях");
        return userService.getUsers(ids, from, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto createUser(@Valid @RequestBody NewUserRequest newUserRequest) {
        log.debug("POST admin/users");
        log.info("Admin: Добавление нового пользователя");
        return userService.createUser(newUserRequest);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long userId) {
        log.debug("DELETE admin/users/{}", userId);
        log.info("Admin: Удаление пользователя");
        userService.deleteUser(userId);
    }
}
