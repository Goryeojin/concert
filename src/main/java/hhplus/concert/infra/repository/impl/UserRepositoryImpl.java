package hhplus.concert.infra.repository.impl;

import hhplus.concert.domain.repository.UserRepository;
import hhplus.concert.infra.repository.jpa.UserJpaRepository;
import hhplus.concert.support.exception.CoreException;
import hhplus.concert.support.code.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final UserJpaRepository userJpaRepository;

    @Override
    public void existsUser(Long userId) {
        if (!userJpaRepository.existsById(userId)) {
            throw new CoreException(ErrorCode.USER_NOT_FOUND);
        }
    }
}
