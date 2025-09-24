// utils/qrGenerator.js
import QRCode from 'qrcode'

/**
 * QR 코드를 생성하고 다운로드하는 유틸리티
 */
export class QRCodeGenerator {
  /**
   * QR 코드를 생성하여 Canvas로 반환
   * @param {string} text - QR 코드에 담을 텍스트 (LOT 번호)
   * @param {Object} options - QR 코드 옵션
   * @returns {Promise<HTMLCanvasElement>}
   */
  static async generateCanvas(text, options = {}) {
    const defaultOptions = {
      width: 256,
      margin: 2,
      color: {
        dark: '#000000',
        light: '#FFFFFF'
      },
      errorCorrectionLevel: 'M'
    }

    const finalOptions = { ...defaultOptions, ...options }
    
    try {
      const canvas = document.createElement('canvas')
      await QRCode.toCanvas(canvas, text, finalOptions)
      return canvas
    } catch (error) {
      console.error('QR 코드 생성 실패:', error)
      throw new Error('QR 코드 생성에 실패했습니다.')
    }
  }

  /**
   * QR 코드를 Data URL로 생성
   * @param {string} text - QR 코드에 담을 텍스트
   * @param {Object} options - QR 코드 옵션
   * @returns {Promise<string>}
   */
  static async generateDataURL(text, options = {}) {
    const defaultOptions = {
      width: 256,
      margin: 2,
      color: {
        dark: '#000000',
        light: '#FFFFFF'
      },
      errorCorrectionLevel: 'M'
    }

    const finalOptions = { ...defaultOptions, ...options }
    
    try {
      return await QRCode.toDataURL(text, finalOptions)
    } catch (error) {
      console.error('QR 코드 생성 실패:', error)
      throw new Error('QR 코드 생성에 실패했습니다.')
    }
  }

  /**
   * QR 코드를 생성하고 파일로 다운로드
   * @param {string} lotNumber - LOT 번호
   * @param {string} filename - 다운로드할 파일명 (확장자 제외)
   * @param {Object} options - QR 코드 옵션
   */
  static async downloadQRCode(lotNumber, filename = null, options = {}) {
    try {
      // 파일명 생성 (미지정 시 LOT 번호 + 타임스탬프 사용)
      const defaultFilename = filename || `LOT_${lotNumber}_${new Date().toISOString().slice(0, 19).replace(/[:-]/g, '')}`
      
      // QR 코드 생성
      const canvas = await this.generateCanvas(lotNumber, options)
      
      // Canvas를 Blob으로 변환
      return new Promise((resolve, reject) => {
        canvas.toBlob((blob) => {
          if (!blob) {
            reject(new Error('QR 코드 변환에 실패했습니다.'))
            return
          }

          // 다운로드 링크 생성
          const url = URL.createObjectURL(blob)
          const link = document.createElement('a')
          link.href = url
          link.download = `${defaultFilename}.png`
          
          // 다운로드 실행
          document.body.appendChild(link)
          link.click()
          document.body.removeChild(link)
          
          // 메모리 정리
          URL.revokeObjectURL(url)
          
          resolve({
            lotNumber,
            filename: `${defaultFilename}.png`,
            success: true
          })
        }, 'image/png', 0.9)
      })
    } catch (error) {
      console.error('QR 코드 다운로드 실패:', error)
      throw error
    }
  }

  /**
   * 여러 LOT 번호에 대한 QR 코드를 ZIP으로 묶어서 다운로드
   * @param {Array<string>} lotNumbers - LOT 번호 배열
   * @param {string} zipFilename - ZIP 파일명
   * @param {Object} options - QR 코드 옵션
   */
  static async downloadMultipleQRCodes(lotNumbers, zipFilename = 'QR_Codes', options = {}) {
    try {
      // JSZip이 필요한 경우 (선택적으로 구현)
      if (typeof JSZip === 'undefined') {
        // 개별 다운로드로 대체
        console.warn('JSZip이 없어서 개별 다운로드로 처리합니다.')
        const results = []
        for (const lotNumber of lotNumbers) {
          const result = await this.downloadQRCode(lotNumber)
          results.push(result)
        }
        return results
      }

      const zip = new JSZip()
      
      for (const lotNumber of lotNumbers) {
        const canvas = await this.generateCanvas(lotNumber, options)
        
        // Canvas를 Blob으로 변환하여 ZIP에 추가
        await new Promise((resolve) => {
          canvas.toBlob((blob) => {
            zip.file(`LOT_${lotNumber}.png`, blob)
            resolve()
          }, 'image/png', 0.9)
        })
      }

      // ZIP 파일 생성 및 다운로드
      const zipBlob = await zip.generateAsync({ type: 'blob' })
      const url = URL.createObjectURL(zipBlob)
      const link = document.createElement('a')
      link.href = url
      link.download = `${zipFilename}.zip`
      
      document.body.appendChild(link)
      link.click()
      document.body.removeChild(link)
      URL.revokeObjectURL(url)

      return {
        lotNumbers,
        filename: `${zipFilename}.zip`,
        count: lotNumbers.length,
        success: true
      }
    } catch (error) {
      console.error('다중 QR 코드 다운로드 실패:', error)
      throw error
    }
  }
}