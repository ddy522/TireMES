// workOrderListSearch.js
import axios from 'axios'

const API_BASE = 'http://localhost:8080/worksheet'  // 컨트롤러 기준

export async function fetchWorkOrders(processNameEng = '') {
  // ✅ Mock 모드일 경우
  if (import.meta.env.VITE_USE_MOCK === 'true') {
    return [
      { id: 'wo-001', proc: '믹싱', eq: '믹싱기 A', status: '진행중', due: '2025-09-20' },
      { id: 'wo-002', proc: '압출', eq: '압출기 A', status: '대기', due: '2025-09-22' },
      { id: 'wo-003', proc: '성형', eq: '성형기 A', status: '완료', due: '2025-09-18' }
    ]
  }

  // ✅ 실제 API 모드일 경우
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
