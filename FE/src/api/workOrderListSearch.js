import axios from 'axios'

const API_BASE = 'http://localhost:8080/worksheet'  // 컨트롤러 기준

export async function fetchWorkOrders(processNameEng = '') {
  try {
    let url
    if (processNameEng) {
      url = `${API_BASE}/${processNameEng}`  // 공정별 조회
    } else {
      url = `${API_BASE}/all`  // 전체 조회
    }

    const response = await axios.get(url)
    return response.data
  } catch (error) {
    console.error('작업지시 조회 실패', error)
    return []
  }
}
