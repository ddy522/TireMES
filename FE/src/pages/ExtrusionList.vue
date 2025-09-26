<template>
  <div class="space-y-4">
    <div class="flex items-center gap-2 text-sm text-gray-500">
      <span>◀</span>
      <h1 class="text-xl font-bold text-gray-900">압출 - 작업지시 조회</h1>
    </div>

    <div class="card">
      <h3 class="section-title flex items-center gap-2">
        <svg xmlns="http://www.w3.org/2000/svg"
            class="w-5 h-5 text-blue-600"
            fill="none" stroke="currentColor" stroke-width="2"
            stroke-linecap="round" stroke-linejoin="round"
            viewBox="0 0 24 24">
          <path d="M3 7V5a2 2 0 0 1 2-2h2"></path>
          <path d="M17 3h2a2 2 0 0 1 2 2v2"></path>
          <path d="M21 17v2a2 2 0 0 1-2 2h-2"></path>
          <path d="M7 21H5a2 2 0 0 1-2-2v-2"></path>
        </svg>
        LOT 스캔
      </h3>
      <p class="text-sm text-gray-600 mb-3">
        LOT를 스캔하거나 입력하면 작업지시가 등록됩니다.
      </p>
      
      <div class="flex gap-2">
        <div class="flex-1 relative">
          <input 
            v-model="lotInput" 
            type="text" 
            placeholder="LOT 번호를 입력/스캔하세요 (예: LOT-W0001-001)"
            class="w-full border rounded-md px-3 py-2 pr-10 text-sm"
            :disabled="isScanning"
          >
          
          <button
            @click="toggleQRScanner"
            class="absolute right-2 top-1/2 transform -translate-y-1/2 text-gray-500 hover:text-gray-700"
            :title="isScanning ? 'QR 스캔 중지' : 'QR 코드 스캔'"
            aria-label="QR 코드 스캔"
          >
            <svg v-if="!isScanning" width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg" stroke="currentColor" stroke-width="2">
              <rect x="3" y="3" width="7" height="7" rx="1" ry="1"/>
              <rect x="14" y="3" width="7" height="7" rx="1" ry="1"/>
              <rect x="3" y="14" width="7" height="7" rx="1" ry="1"/>
              <rect x="16" y="16" width="3" height="3" fill="currentColor"/>
            </svg>
            <svg v-else width="20" height="20" viewBox="0 0 24 24" fill="currentColor" xmlns="http://www.w3.org/2000/svg">
              <circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="2" fill="none"/>
              <rect x="9" y="9" width="6" height="6" rx="1" ry="1"/>
            </svg>
          </button>
        </div>
        
        <button class="btn-primary" @click="insertLot">등록</button>
      </div>
    </div>
    
    <div v-if="isScanning" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div class="bg-white rounded-lg p-4 max-w-sm w-full mx-4 shadow-2xl">
        <div class="flex justify-between items-center mb-4">
          <h3 class="text-lg font-bold text-gray-800">QR 코드 스캔</h3>
          <button @click="stopScanning" class="text-gray-500 hover:text-red-500">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none">
              <path d="M18 6L6 18M6 6L18 18" stroke="currentColor" stroke-width="2"/>
            </svg>
          </button>
        </div>
        
        <div class="relative">
          <video ref="videoElement" class="w-full rounded bg-gray-900" autoplay playsinline></video>
          <canvas ref="canvasElement" class="hidden"></canvas>
        </div>
        
        <div v-if="scanError" class="mt-2 text-red-600 text-sm p-2 border border-red-200 bg-red-50 rounded">
          🚨 **에러:** {{ scanError }}
        </div>
        
        <div class="mt-4 text-sm text-gray-600 text-center">
          QR 코드를 카메라에 맞춰주세요
        </div>
      </div>
    </div>

    <WorkOrderTable :orders="workOrders" @start="goDetail" />
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue' // onUnmounted 추가
import { useRouter } from 'vue-router'
// 기존 import 유지
import WorkOrderTable from '../components/WorkOrderTable.vue'
import { fetchWorkOrders } from '../api/workOrderListSearch.js'
import { insertLotno } from '../api/insertLotno.js'

const router = useRouter()
const lotInput = ref('')
const workOrders = ref([])

// --- QR 스캔 관련 변수 ---
const isScanning = ref(false)
const videoElement = ref(null)
const canvasElement = ref(null)
const scanError = ref('')
let stream = null
let scanInterval = null
let jsQR = null // jsQR 라이브러리 객체

// --- QR 스캔 로직 ---

// jsQR 라이브러리 동적 로드
onMounted(async () => {
  // 기존 데이터 로드 로직
  workOrders.value = await fetchWorkOrders()

  // QR 라이브러리 로드
  try {
    const script = document.createElement('script')
    script.src = 'https://cdn.jsdelivr.net/npm/jsqr@1.4.0/dist/jsQR.js'
    script.onload = () => {
      jsQR = window.jsQR
      if (!jsQR) console.error("QR 라이브러리 로드 실패: window.jsQR 객체 없음");
    }
    document.head.appendChild(script)
  } catch (error) {
    console.error('QR 라이브러리 로드 실패:', error)
  }
})

