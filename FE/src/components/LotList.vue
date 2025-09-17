<template>
  <div class="space-y-3">
    <!-- LOT 목록 헤더 -->
    <div class="flex items-center justify-between mb-1">
      <h4 class="section-title">LOT 목록</h4>
      <div class="text-xs text-gray-500">{{ lots.length }}개 LOT</div>
    </div>

    <!-- LOT 카드 반복 -->
    <div v-for="lot in lots" :key="lot.no" class="border rounded-md p-3">
      <div class="flex items-start justify-between">
        <div>
          <div class="font-medium text-gray-900">{{ lot.no }}</div>
          <div class="text-xs text-gray-500">{{ lot.qty }}개 · 생성: {{ lot.createdAt }}</div>
        </div>
        <div class="flex items-center gap-2">
          <span :class="badgeClass(lot.status)" class="badge">{{ lot.status }}</span>
        </div>
      </div>

      <div class="mt-2 flex gap-2 text-xs">
        <div class="card flex-1">
          <div class="text-gray-500">현재 공정</div>
          <div class="font-medium">{{ lot.currentProc }}</div>
        </div>
        <div class="card flex-1">
          <div class="text-gray-500">공정 이력</div>
          <div class="flex flex-wrap gap-1">
            <span v-for="p in lot.history" :key="p" class="badge badge-gray">{{ p }}</span>
          </div>
        </div>
      </div>

      <div class="mt-3 flex justify-end gap-2">
        <button class="btn bg-white border" @click="openModal(lot)">🔍 추적</button>
        <button class="btn bg-red-500 text-white border" @click="deleteLot(lot.no)">LOT 삭제</button>
      </div>
    </div>

    <!-- LOT 추적 모달 -->
    <div v-if="showModal" class="fixed inset-0 bg-black/50 flex justify-center items-start pt-20 z-50">
      <div class="bg-white rounded-lg shadow-lg w-[600px] max-h-[80vh] overflow-y-auto p-4">
        <div class="flex justify-between items-center mb-3">
          <h5 class="font-bold text-lg">{{ selectedLot.no }} 세부 추적</h5>
          <button @click="closeModal" class="text-gray-500 hover:text-gray-800">&times;</button>
        </div>

        <!-- 그리드 테이블 -->
        <table class="w-full text-sm border border-gray-200">
          <thead class="bg-gray-100">
            <tr>
              <th class="border px-2 py-1 text-left">LOT 번호</th>
              <th class="border px-2 py-1 text-left">공정</th>
              <th class="border px-2 py-1 text-left">작업일자</th>
              <th class="border px-2 py-1 text-left">작업수량</th>
            </tr>
          </thead>
          <tbody>
            <tr>
              <td class="border px-2 py-1">{{ selectedLot.no }}</td>
              <td class="border px-2 py-1">{{ selectedLot.currentProc }}</td>
              <td class="border px-2 py-1">{{ selectedLot.createdAt }}</td>
              <td class="border px-2 py-1">{{ selectedLot.qty }}</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const props = defineProps({ lots: { type: Array, default: () => [] } })

const showModal = ref(false)
const selectedLot = ref({})

function badgeClass(status) {
  if (status === '완료') return 'badge-green'
  if (status === '진행중') return 'badge-blue'
  if (status === '대기') return 'badge-gray'
  return 'badge-gray'
}

function openModal(lot) {
  selectedLot.value = lot
  showModal.value = true
}

function closeModal() {
  showModal.value = false
}

function deleteLot(no) {
  const idx = props.lots.findIndex(l => l.no === no)
  if (idx !== -1) props.lots.splice(idx, 1)
}
</script>
