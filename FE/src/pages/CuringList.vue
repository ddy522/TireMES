<template>
  <div class="space-y-4">
    <div class="flex items-center gap-2 text-sm text-gray-500">
      <span>◀</span>
      <h1 class="text-xl font-bold text-gray-900">가류- 작업지시 조회</h1>
    </div>

    <!-- LOT 스캔 안내 -->
    <div class="card">
      <h3 class="section-title flex items-center gap-2">
        <!-- 아이콘 예시 (lucide-react로 교체 가능) -->
        LOT 스캔
      </h3>
      <p class="text-sm text-gray-600 mb-3">
        LOT를 스캔하면 해당 작업지시를 조회하거나 새로운 작업지시를 생성합니다.
      </p>
      <div class="flex gap-2">
        <input
          v-model="lotInput"
          type="text"
          placeholder="LOT 번호를 입력/스캔하세요 (예: LOT-W0001-001)"
          class="flex-1 border rounded-md px-3 py-2 text-sm"
        >
        <button class="btn-primary" @click="insertLot">등록</button>
      </div>
    </div>

    <!-- 공통 셀렉트 박스 예시 (나중에 사용 가능) -->
    
    <CommonSelect
      type="part"
      label="품번"
      v-model="status"
      width = "200px"
    />

    <button class="btn-primary" @click="searchLot">조회</button>
   

    <!-- 작업지시 테이블 -->
    <WorkOrderTable :orders="workOrders" @start="goDetail" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import WorkOrderTable from '../components/WorkOrderTable.vue'
import { fetchWorkOrders } from '../api/workOrderListSearch.js'
import CommonSelect from '../components/CommonSelect.vue'

const router = useRouter()

// 상태값들
const lotInput = ref('')
const workOrders = ref([])
const status = ref('')
const priority = ref('')

// 상세 페이지 이동
function goDetail(orderId) {
  router.push(`/curing/${orderId}`)
}

// 페이지 로딩 시 초기 데이터 조회
onMounted(async () => {
  workOrders.value = await fetchWorkOrders()
})

// LOT 조회
async function searchLot() {
  if (lotInput.value) {
    workOrders.value = await fetchWorkOrders(lotInput.value)
    if (workOrders.value.length > 0) {
      router.push(`/curing/${workOrders.value[0].id}`)
    }
  }
}
</script>

<style scoped>
/* 필요하면 스타일 추가 */
</style>
