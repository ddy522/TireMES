import axios from 'axios'
const API_BASE = 'http://localhost:8080/materials'

export async function fetchInventoryAll() {
  try {
    const { data } = await axios.get(`${API_BASE}/all`)
    return data ?? []
  } catch (e) {
    console.error('재고 조회 실패', e)
    return []
  }
}
