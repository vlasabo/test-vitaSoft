package ru.vitasoft.testWork.model.request;


import lombok.Getter;

public enum RequestStatus {

    DRAFT("Черновик"),
    POSTED("Отправлено"),
    ACCEPTED("Принято"),
    REJECTED("Отклонено");

    @Getter
    private final String statusName;

    RequestStatus(String statusName) {
        this.statusName = statusName;
    }

}
