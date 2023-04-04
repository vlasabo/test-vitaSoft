package ru.vitasoft.testWork;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import ru.vitasoft.testWork.controller.AdministratorController;
import ru.vitasoft.testWork.controller.OperatorController;
import ru.vitasoft.testWork.controller.UserController;
import ru.vitasoft.testWork.service.RequestService;
import ru.vitasoft.testWork.service.UserService;

import java.util.NoSuchElementException;


@SpringBootTest()
class TestWorkApplicationTests {

    @Autowired
    RequestService requestService;
    @Autowired
    UserService userService;
    @Autowired
    AdministratorController administratorController;
    @Autowired
    OperatorController operatorController;
    @Autowired
    UserController userController;

    //ТЕСТЫ ДОСТУПОВ
    //один authorities
    //AdministratorController
    @Test
    @WithMockUser(username = "user", authorities = {"USER"})
    void userAccessDeniedToAdministratorController() {
        Assertions.assertThrows(org.springframework.security.access.AccessDeniedException.class,
                () -> administratorController.getUsers());
    }

    @Test
    @WithMockUser(username = "user", authorities = {"OPERATOR"})
    void operatorAccessDeniedToAdministratorController() {
        Assertions.assertThrows(org.springframework.security.access.AccessDeniedException.class,
                () -> administratorController.getUsers());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void adminAccessGrantedToAdministratorController() {
        Assertions.assertDoesNotThrow(() -> administratorController.getUsers());
    }

    //OperatorController
    @Test
    @WithMockUser(username = "user", authorities = {"USER"})
    void userAccessDeniedToOperatorController() {
        Assertions.assertThrows(org.springframework.security.access.AccessDeniedException.class,
                () -> operatorController.getRequest(1L));
    }

    @Test
    @WithMockUser(username = "operator", authorities = {"OPERATOR"})
    void operatorAccessGrantedToOperatorController() {
        Assertions.assertThrows(NoSuchElementException.class, //есть доступ, нет заявки
                () -> operatorController.getRequest(1L));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void adminAccessDeniedToOperatorController() {
        Assertions.assertThrows(org.springframework.security.access.AccessDeniedException.class,
                () -> operatorController.getRequest(1L));
    }

    //UserController
    @Test
    @WithMockUser(username = "user", authorities = {"USER"})
    void userAccessGrantedToUserController() {
        Assertions.assertDoesNotThrow(() -> userController.getUserRequests(true, 0, userService.findUserByUsername("user")));
    }

    @Test
    @WithMockUser(username = "operator", authorities = {"OPERATOR"})
    void operatorAccessDeniedToUserController() {
        Assertions.assertThrows(org.springframework.security.access.AccessDeniedException.class,
                () -> userController.getUserRequests(true, 0, userService.findUserByUsername("operator")));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void adminAccessDeniedToUserController() {
        Assertions.assertThrows(org.springframework.security.access.AccessDeniedException.class,
                () -> userController.getUserRequests(true, 0, userService.findUserByUsername("admin")));
    }

    //все authorities
    //AdministratorController
    @Test
    @WithMockUser(username = "username", authorities = {"USER", "OPERATOR", "ADMIN"})
    void manyAuthoritiesAccessGrantedToAdministratorController() {
        Assertions.assertDoesNotThrow(() -> administratorController.getUsers());
    }

    //OperatorController
    @Test
    @WithMockUser(username = "username", authorities = {"USER", "OPERATOR", "ADMIN"})
    void manyAuthoritiesAccessGrantedToOperatorController() {
        Assertions.assertThrows(NoSuchElementException.class, //есть доступ, нет заявки
                () -> operatorController.getRequest(1L));
    }

    //UserController
    @Test
    @WithMockUser(username = "username", authorities = {"USER", "OPERATOR", "ADMIN"})
    void manyAuthoritiesAccessGrantedToUserController() {
        Assertions.assertDoesNotThrow(() -> userController.getUserRequests(true, 0, userService.findUserByUsername("user")));
    }


//    //ТЕСТЫ ЛОГИКИ
//    @Test
//    @WithMockUser(username = "user", authorities = {"USER"})
//    void addCorrectRequest() {
//        RequestDtoIn requestDtoIn = new RequestDtoIn();
//        requestDtoIn.setUser(userService.findUserByUsername("user"));
//        requestDtoIn.setText("text");
//        requestService.addRequest(requestDtoIn, "user");
//        Assertions.assertEquals(requestService.getAllForUser("user", false, 0).getSize(), 1);
//    }

}
