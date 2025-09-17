<template>
  <div class="space-y-6">
    <div class="flex items-center gap-2 text-sm text-gray-500">
      <RouterLink to="/extrusion" class="hover:underline">◀ 뒤로가기</RouterLink>
      <h1 class="text-xl font-bold text-gray-900">재단 작업화면</h1>
      <span class="ml-auto text-xs text-blue-600 bg-blue-50 border border-blue-200 rounded px-2 py-0.5">실시간 작업</span>
    </div>

    <section class="grid grid-cols-1 lg:grid-cols-2 gap-6">
      <!-- 좌: 작업지시 정보 / 생산완료처리 -->
      <div class="space-y-6">
        <div class="card">
          <h3 class="section-title">작업지시 정보</h3>
          <div class="flex items-center gap-4">
            <div class="w-20 h-20 bg-gray-100 rounded-md flex items-center justify-center">
              <span class="text-3xl">🧵</span>
            </div>
            <div class="text-sm">
              <div class="text-gray-500">작업지시번호</div>
              <div class="font-semibold text-gray-900">{{ id }}</div>
              <div class="mt-2 grid grid-cols-3 gap-4">
                <div><div class="text-gray-500">제품명</div><div class="font-medium">프리미엄 타이어 225/60R17</div></div>
                <div><div class="text-gray-500">계획수량</div><div class="font-medium">800개</div></div>
                <div><div class="text-gray-500">진행상태</div><span class="badge badge-yellow">대기중</span></div>
              </div>
            </div>
          </div>
        </div>

        <div class="card">
          <h3 class="section-title">생산완료처리</h3>
          <div class="grid grid-cols-1 md:grid-cols-3 gap-3">
            <input v-model.number="doneQty" type="number" placeholder="완료된 수량 입력" class="border rounded-md px-3 py-2 text-sm">
            <select v-model="quality" class="border rounded-md px-3 py-2 text-sm">
              <option value="합격">합격</option>
              <option value="불합격">불합격</option>
            </select>
            <input v-model="remark" type="text" placeholder="특이사항이나 비고를 입력하세요" class="border rounded-md px-3 py-2 text-sm md:col-span-3">
          </div>
          <div class="mt-3">
            <button class="btn-primary w-full md:w-auto">다음 공정 발행</button>
          </div>
        </div>
      </div>

      <!-- 우: LOT 관리 시스템 (BOM 대신 로트 추적) -->
      <div class="card">
        <div class="flex items-center justify-between mb-2">
          <h3 class="section-title">LOT 관리 시스템</h3>
          <div class="text-xs text-gray-500">작업지시 {{ id }}의 LOT 현황 및 관리</div>
        </div>

        <LotCreateForm @create="createLot" />

        <LotList :lots="lots" class="mt-4" />
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRoute } from 'vue-router'
import LotCreateForm from '../components/LotCreateForm.vue'
import LotList from '../components/LotList.vue'

const route = useRoute()
const id = route.params.id

const doneQty = ref(0)
const quality = ref('합격')
const remark = ref('')

const lots = ref([
  { no: 'LOT-W0001-001', qty: 25, createdAt: '14:30', status: '완료', currentProc: '재단', quality: '합격', history: ['믹싱', '압출'] },
  { no: 'LOT-W0001-002', qty: 30, createdAt: '15:15', status: '진행중', currentProc: '압출', quality: '검사중', history: ['믹싱'] },
])

function createLot(qty) {
  const idx = (lots.value.length + 1).toString().padStart(3, '0')
  lots.value.push({
    no: `LOT-W0001-${idx}`,
    qty, createdAt: new Date().toTimeString().slice(0,5),
    status: '대기', currentProc: '압출', quality: '대기', history: ['믹싱']
  })
}
</script>
