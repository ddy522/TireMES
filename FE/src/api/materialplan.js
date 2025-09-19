import axios from 'axios'

const http = axios.create({
  baseURL: 'http://localhost:8080', // ✅ 백엔드 서버 주소
  timeout: 10000,
})

export async function fetchMaterialPlan(params) {
  const res = await http.get('/materials/plan', { params })
  return res.data
}
