<template>
  <div class="space-y-6">
    <!-- 헤더 -->
    <div class="flex items-center gap-2 text-sm text-gray-500">
      <h1 class="text-2xl font-bold text-gray-900">로트 추적</h1>
      <span class="ml-auto text-xs text-blue-600 bg-blue-50 border border-blue-200 rounded px-2 py-0.5">/lot-tracking</span>
    </div>

    <!-- LOT 검색 -->
    <div class="card">
      <h3 class="section-title">로트 번호 검색</h3>
      <p class="text-sm text-gray-600 mb-3">로트 번호를 입력하면 상세 추적 정보를 확인합니다</p>
      <div class="flex gap-2">
        <input v-model.trim="lotInput" class="flex-1 border rounded-md px-3 py-2 text-sm"
               placeholder="예) LOT-240316-001" @keyup.enter="search"/>
        <button class="btn bg-white border" @click="search">검색</button>
      </div>
    </div>

    <!-- 요약 카드 -->
    <div class="card" v-if="currentLot">
      <div class="flex items-center gap-3">
        <div class="w-11 h-11 rounded bg-gray-100 flex items-center justify-center text-xl">🏷️</div>
        <div class="flex-1">
          <div class="font-semibold text-gray-900">{{ currentLot.lotno }}</div>
          <div class="text-xs text-gray-500">{{ currentLot.partName }}</div>
        </div>
        <div class="grid grid-cols-3 gap-6 text-sm">
          <div>
            <div class="text-gray-500">현재 상태</div>
            <div class="font-medium">{{ currentLot.processName }}</div>
          </div>
          <div>
            <div class="text-gray-500">시작 시간</div>
            <div class="font-medium">{{ currentLot.createdAt }}</div>
          </div>
          <!-- <div class="min-w-56">
            <div class="text-gray-500 mb-1">진행률</div>
            <div class="w-full bg-gray-100 h-2 rounded">
              <div class="h-2 rounded bg-gray-800" :style="{ width: progress + '%' }"></div>
            </div>
            <div class="text-right text-xs mt-1">{{ progress }}%</div>
          </div> -->
        </div>
      </div>
    </div>

    <!-- 공정별 이력 LOT 테이블 -->
    <LotHistoryTable :rows="historyRows" class="mt-2" />
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import axios from 'axios'
import LotHistoryTable from '../components/lot/LotHistoryTable.vue'

const lotInput = ref('0320250924002')  // 기본값 샘플
const currentLot = ref(null)
const steps = ref([])
const historyRows = ref([])

// 진행률 계산 (steps가 없으면 0)
const progress = computed(() => {
  if (!steps.value || steps.value.length === 0) return 0
  const done = steps.value.filter(s => s.status === '완료').length
  const half = steps.value.filter(s => s.status === '진행중').length * 0.5
  return Math.round(((done + half) / steps.value.length) * 100)
})

function search() {
  if (!lotInput.value) return

  console.log("lot:",lotInput.value);

  axios.get(`/api/lot-tracking/${lotInput.value}`)
    .then(res => {
      const data = res.data  // data는 배열
      console.log(data);

      if (!data || data.length === 0) {
        alert('조회된 LOT가 없습니다.')
        currentLot.value = null
        historyRows.value = []
        steps.value = []
        return
      }

      // 🔹 검색한 LOT 번호와 일치하는 데이터를 찾음
      const searchedLot = data.find(l => l.lotno === lotInput.value)

      // 🔹 못 찾으면 fallback으로 마지막 LOT 사용
      const lotInfo = searchedLot || data[data.length - 1]

      // currentLot에 설정
      currentLot.value = {
        lotno: lotInfo.lotno,
        partName: lotInfo.partName,
        processName: lotInfo.processName,
        createdAt: lotInfo.createdAt,
        partCode: lotInfo.partCode,
        qty: lotInfo.qty
      }

      // steps: 각 공정을 완료 처리 (임시)
      steps.value = data.map(l => ({
        status: '완료',
        processName: l.processName
      }))

      // historyRows: 전체 LOT 배열 그대로 테이블에 넣기
      historyRows.value = data.map((l, idx) => ({
        order: idx + 1,
        lotno: l.lotno,
        processName: l.processName,
        partCode: l.partCode,
        partName: l.partName,
        qty: l.qty,
        createdAt: l.createdAt
      }))
    })
    .catch(err => {
      console.error(err)
      alert('LOT 조회 중 오류가 발생했습니다.')
    })
}


</script>
