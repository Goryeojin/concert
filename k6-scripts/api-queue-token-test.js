import http from 'k6/http';
import { check, sleep } from 'k6';

// 부하 조건 설정
export let options = {
    stages: [
        { duration: '20s', target: 20 },
        { duration: '20s', target: 50 },
        { duration: '20s', target: 20 }
    ],
    thresholds: {
        http_req_duration: ['p(95)<500'], // 95%의 요청이 500ms 미만이어야 함
        http_req_failed: ['rate<0.01'],  // 실패율은 1% 미만
    },
};

export default function () {
    let userId = Math.floor(Math.random() * 200) + 1;

    let url = 'http://localhost:8080/api/v1/queue/tokens'; // 대기열 토큰 생성 API 엔드포인트
    let payload = JSON.stringify({
        userId: userId,
    });

    let params = {
        headers: {
            'Content-Type': 'application/json',
        },
    };

    // HTTP POST 요청
    let res = http.post(url, payload, params);

    // 응답 검증
    check(res, {
        'status is 201': (r) => r.status === 201,
        'response time < 500ms': (r) => r.timings.duration < 500,
    });

    // 요청 간격
    sleep(0.5);
}

