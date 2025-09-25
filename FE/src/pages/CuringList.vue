<template>
  <div class="space-y-4">
    <div class="flex items-center gap-2 text-sm text-gray-500">
      <span>◀</span>
      <h1 class="text-xl font-bold text-gray-900">가류 - 작업지시 조회</h1>
    </div>

    <!-- LOT 스캔 안내 -->
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
        LOT를 스캔하면 작업지시가 등록됩니다.
      </p>
      
      <!-- QR 컴포넌트 사용 -->
      <QRLotInput 
        v-model="lotInput"
        placeholder="LOT 번호를 입력/스캔하세요 (예: LOT-W0001-001)"
        button-text="등록"
        @submit="insertLot"
      />
    </div>

    <!-- ✅ 버튼만, 오른쪽 정렬 -->
    <div class="flex justify-end gap-2">
      <button class="px-4 py-2 rounded-lg border bg-white hover:bg-gray-50">
        금형 교체
      </button>
      <button class="px-4 py-2 rounded-lg bg-blue-600 text-white hover:bg-blue-700">
        교체 완료
      </button>
    </div>

    <!-- 작업지시 목록 -->
    <WorkOrderTable :orders="workOrders" @start="goDetail" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import WorkOrderTable from '../components/WorkOrderTable.vue'
import QRLotInput from '../components/QRLotInput.vue'
import { fetchWorkOrders } from '../api/workOrderListSearch.js'
import { insertLotno } from '../api/insertLotno.js'

const router = useRouter()
const lotInput = ref('')
const workOrders = ref([])

// 상세페이지 이동
function goDetail(orderId) {
  router.push(`/curing/${orderId}`)
}

// 페이지 로딩 시 초기 데이터 조회
onMounted(async () => {
  workOrders.value = await fetchWorkOrders()
})

function searchLot() {
  if (lotInput.value) router.push('/curing/WO-2024-001')
}

// LOT 등록 - QR 컴포넌트에서 emit된 submit 이벤트 처리
async function insertLot(lotNo) {
  if (!lotNo) {
    alert("LOT 번호를 입력해주세요.")
    return
  }
  try {
    const proc = "curing"
    const result = await insertLotno(lotNo, proc)  // 서버 호출
    console.log("✅ 등록 결과:", result)

    if (result.success) {
      alert(result.message)   // 성공 메시지 출력
      // 등록 후 리스트 갱신
      workOrders.value = await fetchWorkOrders()
    } else {
      // 실패 메시지 alert
      alert(result.message)
    }

  } catch (e) {
    // 네트워크 오류나 서버 예외 처리
    console.error(e)
    alert("등록 중 오류가 발생했습니다.")
  }
}
</script>

<style scoped>
.btn-primary {
  @apply bg-blue-600 hover:bg-blue-700 text-white font-medium py-2 px-4 rounded-md text-sm transition-colors;
}

.card {
  @apply bg-white rounded-lg shadow p-4;
}

.section-title {
  @apply text-lg font-semibold text-gray-900 mb-2;
}
</style>