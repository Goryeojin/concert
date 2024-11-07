package hhplus.concert.domain.service;

import hhplus.concert.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    public void validateUser(Long userId) {
        userRepository.existsUser(userId);
    }
}
