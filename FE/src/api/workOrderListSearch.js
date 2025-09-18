// src/api/workOrderListSearch.js
import axios from 'axios'

// 기본 URL 설정 (Spring Boot 서버 주소)
const API_BASE = 'http://localhost:8080/api'  // 실제 스프링 서버 URL로 변경

/**
 * 작업지시 리스트 조회
 * @param {string} lot LOT 번호 (옵션)
 * @returns {Promise<Array>} workOrders 배열
 */
export async function fetchWorkOrders(lot = '') {
  try {
    const response = await axios.get(`${API_BASE}/work-orders`, {
      params: { lot }  // lot가 있으면 검색 조건으로 넘김
    })
    return response.data  // Spring에서 JSON으로 보내주는 작업지시 배열
  } catch (error) {
    console.error('작업지시 조회 실패', error)
    return []  // 실패 시 빈 배열 반환
  }
}
