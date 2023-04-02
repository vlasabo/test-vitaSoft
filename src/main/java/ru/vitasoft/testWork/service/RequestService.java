package ru.vitasoft.testWork.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.vitasoft.testWork.dto.request.RequestDtoIn;
import ru.vitasoft.testWork.dto.request.RequestDtoOut;
import ru.vitasoft.testWork.dto.request.RequestMapper;
import ru.vitasoft.testWork.repository.RequestRepository;
import ru.vitasoft.testWork.repository.UserRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RequestService {
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;

    public RequestDtoOut addRequest(RequestDtoIn requestDto, String username) {
        requestDto.setCreationDate(LocalDateTime.now());
        //requestDto.setUser(userRepository.findByUsername(username).orElseThrow()); todo: убрать после донастройки секьюрити
        return RequestMapper.toRequestDtoOut(requestRepository.save(RequestMapper.toRequest(requestDto)));
    }
}
