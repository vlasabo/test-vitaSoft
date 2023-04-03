package ru.vitasoft.testWork.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.vitasoft.testWork.model.user.Role;
import ru.vitasoft.testWork.model.user.User;
import ru.vitasoft.testWork.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@Slf4j
public class AdministratorController {
    private final UserService userService;

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    public List<User> getUsers() {
        log.debug("get all users");
        return userService.getAllUsers();
    }

    @GetMapping("/findUser")
    @ResponseStatus(HttpStatus.OK)
    public List<User> getUserByUsername(@RequestParam String username) {
        log.debug("get user by part of username {}", username);
        return userService.findUserByPartOfUsername(username);
        //Оставлю тут лист потому что совпадений может быть много. Про findFirst знаю.
    }

    @PatchMapping("addRole/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public User setRoleToUser(@PathVariable Long userId) {
        log.debug("set operator role to user with id {}", userId);
        return userService.setRoleToUser(userId, Role.OPERATOR);
    }
}
