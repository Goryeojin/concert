package hhplus.concert.infra.entity;

import hhplus.concert.domain.model.Queue;
import hhplus.concert.support.type.QueueStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity(name = "queue")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueueEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @Column(nullable = false)
    private String token;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private QueueStatus status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime enteredAt;

    private LocalDateTime expiredAt;

    public Queue of(QueueEntity entity) {
        return Queue.builder()
                .id(entity.getId())
                .userId(entity.getUser().getId())
                .token(entity.getToken())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .enteredAt(entity.getEnteredAt())
                .expiredAt(entity.getExpiredAt())
                .build();
    }

    public QueueEntity from(Queue queue) {
        return QueueEntity.builder()
                .id(queue.id())
                .user(UserEntity.builder().id(queue.userId()).build())
                .token(queue.token())
                .status(queue.status())
                .createdAt(queue.createdAt())
                .enteredAt(queue.enteredAt())
                .expiredAt(queue.expiredAt())
                .build();
    }

}