onUnmounted(() => {
    stopScanning() // 컴포넌트 종료 시 카메라 중지
})


function toggleQRScanner() {
  if (isScanning.value) {
    stopScanning()
  } else {
    // UI 업데이트 보장 및 startScanning 호출
    setTimeout(startScanning, 100); 
  }
}

async function startScanning() {
  if (!jsQR) {
    scanError.value = 'QR 스캔 라이브러리가 로드되지 않았습니다. 잠시 후 다시 시도해주세요.'
    return
  }
  
  console.log('1. navigator.mediaDevices.getUserMedia() 실행 시도...');

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
    
    console.log('2. 카메라 스트림 성공적으로 획득됨.');

    if (videoElement.value) {
      videoElement.value.srcObject = stream
      await new Promise(resolve => setTimeout(resolve, 50)); 
      videoElement.value.play()
      
      console.log('3. 비디오 재생 시작됨.');
      // 스캔 시작 (200ms 간격으로 이미지 분석)
      scanInterval = setInterval(scanQRCode, 200) 
    }
  } catch (error) {
    // 🚨 카메라 접근 실패 시 상세 에러 처리
    console.error('❌ 카메라 접근 실패 상세:', error.name, error);
    
    let userMessage = '카메라에 접근할 수 없습니다. 권한을 확인해주세요.';
    
    if (error.name === 'NotAllowedError' || error.name === 'PermissionDeniedError') {
      userMessage = '카메라 사용이 **브라우저**나 **시스템 설정**에서 **차단**되어 있습니다. [시스템 설정] -> [개인 정보 보호] -> [카메라]에서 브라우저 권한을 확인하고 재시도하세요.';
    } else if (error.name === 'NotReadableError') {
      userMessage = '카메라가 다른 프로그램에 의해 사용 중입니다. 다른 앱을 닫아주세요.';
    } else if (error.name === 'SecurityError') {
      userMessage = 'HTTPS 환경이 아닙니다. (로컬호스트를 사용하거나 HTTPS를 설정해야 합니다.)';
    }
    
    scanError.value = userMessage
    isScanning.value = false
  }
}

function scanQRCode() {
  if (!videoElement.value || !canvasElement.value || !jsQR) return
  
  const video = videoElement.value
  const canvas = canvasElement.value
  const context = canvas.getContext('2d')
  
  if (video.readyState >= video.HAVE_CURRENT_DATA) { 
    canvas.width = video.videoWidth
    canvas.height = video.videoHeight
    context.drawImage(video, 0, 0, canvas.width, canvas.height)
    
    const imageData = context.getImageData(0, 0, canvas.width, canvas.height)
    const code = jsQR(imageData.data, imageData.width, imageData.height)
    
    if (code) {
      // 1. 스캔 성공 시 카메라 중지
      stopScanning() 
      
      // 2. 입력 필드에 값 할당
      lotInput.value = code.data
      
      // 3. 확인 창을 띄운 후 등록 진행 (비동기로 실행)
      const scannedLotNo = code.data;
      
      // 사용자에게 등록 여부 확인
      if (confirm(`LOT 번호 ${scannedLotNo}를 등록하시겠습니까?`)) {
          // 확인하면 등록 로직 실행
          insertLot();
      } else {
          // 취소하면 아무것도 하지 않음 (lotInput.value는 이미 채워져 있음)
          alert("등록이 취소되었습니다.");
      }
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

// --- 기존 로직 유지 ---

// 상세페이지 이동
function goDetail(orderId) {
  router.push(`/extrusion/${orderId}`)
}

// LOT 등록 (기존 로직 유지)
async function insertLot() {
  if (!lotInput.value) {
    alert("LOT 번호를 입력해주세요.")
    return
  }
  // 스캔 중이었다면 중지
  if (isScanning.value) {
      stopScanning()
  }
  
  try {
    const proc = "extrusion"
    const result = await insertLotno(lotInput.value, proc) 

    if (result.success) {
      alert(result.message)
      // 등록 후 리스트 갱신
      workOrders.value = await fetchWorkOrders()
      lotInput.value = '' // 입력 필드 초기화
      window.location.reload(); 
    } else {
      alert(result.message)
    }

  } catch (e) {
    console.error(e)
    alert("등록 중 오류가 발생했습니다.")
  }
  
  // 기존 코드에 있던 중복 호출 제거 (WorkOrders.value = await fetchWorkOrders()는 위에 이미 있습니다.)
}

</script>

<style scoped>
/* 기존 스타일 유지 및 추가 */
.card {
  @apply bg-white p-4 rounded-lg shadow;
}
.section-title {
  @apply text-lg font-semibold mb-3 text-gray-800;
}
.btn-primary {
  @apply bg-blue-600 text-white px-4 py-2 rounded-md hover:bg-blue-700 transition duration-150;
}
.btn-secondary {
  @apply bg-gray-200 text-gray-800 px-4 py-2 rounded-md hover:bg-gray-300 transition duration-150;
}
</style>