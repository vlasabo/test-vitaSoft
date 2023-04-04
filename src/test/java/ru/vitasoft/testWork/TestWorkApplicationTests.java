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
import ru.vitasoft.testWork.model.user.User;
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
    @Autowired
    ObjectMapper mapper;

    //ТЕСТЫ ДОСТУПОВ
    //один authorities
    //AdministratorController
    @Test
    @WithMockUser(username = "user", authorities = {"USER"})
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void userAccessDeniedToAdministratorController() {
        Assertions.assertThrows(org.springframework.security.access.AccessDeniedException.class,
                () -> administratorController.getUsers());
    }

    @Test
    @WithMockUser(username = "user", authorities = {"OPERATOR"})
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void operatorAccessDeniedToAdministratorController() {
        Assertions.assertThrows(org.springframework.security.access.AccessDeniedException.class,
                () -> administratorController.getUsers());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void adminAccessGrantedToAdministratorController() {
        Assertions.assertDoesNotThrow(() -> administratorController.getUsers());
    }

    //OperatorController
    @Test
    @WithMockUser(username = "user", authorities = {"USER"})
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
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
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void adminAccessDeniedToOperatorController() {
        Assertions.assertThrows(org.springframework.security.access.AccessDeniedException.class,
                () -> operatorController.getRequest(1L));
    }

    //UserController
    @Test
    @WithMockUser(username = "user", authorities = {"USER"})
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void userAccessGrantedToUserController() {
        Assertions.assertDoesNotThrow(() -> userController.getUserRequests(true, 0, getUser()));
    }

    @Test
    @WithMockUser(username = "operator", authorities = {"OPERATOR"})
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void operatorAccessDeniedToUserController() {
        Assertions.assertThrows(org.springframework.security.access.AccessDeniedException.class,
                () -> userController.getUserRequests(true, 0, userService.findUserByUsername("operator")));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void adminAccessDeniedToUserController() {
        Assertions.assertThrows(org.springframework.security.access.AccessDeniedException.class,
                () -> userController.getUserRequests(true, 0, userService.findUserByUsername("admin")));
    }

    //все authorities
    //AdministratorController
    @Test
    @WithMockUser(username = "username", authorities = {"USER", "OPERATOR", "ADMIN"})
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
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
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void manyAuthoritiesAccessGrantedToUserController() {
        Assertions.assertDoesNotThrow(() -> userController.getUserRequests(true, 0, getUser()));
    }


    //ТЕСТЫ ЛОГИКИ
    @Test
    @WithMockUser(username = "user", authorities = {"USER"})
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void addCorrectRequest() {
        RequestDtoIn requestDtoIn = new RequestDtoIn();
        requestDtoIn.setUser(getUser());
        requestDtoIn.setText("text");

        requestService.addRequest(requestDtoIn, "user");
        var pageRequests = requestService.getAllForUser("user", false, 0);
        Assertions.assertEquals(pageRequests.getSize(), 1);
        Assertions.assertTrue(pageRequests.get().findFirst().isPresent());
        Assertions.assertEquals(pageRequests.get().findFirst().get().getText(), "text");
    }

    @Test
    @WithMockUser(username = "operator", authorities = {"USER", "OPERATOR"})
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void setRequestAsPostedAsUserAndGetTextFromRequestAsOperatorAndAcceptRequestAndRejectRequest() {
        RequestDtoIn requestDtoIn = new RequestDtoIn();
        requestDtoIn.setUser(getUser());
        requestDtoIn.setText("Мне нужна помощь");
        requestService.addRequest(requestDtoIn, "user");
        userController.sendToSubmit(1L, getUser());
        String expectedText = "М-н-е- -н-у-ж-н-а- -п-о-м-о-щ-ь";

        var pageRequests = operatorController.getUserRequests(false, 0, "user");
        Assertions.assertEquals(pageRequests.get().findFirst().get().getText(), expectedText);
        pageRequests = operatorController.getAllRequests(false, 0);
        Assertions.assertEquals(pageRequests.get().findFirst().get().getText(), expectedText);
        RequestDtoOut requestDtoOut = operatorController.getRequest(1L);
        Assertions.assertEquals(requestDtoOut.getText(), expectedText);

        operatorController.acceptRequest(1L);
        pageRequests = operatorController.getUserRequests(false, 0, "user");
        Assertions.assertEquals(pageRequests.getSize(), 0); //оператор не видит заявку в статусе принята
        Assertions.assertEquals(
                userController.getUserRequests( //пользователь видит
                        true,
                        0,
                        getUser()).get().findFirst().get().getStatus(),
                RequestStatus.ACCEPTED);

        RequestDtoIn requestDtoIn2 = new RequestDtoIn();
        requestDtoIn2.setUser(getUser());
        requestDtoIn2.setText("Мне нужна помощь");
        userController.addRequest(requestDtoIn2, getUser());
        requestService.sendToSubmit(2L, "user");
        operatorController.rejectRequest(2L);
        Assertions.assertEquals(
                userController.getUserRequests( //пользователь видит
                        true,
                        0,
                        getUser()).get().findFirst().get().getStatus(),
                RequestStatus.REJECTED);
    }

    @Test
    @WithMockUser(username = "user", authorities = {"USER"})
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void correctEditUserRequestAndTryToChangeStatusAndEdit() {
        RequestDtoIn requestDtoIn = new RequestDtoIn();
        requestDtoIn.setUser(getUser());
        requestDtoIn.setText("Мне нужна помощь");
        requestService.addRequest(requestDtoIn, "user");
        requestDtoIn.setText("не надо уже ничего");
        userController.editRequest(requestDtoIn, 1L, getUser());
        var requestDto = userController.getUserRequests(false, 0, getUser()).get().findFirst().get();
        Assertions.assertEquals(requestDto.getText(), "не надо уже ничего");

        Assertions.assertThrows(UserAccessException.class, //не тот пользователь
                () -> userController.editRequest(requestDtoIn, 1L, userService.findUserByUsername("admin")));

        requestService.sendToSubmit(1L, "user");
        requestDto.setText("всё-таки надо");
        Assertions.assertThrows(RequestUpdateException.class, //не тот статус заявки
                () -> userController.editRequest(requestDtoIn, 1L, getUser()));
    }

    private User getUser() {
        return userService.findUserByUsername("user");
    }
}
