<template>
  <div class="space-y-4">
    <div class="flex items-center gap-2 text-sm text-gray-500">
      <span>◀</span>
      <h1 class="text-xl font-bold text-gray-900">압출 - 작업지시 조회</h1>
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
        LOT를 스캔하면 해당 작업지시를 조회하거나 로트 추적 화면으로 이동합니다.
      </p>
      <div class="flex gap-2">
        <input v-model="lotInput" type="text" placeholder="LOT 번호를 입력/스캔하세요 (예: LOT-W0001-001)"
               class="flex-1 border rounded-md px-3 py-2 text-sm">
        <button class="btn-primary" @click="searchLot">조회</button>
      </div>
    </div>

    <WorkOrderTable :orders="workOrders" @start="goDetail" />
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import WorkOrderTable from '../components/WorkOrderTable.vue'

const router = useRouter()
const lotInput = ref('')
const workOrders = ref([
  { id: 'WO-2024-001', product: '타이어 A', qty: 100, status: '진행중', priority: '높음', start: '09:00', end: '17:00' },
  { id: 'WO-2024-002', product: '타이어 B', qty: 150, status: '대기', priority: '보통', start: '10:00', end: '18:00' },
  { id: 'WO-2024-003', product: '타이어 C', qty: 80,  status: '완료', priority: '낮음', start: '08:00', end: '16:00' },
])

function goDetail(orderId) {
  router.push(`/extrusion/${orderId}`)
}
function searchLot() {
  if (lotInput.value) router.push('/extrusion/WO-2024-001')
}
</script>
