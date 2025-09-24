// src/composables/useProduction.js
import { ref } from 'vue'
import { QRCodeGenerator } from '../utils/qrGenerator'

export function useProduction() {
  const loading = ref(false)
  const error = ref('')

  /**
   * 생산완료 처리 및 LOT 생성
   * @param {Object} productionData - 생산완료 데이터
   * @param {string} productionData.workNo - 작업지시번호
   * @param {number} productionData.doneQty - 완료수량
   * @param {string} productionData.remark - 비고
   * @returns {Promise<Object>} - { lotNumber, success }
   */
  async function completeProduction(productionData) {
    try {
      loading.value = true
      error.value = ''

      console.log('생산완료 처리 시작:', productionData)

      // 임시 목 데이터 (API 개발 완료 전까지 사용)
      // TODO: 실제 API 완성 후 아래 주석을 해제하고 목 데이터 부분 삭제
      
      // 목 데이터로 임시 처리
      const mockLotNumber = `LOT${new Date().getFullYear()}${String(new Date().getMonth() + 1).padStart(2, '0')}${String(new Date().getDate()).padStart(2, '0')}${String(Math.floor(Math.random() * 1000)).padStart(3, '0')}`
      
      // 실제 응답 시뮬레이션 (2초 대기)
      await new Promise(resolve => setTimeout(resolve, 2000))
      
      const result = {
        lotNumber: mockLotNumber,
        success: true,
        message: '생산완료 처리가 완료되었습니다.'
      }

      /*
      // 실제 API 호출 (API 준비되면 이 부분 사용)
      const response = await fetch(`http://localhost:8080/api/mixing-detail/production-complete/${productionData.workNo}`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(productionData)
      })

      if (!response.ok) {
        throw new Error(`생산완료 처리에 실패했습니다: ${response.status}`)
      }

      const result = await response.json()
      */

      console.log('생산완료 처리 결과:', result)

      return {
        lotNumber: result.lotNumber,
        success: true,
        message: '생산완료 처리가 완료되었습니다.'
      }

    } catch (err) {
      error.value = err.message
      console.error('생산완료 처리 오류:', err)
      throw err
    } finally {
      loading.value = false
    }
  }

  /**
   * QR 코드 생성 및 다운로드
   * @param {string} lotNumber - LOT 번호
   * @param {Object} qrOptions - QR 코드 옵션
   */
  async function generateAndDownloadQR(lotNumber, qrOptions = {}) {
    try {
      const defaultOptions = {
        width: 300,
        margin: 2,
        color: {
          dark: '#000000',
          light: '#FFFFFF'
        }
      }

      const finalOptions = { ...defaultOptions, ...qrOptions }
      
      await QRCodeGenerator.downloadQRCode(lotNumber, null, finalOptions)
      
      return {
        success: true,
        message: 'QR 코드가 다운로드되었습니다.'
      }
    } catch (err) {
      error.value = 'QR 코드 생성에 실패했습니다.'
      console.error('QR 코드 생성 오류:', err)
      throw err
    }
  }

  /**
   * 생산완료 처리와 QR 코드 생성을 한번에 처리
   * @param {Object} productionData - 생산완료 데이터
   * @param {Object} qrOptions - QR 코드 옵션
   */
  async function completeProductionWithQR(productionData, qrOptions = {}) {
    try {
      // 1. 생산완료 처리 및 LOT 번호 받기
      const productionResult = await completeProduction(productionData)
      
      if (!productionResult.success || !productionResult.lotNumber) {
        throw new Error('LOT 번호를 받지 못했습니다.')
      }

      // 2. QR 코드 생성 및 다운로드
      await generateAndDownloadQR(productionResult.lotNumber, qrOptions)

      return {
        success: true,
        lotNumber: productionResult.lotNumber,
        message: '생산완료 처리 및 QR 코드 다운로드가 완료되었습니다.'
      }
    } catch (err) {
      error.value = err.message
      throw err
    }
  }

  return {
    loading,
    error,
    completeProduction,
    generateAndDownloadQR,
    completeProductionWithQR
  }
}