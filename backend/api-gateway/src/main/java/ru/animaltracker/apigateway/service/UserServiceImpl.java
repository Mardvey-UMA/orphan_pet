package ru.animaltracker.apigateway.service;

import ru.animaltracker.apigateway.entity.User;
import ru.animaltracker.apigateway.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public Mono<User> getByUsername(String username) {return userRepository.findByUsername(username);}

    @Override
    public Mono<User> getById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public  Mono<User> getByEmail(String email) {return userRepository.findByEmail(email);}
}