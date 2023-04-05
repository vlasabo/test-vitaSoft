package ru.vitasoft.testWork;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import ru.vitasoft.testWork.controller.AdministratorController;
import ru.vitasoft.testWork.controller.OperatorController;
import ru.vitasoft.testWork.controller.UserController;
import ru.vitasoft.testWork.dto.request.RequestDtoIn;
import ru.vitasoft.testWork.dto.request.RequestDtoOut;
import ru.vitasoft.testWork.exception.RequestUpdateException;
import ru.vitasoft.testWork.exception.UserAccessException;
import ru.vitasoft.testWork.model.request.RequestStatus;
import ru.vitasoft.testWork.model.security.Role;
import ru.vitasoft.testWork.model.user.User;
import ru.vitasoft.testWork.service.RequestService;
import ru.vitasoft.testWork.service.UserService;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;


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
    @Autowired
    ObjectMapper mapper;

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
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
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
        Assertions.assertDoesNotThrow(() -> userController.getUserRequests(true, 0, getUser()));
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
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void manyAuthoritiesAccessGrantedToOperatorController() {
        Assertions.assertThrows(NoSuchElementException.class, //есть доступ, нет заявки
                () -> operatorController.getRequest(1L));
    }

    //UserController
    @Test
    @WithMockUser(username = "username", authorities = {"USER", "OPERATOR", "ADMIN"})
    void manyAuthoritiesAccessGrantedToUserController() {
        Assertions.assertDoesNotThrow(() -> userController.getUserRequests(true, 0, getUser()));
    }


    //ТЕСТЫ ЛОГИКИ
    @Test
    @WithMockUser(username = "user", authorities = {"USER"})
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void addCorrectRequest() {
        RequestDtoIn requestDtoIn = getRequestDtoIn("text");

        requestService.addRequest(requestDtoIn, "user");
        var pageRequests = requestService.getAllForUser("user", false, 0);
        Assertions.assertEquals(1, pageRequests.getSize());
        Assertions.assertTrue(pageRequests.get().findFirst().isPresent());
        Assertions.assertEquals("text", pageRequests.get().findFirst().get().getText());
    }

    @Test
    @WithMockUser(username = "operator", authorities = {"USER", "OPERATOR"})
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void setRequestAsPostedAsUserAndGetTextFromRequestAsOperatorAndAcceptRequestAndRejectRequest() {
        RequestDtoIn requestDtoIn = getRequestDtoIn("Мне нужна помощь");
        requestService.addRequest(requestDtoIn, "user");
        userController.sendToSubmit(1L, getUser());
        String expectedText = "М-н-е- -н-у-ж-н-а- -п-о-м-о-щ-ь";

        var pageRequests = operatorController.getUserRequests(false, 0, "user");
        Assertions.assertEquals(expectedText, pageRequests.get().findFirst().get().getText());
        pageRequests = operatorController.getAllRequests(false, 0);
        Assertions.assertEquals(expectedText, pageRequests.get().findFirst().get().getText());
        RequestDtoOut requestDtoOut = operatorController.getRequest(1L);
        Assertions.assertEquals(expectedText, requestDtoOut.getText());

        operatorController.acceptRequest(1L);
        pageRequests = operatorController.getUserRequests(false, 0, "user");
        Assertions.assertEquals(0, pageRequests.getSize()); //оператор не видит заявку в статусе принята
        Assertions.assertEquals(RequestStatus.ACCEPTED,
                userController.getUserRequests( //пользователь видит
                        true,
                        0,
                        getUser()).get().findFirst().get().getStatus());
    }

    @Test
    @WithMockUser(username = "operator", authorities = {"USER", "OPERATOR"})
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void setRequestAsPostedAsUserAndRejectRequest() {
        RequestDtoIn requestDtoIn = getRequestDtoIn("Мне нужна помощь");
        userController.addRequest(requestDtoIn, getUser());
        requestService.sendToSubmit(1L, "user");
        operatorController.rejectRequest(1L);
        Assertions.assertEquals(RequestStatus.REJECTED,
                userController.getUserRequests(
                        true,
                        0,
                        getUser()).get().findFirst().get().getStatus());
    }


    @Test
    @WithMockUser(username = "user", authorities = {"USER"})
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void correctEditUserRequestAndTryToChangeStatusAndEdit() {
        RequestDtoIn requestDtoIn = getRequestDtoIn("Мне нужна помощь");
        requestService.addRequest(requestDtoIn, "user");
        requestDtoIn.setText("не надо уже ничего");
        userController.editRequest(requestDtoIn, 1L, getUser());
        var requestDto = userController.getUserRequests(false, 0, getUser()).get().findFirst().get();
        Assertions.assertEquals("не надо уже ничего", requestDto.getText());

        Assertions.assertThrows(UserAccessException.class, //не тот пользователь
                () -> userController.editRequest(requestDtoIn, 1L, userService.findUserByUsername("admin")));

        requestService.sendToSubmit(1L, "user");
        requestDto.setText("всё-таки надо");
        Assertions.assertThrows(RequestUpdateException.class, //не тот статус заявки
                () -> userController.editRequest(requestDtoIn, 1L, getUser()));
    }


    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void getAllUsers() {
        Assertions.assertEquals(3, administratorController.getUsers().size());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void setRoleToUser() {
        User user = getUser();
        Set<Role> roles = user.getRoles();
        Assertions.assertEquals(1, roles.size());
        Assertions.assertTrue(roles.contains(Role.USER));

        administratorController.setRoleToUser(user.getId());
        List<User> usersFromBase = administratorController.getUserByUsername("user");
        Assertions.assertEquals(1, usersFromBase.size());
        roles = usersFromBase.get(0).getRoles();
        Assertions.assertEquals(2, roles.size());
        Assertions.assertTrue(roles.contains(Role.USER) && roles.contains(Role.OPERATOR));
    }

    private RequestDtoIn getRequestDtoIn(String text) {
        RequestDtoIn requestDtoIn = new RequestDtoIn();
        requestDtoIn.setUser(getUser());
        requestDtoIn.setText(text);
        return requestDtoIn;
    }

    private User getUser() {
        return userService.findUserByUsername("user");
    }

}
