// src/api/inventoryLots.js
import axios from 'axios';

const http = axios.create({
  baseURL: 'http://localhost:8080/materials',
  timeout: 10000,
});

export async function fetchLotsByPartCode(partCode) {
  const { data } = await http.get(`/inventory/${encodeURIComponent(partCode)}/lots`);
  return Array.isArray(data) ? data : [];
}
