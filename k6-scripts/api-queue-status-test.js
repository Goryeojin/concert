import http from 'k6/http';
import { check, sleep } from 'k6';

// 부하 조건 설정
export let options = {
    stages: [
        { duration: '20s', target: 200 }, // 20초 동안 유저 수를 200명으로 증가
        { duration: '20s', target: 200 }, // 20초 동안 일정한 부하 유지
        { duration: '20s', target: 200 }, // 20초 동안 일정한 부하 유지
    ],
    thresholds: {
        http_req_duration: ['p(95)<500'], // 95%의 요청이 500ms 미만이어야 함
        http_req_failed: ['rate<0.01'],  // 실패율은 1% 미만
    },
};

export default function () {
    let token = 'e05a1959-dabe-33cd-94b1-b2e30a63d73e';

    let url = 'http://localhost:8080/api/v1/queue/status'; // 대기열 상태 조회 API 엔드포인트
    let params = {
        headers: {
            'Content-Type': 'application/json',
            'Token': token,
            'User-Id': '1',
        },
    };

    // HTTP GET 요청
    let res = http.get(url, params);

    // 응답 검증
    check(res, {
        'status is 200': (r) => r.status === 200,
        'response time < 500ms': (r) => r.timings.duration < 500,
    });

    // 요청 간격 (0.5초로 설정, 초당 200명씩 요청을 보내려면 간격이 필요)
    sleep(0.005);  // sleep(0.005) => 1초에 약 200번 요청 가능
}
