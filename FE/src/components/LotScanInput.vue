<template>
  <div class="mb-3">
    <label class="sr-only" for="lot-input">원자재 LOT 스캔</label>
    <div class="flex gap-2">
      <div class="flex-1 relative">
        <input
          id="lot-input"
          v-model="lotNo"
          type="text"
          :placeholder="placeholder"
          class="w-full border rounded-md px-3 py-2 pr-10 text-sm"
          @keyup.enter="emitAdd"
        />
        <button
          @click="toggleQRScanner"
          class="absolute right-2 top-1/2 transform -translate-y-1/2 text-gray-500 hover:text-gray-700"
          :title="isScanning ? 'QR 스캔 중지' : 'QR 코드 스캔'"
        >
          <svg v-if="!isScanning" width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <rect x="3" y="3" width="7" height="7" stroke="currentColor" stroke-width="2" fill="none"/>
            <rect x="14" y="3" width="7" height="7" stroke="currentColor" stroke-width="2" fill="none"/>
            <rect x="3" y="14" width="7" height="7" stroke="currentColor" stroke-width="2" fill="none"/>
            <rect x="5" y="5" width="3" height="3" fill="currentColor"/>
            <rect x="16" y="5" width="3" height="3" fill="currentColor"/>
            <rect x="5" y="16" width="3" height="3" fill="currentColor"/>
            <rect x="14" y="14" width="7" height="7" stroke="currentColor" stroke-width="2" fill="none"/>
          </svg>
          <svg v-else width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="2"/>
            <rect x="9" y="9" width="6" height="6" fill="currentColor"/>
          </svg>
        </button>
      </div>
      <button class="btn-primary" @click="emitAdd">{{ buttonText }}</button>
    </div>
    
    <!-- QR 스캐너 모달 -->
    <div v-if="isScanning" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div class="bg-white rounded-lg p-4 max-w-sm w-full mx-4">
        <div class="flex justify-between items-center mb-4">
          <h3 class="text-lg font-semibold">QR 코드 스캔</h3>
          <button @click="stopScanning" class="text-gray-500 hover:text-gray-700">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none">
              <path d="M18 6L6 18M6 6L18 18" stroke="currentColor" stroke-width="2"/>
            </svg>
          </button>
        </div>
        
        <div class="relative">
          <video ref="videoElement" class="w-full rounded" autoplay playsinline></video>
          <canvas ref="canvasElement" class="hidden"></canvas>
          
          <!-- 스캔 가이드 라인 -->
          <div class="absolute inset-0 flex items-center justify-center">
            <div class="w-48 h-48 border-2 border-red-500 border-dashed rounded-lg"></div>
          </div>
        </div>
        
        <div v-if="scanError" class="mt-2 text-red-600 text-sm">
          {{ scanError }}
        </div>
        
        <div class="mt-4 text-sm text-gray-600">
          QR 코드를 카메라에 맞춰주세요
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'

// Props 정의
const props = defineProps({
  placeholder: {
    type: String,
    default: '원자재 LOT 번호를 스캔하세요 (예: MAT-003-LOT-001)'
  },
  buttonText: {
    type: String,
    default: '투입'
  }
})

const lotNo = ref('')
const emit = defineEmits(['add'])

// QR 스캔 관련
const isScanning = ref(false)
const videoElement = ref(null)
const canvasElement = ref(null)
const scanError = ref('')
let stream = null
let scanInterval = null
let jsQR = null

// jsQR 라이브러리 동적 로드
onMounted(async () => {
  try {
    // CDN에서 jsQR 라이브러리 로드
    const script = document.createElement('script')
    script.src = 'https://cdn.jsdelivr.net/npm/jsqr@1.4.0/dist/jsQR.js'
    script.onload = () => {
      jsQR = window.jsQR
    }
    document.head.appendChild(script)
  } catch (error) {
    console.error('QR 라이브러리 로드 실패:', error)
  }
})

function emitAdd() {
  if (!lotNo.value) return
  emit('add', lotNo.value)
  lotNo.value = ''
}

async function toggleQRScanner() {
  if (isScanning.value) {
    stopScanning()
  } else {
    await startScanning()
  }
}

async function startScanning() {
  if (!jsQR) {
    scanError.value = 'QR 스캔 라이브러리가 로드되지 않았습니다.'
    return
  }

  try {
    scanError.value = ''
    isScanning.value = true
    
    // 카메라 접근 권한 요청
    stream = await navigator.mediaDevices.getUserMedia({
      video: { 
        facingMode: 'environment', // 후면 카메라 우선
        width: { ideal: 1280 },
        height: { ideal: 720 }
      }
    })
    
    if (videoElement.value) {
      videoElement.value.srcObject = stream
      videoElement.value.play()
      
      // 스캔 시작
      scanInterval = setInterval(scanQRCode, 500)
    }
  } catch (error) {
    console.error('카메라 접근 실패:', error)
    scanError.value = '카메라에 접근할 수 없습니다. 권한을 확인해주세요.'
    isScanning.value = false
  }
}

function scanQRCode() {
  if (!videoElement.value || !canvasElement.value || !jsQR) return
  
  const video = videoElement.value
  const canvas = canvasElement.value
  const context = canvas.getContext('2d')
  
  if (video.readyState === video.HAVE_ENOUGH_DATA) {
    canvas.width = video.videoWidth
    canvas.height = video.videoHeight
    context.drawImage(video, 0, 0, canvas.width, canvas.height)
    
    const imageData = context.getImageData(0, 0, canvas.width, canvas.height)
    const code = jsQR(imageData.data, imageData.width, imageData.height)
    
    if (code) {
      lotNo.value = code.data
      stopScanning()
      
      // 자동으로 추가하고 싶다면 주석 해제
      // emitAdd()
    }
  }
}

function stopScanning() {
  isScanning.value = false
  scanError.value = ''
  
  if (scanInterval) {
    clearInterval(scanInterval)
    scanInterval = null
  }
  
  if (stream) {
    stream.getTracks().forEach(track => track.stop())
    stream = null
  }
  
  if (videoElement.value) {
    videoElement.value.srcObject = null
  }
}

onUnmounted(() => {
  stopScanning()
})
</script>

<style scoped>
.btn-primary {
  @apply bg-blue-600 hover:bg-blue-700 text-white font-medium py-2 px-4 rounded-md text-sm transition-colors;
}
</style>