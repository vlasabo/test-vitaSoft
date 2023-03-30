package ru.vitasoft.testWork.model.request;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum RequestStatus {

    DRAFT("Черновик"),
    POSTED("Отправлено"),
    ACCEPTED("Принято"),
    REJECTED("Отклонено");

    private final String statusName;

}
