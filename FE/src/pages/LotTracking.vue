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
    <div class="card">
      <div class="flex items-center gap-3">
        <div class="w-11 h-11 rounded bg-gray-100 flex items-center justify-center text-xl">🏷️</div>
        <div class="flex-1">
          <div class="font-semibold text-gray-900">{{ currentLot.no }}</div>
          <div class="text-xs text-gray-500">{{ currentLot.product }}</div>
        </div>
        <div class="grid grid-cols-3 gap-6 text-sm">
          <div>
            <div class="text-gray-500">현재 상태</div>
            <div class="font-medium">{{ currentLot.state }}</div>
          </div>
          <div>
            <div class="text-gray-500">시작 시간</div>
            <div class="font-medium">{{ currentLot.start }}</div>
          </div>
          <div class="min-w-56">
            <div class="text-gray-500 mb-1">진행률</div>
            <div class="w-full bg-gray-100 h-2 rounded">
              <div class="h-2 rounded bg-gray-800" :style="{ width: progress + '%' }"></div>
            </div>
            <div class="text-right text-xs mt-1">{{ progress }}%</div>
          </div>
        </div>
      </div>
    </div>

    <!-- 공정 진행 현황 (타임라인/스텝) -->
    <LotTimeline :steps="steps" />

    <!-- 공정별 이력 LOT 테이블 -->
    <LotHistoryTable :rows="historyRows" class="mt-2" />
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import LotTimeline from '../components/lot/LotTimeline.vue'
import LotHistoryTable from '../components/lot/LotHistoryTable.vue'

const lotInput = ref('LOT-240316-001')

// 데모 데이터 (실제 연동 시 API 응답으로 대체)
const steps = ref([
  { key: 'mixing',   name: '믹싱',   window: '08:00 - 10:30', worker: '김철수',   status: '완료' },
  { key: 'extrude',  name: '압출',   window: '10:45 - 13:20', worker: '이영희',   status: '완료' },
  { key: 'cutting',  name: '재단',   window: '13:30 - 14:45', worker: '박민수',   status: '완료' },
  { key: 'curing',   name: '가류',   window: '15:00 - -',     worker: '최영수',   status: '진행중' },
  { key: 'inspect',  name: '검사',   window: '- - -',         worker: '-',        status: '대기' },
])

const historyRows = ref([
  // 공정별로 “그 공정에서 부여된 LOT 번호”와 기본 정보
  { order: 1, process: '믹싱',   lotNo: 'MIX-240316-045',  qty: 100,  status: '완료',   timestamp: '2024-03-16 10:30', operator: '김철수' },
  { order: 2, process: '압출',   lotNo: 'EXT-240316-012',  qty: 98,   status: '완료',   timestamp: '2024-03-16 13:20', operator: '이영희' },
  { order: 3, process: '재단',   lotNo: 'CUT-240316-008',  qty: 98,   status: '완료',   timestamp: '2024-03-16 14:45', operator: '박민수' },
  { order: 4, process: '가류',   lotNo: 'CUR-240316-003',  qty: 96,   status: '진행중', timestamp: '2024-03-16 15:40', operator: '최영수' },
  { order: 5, process: '검사',   lotNo: '-',               qty: '-',  status: '대기',   timestamp: '-',                operator: '-' },
])

const currentLot = ref({
  no: 'LOT-240316-001',
  product: '205/55R16 승용차 타이어',
  state: '가류중',
  start: '2024-03-16 08:00',
})

const progress = computed(() => {
  const done = steps.value.filter(s => s.status === '완료').length
  const half = steps.value.filter(s => s.status === '진행중').length * 0.5
  return Math.round(((done + half) / steps.value.length) * 100)
})

function search() {
  // 여기서 lotInput.value를 이용해 API 호출 후 currentLot/steps/historyRows 갱신하면 됨.
  // 데모에서는 그대로 둡니다.
}
</script>
