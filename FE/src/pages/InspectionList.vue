<template>
  <div class="space-y-4">
    <div class="flex items-center gap-2 text-sm text-gray-500">
      <span>◀</span>
      <h1 class="text-xl font-bold text-gray-900">검사 공정 - 작업지시 조회</h1>
    </div>

    <!-- LOT 스캔 안내 -->
    <div class="card">
      <h3 class="section-title">LOT 스캔</h3>
      <p class="text-sm text-gray-600 mb-3">
        LOT를 스캔하면 해당 작업지시를 조회하거나 검사 공정 상세 화면으로 이동합니다.
      </p>
      <div class="flex gap-2">
        <input
          v-model="lotInput"
          type="text"
          placeholder="LOT 번호를 입력/스캔하세요 (예: LOT-W0001-001)"
          class="flex-1 border rounded-md px-3 py-2 text-sm"
        />
        <button class="btn-primary" @click="searchLot">조회</button>
      </div>
    </div>
        <WorkOrderTable
          @bulk-start="onBulkStart"
          @start="onStartSingle"
    />

    <!-- <WorkOrderTable :orders="workOrders" @start="goDetail" /> -->

  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import WorkOrderTable from '../components/WorkOrderTable.vue'
import { startInspectionOrders } from '../api/inspection.js'
import axios from 'axios'

const router = useRouter()
const lotInput = ref('')
const tableRef = ref(null)

//  선택 작업 시작 (일괄) → 라즈베리 예측 호출 + 상세 페이지 이동
async function onBulkStart({ workNos, count }) {
  try {

    const { data : piResp } = await axios.post(
      '/api/inspection/predict',
      {count}, 
      { timeout: 15000 }
    )
    await axios.post('/api/inspection/start', { workNos })
    
    // 선택된 작업지시 번호들과 작업 수(runCount) 서버에 전달
    console.log('선택된 개수:', count)
    console.log('선택된 목록:', workNos)
    console.log('PI 응답 :', piResp)

    alert(`predict success: status=${piResp?.status}, actual_count=${piResp?.actual_count}`)
   
    // 라즈베리 응답까지 끝나면 상세 페이지로 이동
    router.push({
      name: 'InspectionDetail',
      query: { workNos: workNos.join(',') }
    })



    // 성공 후 테이블 새로고침
    tableRef.value?.reload?.()
  } catch (err) {
    console.error(err)
    alert('작업 시작 요청 중 오류가 발생했습니다.')
  }
}

// async function goInspectMany(workNos) {
//   try {
//     // 성공시 이동
//     await startInspectionOrders(workNos)
//     router.push({
//       name: 'InspectionDetail',
//       query: { workNos: workNos.join(',') }
//     })
//   } catch (e) {
//     console.error(e)
//     alert('검사 시작에 실패했습니다.')
//   }

//   function searchLot() {
//   if (!lotInput.value?.trim()) return
//   router.push({
//     name: 'InspectionDetail',
//     query: { lot: lotInput.value.trim() }
//   })
// }

// }
function goInspectMany(workNos) {
  // 선택된 workNos를 가지고 검사 상세 페이지로 이동
  router.push({
    name: 'InspectionDetail',
    query: { workNos: workNos.join(',') }
  })
}

</script>
