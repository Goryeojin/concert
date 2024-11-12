package hhplus.concert.domain.repository;

import hhplus.concert.domain.model.Queue;

import java.util.List;

public interface QueueRepository {

    boolean activeTokenExist(String token);

    void removeToken(String token);

    Long getActiveTokenCount();

    Long getWaitingTokenCount();

    void saveActiveToken(String token);

    void saveWaitingToken(String token);

    List<String> retrieveAndRemoveWaitingTokens(long neededTokens);

    Queue findToken(String token);
}
