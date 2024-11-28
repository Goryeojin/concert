import http from 'k6/http';
import { check, sleep } from 'k6';

// 부하 조건 설정
export let options = {
    stages: [
        { duration: '20s', target: 10 },  // 20초 동안 20명까지 증가
        { duration: '20s', target: 50 },  // 20초 동안 60명까지 증가
        { duration: '20s', target: 10 },  // 20초 동안 80명까지 증가
    ],
    thresholds: {
        http_req_duration: ['p(95)<500'], // 95%의 요청이 500ms 미만이어야 함
        http_req_failed: ['rate<0.01'],  // 실패율은 1% 미만
    },
};

export default function () {
    let url = 'http://localhost:8080/api/v1/concerts/1/schedules';

    // HTTP GET 요청
    let res = http.get(url);

    // 응답 검증
    check(res, {
        'status is 200': (r) => r.status === 200,
        'response time < 500ms': (r) => r.timings.duration < 500,
    });

    sleep(1);
}
