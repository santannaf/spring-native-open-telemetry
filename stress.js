import {check} from 'k6';
import http from 'k6/http';

const URL = "http://localhost:8080/ping";

export let options = {
    dns: {
        ttl: '1s',
        select: 'roundRobin',
        policy: 'any'
    },
    insecureSkipTLSVerify: true,
    scenarios: {
        get_test: {
            executor: 'constant-arrival-rate',
            duration: '180s',
            rate: '50000',
            timeUnit: '1m',
            preAllocatedVUs: 100,
            maxVUs: 2000
        }
    },
    discardRespondeBodies: true,
    threshold: {
        'http_req_duration{name:rinha}': ['p(95)>2000']
    }
};

export default function () {
    const response = http.get(URL, {
        tags: {
            name: 'rinha'
        }
    })

    check(response, {
        'get status should be 200 or 404 or 422': r => (r.status === 200 || r.status === 404 || r.status === 422)
    }, {
        name: 'rinha'
    })
}
