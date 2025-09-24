// src/api/workOrderListSearch.js
import axios from 'axios'

const API_BASE = 'http://localhost:8080/worksheet'  // 컨트롤러 기준

// 빈값은 URL 파라미터에서 제거
function clean(params = {}) {
  return Object.fromEntries(
    Object.entries(params).filter(([, v]) => v !== '' && v !== null && v !== undefined)
  )
}

export async function fetchWorkOrders(processNameEng = '') {
  try {
    let url
    if (processNameEng) {
      url = `${API_BASE}/${processNameEng}`  // 공정별 조회 (기존 유지)
    } else {
      url = `${API_BASE}/all`                // 전체 조회
    }

    const response = await axios.get(url)
    console.log(response.data)
    return response.data

  } catch (error) {
    console.error('작업지시 조회 실패', error)
    return []
  }
}

// ★ 추가: 검색 엔드포인트 사용
export async function searchWorkOrders({ processNameEng = '', partCode = '', workStatus = '', workNo = '' } = {}) {
  try {
    const { data } = await axios.get(`${API_BASE}/search`, {
      params: clean({ processNameEng, partCode, workStatus, workNo })
    })
    return data
  } catch (error) {
    console.error('작업지시 검색 실패', error)
    return []
  }
}


// import axios from 'axios'

// const API_BASE = 'http://localhost:8080/worksheet'  // 컨트롤러 기준

// export async function fetchWorkOrders(processNameEng = '') {
//   try {
//     let url
//     if (processNameEng) {
//       url = `${API_BASE}/${processNameEng}`  // 공정별 조회
//     } else {
//       url = `${API_BASE}/all`  // 전체 조회
//     }

//     const response = await axios.get(url)
//     return response.data
//   } catch (error) {
//     console.error('작업지시 조회 실패', error)
//     return []
//   }
// }
