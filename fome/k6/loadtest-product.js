import http from 'k6/http';
import { check } from 'k6';

export const options = {
  vus: 50,
  duration: '2m',
};

const PRODUCT_URL = 'http://ad5e8bb5d24184c3bbede099e98eb991-555235693.sa-east-1.elb.amazonaws.com:8080';

export default function () {
  const res = http.get(`${PRODUCT_URL}/product`);
  check(res, {
    'status 200': (r) => r.status === 200,
  });
}
