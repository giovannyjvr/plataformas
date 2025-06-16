import http from 'k6/http';
import { sleep } from 'k6';

export const options = {
  vus: 50,           // mais VUs pra gerar CPU
  duration: '60s',   // roda por 1 minuto
};

const BASE_ACCOUNT = 'http://a91581898cea249f5957f886205be7cc-1161370802.sa-east-1.elb.amazonaws.com:8080';

export default function () {
  http.get(`${BASE_ACCOUNT}/account`);
  // sem sleep longo pra aumentar carga
  sleep(0.1);
}
