package ru.animaltracker.apigateway.service;

import ru.animaltracker.apigateway.entity.User;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public interface UserService {
    Mono<User> getById(Long id);
    Mono<User> getByEmail(String email);
    Mono<User> getByUsername(String username);
}
