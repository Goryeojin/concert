package hhplus.concert.infra.repository.impl;

import hhplus.concert.domain.repository.UserRepository;
import hhplus.concert.infra.repository.jpa.UserJpaRepository;
import hhplus.concert.support.code.ErrorType;
import hhplus.concert.support.exception.CoreException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final UserJpaRepository userJpaRepository;

    @Override
    public void existsUser(Long userId) {
        if (!userJpaRepository.existsById(userId)) {
            throw new CoreException(ErrorType.RESOURCE_NOT_FOUND, "사용자 ID: " + userId);
        }
    }
}
