<template>
  <div class="space-y-4">
    <!-- LOT 목록 헤더 -->
    <div class="flex items-center justify-between mb-2">
      <h4 class="section-title text-xl font-bold">LOT 목록</h4>
      <div class="text-sm text-gray-500">{{ lots.length }}개 LOT</div>
    </div>

    <!-- LOT 카드 반복 -->
    <div v-for="lot in lots" :key="lot.lotno" class="border rounded-xl p-4 shadow-md hover:shadow-lg transition bg-white">
      <!-- 상단 LOT 번호 + 상태 -->
      <div class="flex justify-between items-center mb-3">
        <div class="space-y-1">
          <div class="text-lg font-semibold text-gray-900">LOTNO : {{ lot.lotno }}</div>
          <div class="text-sm text-gray-500">{{ lot.lotDate }} · 생성: {{ lot.createdAt }}</div>
        </div>
      </div>

      <!-- 개별 정보 박스 -->
      <div class="grid grid-cols-2 gap-3 mb-3">
        <div class="border rounded-lg p-3 bg-gray-50">
          <div class="text-gray-500 text-sm">품번</div>
          <div class="font-semibold text-gray-800">{{ lot.partCode }}</div>
        </div>
        <div class="border rounded-lg p-3 bg-gray-50">
          <div class="text-gray-500 text-sm">품명</div>
          <div class="font-semibold text-gray-800">{{ lot.partName }}</div>
        </div>
        <div class="border rounded-lg p-3 bg-gray-50">
          <div class="text-gray-500 text-sm">수량</div>
          <div class="font-semibold text-gray-800">{{ lot.qty }}개</div>
        </div>
        <div class="border rounded-lg p-3 bg-gray-50">
          <div class="text-gray-500 text-sm">공정번호</div>
          <div class="font-semibold text-gray-800">{{ lot.processNo }}</div>
        </div>
      </div>

      <!-- 버튼 -->
      <div class="flex justify-end gap-2">
        <button class="btn bg-white border text-sm px-3 py-1 hover:bg-gray-100 transition" @click="openModal(lot)">🔍 추적</button>
        <button class="btn bg-red-500 text-white border text-sm px-3 py-1 hover:bg-red-600 transition" @click="deleteLot(lot.lotno)">LOT 삭제</button>
      </div>
    </div>

    <!-- LOT 추적 모달 -->
    <div v-if="showModal" class="fixed inset-0 bg-black/50 flex justify-center items-start pt-20 z-50">
      <div class="bg-white rounded-lg shadow-lg w-[600px] max-h-[80vh] overflow-y-auto p-4">
        <div class="flex justify-between items-center mb-3">
          <h5 class="font-bold text-lg">{{ selectedLot.lotno }} 세부 추적</h5>
          <button @click="closeModal" class="text-gray-500 hover:text-gray-800">&times;</button>
        </div>

        <!-- 그리드 테이블 -->
        <table class="w-full text-sm border border-gray-200">
          <thead class="bg-gray-100">
            <tr>
              <th class="border px-2 py-1 text-left">LOT 번호</th>
              <th class="border px-2 py-1 text-left">공정번호</th>
              <th class="border px-2 py-1 text-left">작업일자</th>
              <th class="border px-2 py-1 text-left">수량</th>
              <th class="border px-2 py-1 text-left">품번</th>
              <th class="border px-2 py-1 text-left">품명</th>
            </tr>
          </thead>
          <tbody>
            <tr>
              <td class="border px-2 py-1">{{ selectedLot.lotno }}</td>
              <td class="border px-2 py-1">{{ selectedLot.processNo }}</td>
              <td class="border px-2 py-1">{{ selectedLot.createdAt }}</td>
              <td class="border px-2 py-1">{{ selectedLot.qty }}</td>
              <td class="border px-2 py-1">{{ selectedLot.partCode }}</td>
              <td class="border px-2 py-1">{{ selectedLot.partName }}</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch, onMounted, defineExpose } from 'vue'
import axios from 'axios'

const props = defineProps({
  worksheetSkey: String
})

const lots = ref([])
const showModal = ref(false)
const selectedLot = ref({})
const loading = ref(false)

// LOT 조회
async function fetchLots(skey) {
  if (!skey) return
  try {
    loading.value = true
    const res = await axios.get(`http://localhost:8080/api/lot/lotList/${skey}`)
    lots.value = res.data
  } catch (e) {
    console.error('LOT 불러오기 실패:', e)
    lots.value = []
  } finally {
    loading.value = false
  }
}

// prop 변경 시 자동 갱신
watch(() => props.worksheetSkey, (newSkey) => {
  if (newSkey) fetchLots(newSkey)
})

// 처음 마운트
onMounted(() => {
  if (props.worksheetSkey) fetchLots(props.worksheetSkey)
})

// fetchLots를 부모에서 호출 가능하게 노출
defineExpose({ fetchLots })

// 모달 열기/닫기
function openModal(lot) {
  selectedLot.value = lot
  showModal.value = true
}
function closeModal() {
  showModal.value = false
}

// LOT 삭제
async function deleteLot(lotno) {
  if (!props.worksheetSkey) return
  try {
    await axios.delete(`http://localhost:8080/api/lot/deleteLot`, {
      params: { lotNo: lotno, worksheetSkey: props.worksheetSkey }
    })
    lots.value = lots.value.filter(l => l.lotno !== lotno)
  } catch (e) {
    console.error('LOT 삭제 실패:', e)
  }
}
</script>
