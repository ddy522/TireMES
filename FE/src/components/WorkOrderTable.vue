<template>
  <div class="card">
    <div class="flex items-center justify-between mb-3">
      <div>
        <h3 class="font-semibold text-gray-900">작업지시 목록</h3>
        <p class="text-sm text-gray-500">현재 {{ processName }} 공정의 작업지시 현황</p>
      </div>

      <!-- ✅ inspection 전용: 선택 작업 시작 버튼 -->
      <button
        v-if="isInspection"
        class="btn-primary flex items-center gap-2"
        :disabled="selectedCount === 0 || loading"
        @click="emitBulkStart"
        title="선택된 모든 작업지시를 시작"
      >
        선택 작업 시작
        <!-- <span class="badge badge-yellow" style="background:#eab308; color:#fff">
          {{ selectedCount }}
        </span> -->
      </button>
    </div>

    <!-- 검색 조건 -->
    <div class="flex items-center gap-3 mb-3">
      <CommonSelect type="part"   label="품번" v-model="filters.partCode"  width="220px" />
      <CommonSelect type="status" label="상태" v-model="filters.workStatus" width="180px" />

      <input
        v-model="filters.workNo"
        placeholder="작업지시 번호"
        class="border rounded px-3 py-2 text-sm flex-1"
        @keyup.enter="onSearch"
      />
      <button class="btn-primary" @click="onSearch">조회</button>
      <button class="border rounded px-3 py-2 text-sm" @click="onReset">초기화</button>
    </div>
    
    <div class="overflow-x-auto">
      <table class="min-w-full text-sm">
        <thead>
          <tr class="bg-gray-100 text-gray-700">
            <!-- ✅ inspection 전용: 전체 선택 체크박스 -->
            <th v-if="isInspection" class="px-3 py-2 text-center w-10">
              <input type="checkbox" :checked="allChecked" @change="toggleAll($event)" />
            </th>
            <th class="px-3 py-2 text-left">작업지시번호</th>
            <th class="px-3 py-2 text-left">제품명</th>
            <th v-if="isCuring" class="px-3 py-2 text-center">금형틀</th>
            <th class="px-3 py-2 text-right">수량</th>
            <th class="px-3 py-2 text-center">상태</th>
            <th class="px-3 py-2 text-center">우선순위</th>
            <th class="px-3 py-2 text-center">납기일</th>
            <th v-if="!isInspection" class="px-3 py-2 text-center">작업</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="order in orders" :key="order.workNo" class="border-t">
            <!-- ✅ inspection 전용: 행 선택 체크박스 -->
            <td v-if="isInspection" class="px-3 py-2 text-center">
              <input
                type="checkbox"
                :checked="selected.has(order.workNo)"
                @change="toggleRow(order.workNo, $event)"
              />
            </td>

            <td class="px-3 py-2">{{ order.workNo }}</td>
            <td class="px-3 py-2">{{ order.partName }}</td>
            <td v-if="isCuring" class="px-3 py-2 text-center">{{ order.std }}</td>
            <td class="px-3 py-2 text-right">{{ order.qty }}</td>
            <td class="px-3 py-2 text-center">
              <span :class="[statusClass(order.workStatus), 'badge']">
                {{ statusLabel(order.workStatus) }}   
              </span>
            </td>
            <td class="px-3 py-2 text-center">
              <span :class="[priorityClass(order.priority), 'badge']">
                {{ priorityLabel(order.priority) }}  
              </span>
            </td>
            <td class="px-3 py-2 text-center">{{ order.deadline }}</td>
            <td v-if="!isInspection" class="px-3 py-2 text-center">
               <button class="btn-primary" @click="$emit('start', order.workNo, order.worksheetSkey)">작업시작</button>
            </td>
          </tr>
          <tr v-if="!loading && orders.length === 0">
             <td :colspan="isInspection ? (isCuring ? 9 : 8) : (isCuring ? 8 : 7)"
                class="px-3 py-6 text-center text-gray-500">
              데이터가 없습니다
              </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch, computed } from 'vue'
import { useRoute } from 'vue-router'
import { fetchWorkOrders, searchWorkOrders } from '../api/workOrderListSearch.js'
import CommonSelect from './CommonSelect.vue'

const emit = defineEmits(['start', 'bulk-start'])   // ✅ bulk-start 추가

// 상태
const loading = ref(false)
const error   = ref(null)
const orders  = ref([])

const route = useRoute()
const processName = ref('전체')

// API 응답 정규화
function pickData(res) {
  if (!res) return []
  if (Array.isArray(res)) return res
  if (Array.isArray(res.data)) return res.data
  return res.data?.content ?? res.data ?? []
}

// 검색 상태
const filters = ref({
  processNameEng: '',
  partCode: '',
  workStatus: '1',
  workNo: ''
})

function getEngFromRoute() {
  const eng = (route.path || '').replace('/', '').toLowerCase()
  return eng || ''
}

const isInspection = computed(() => filters.value.processNameEng === 'inspection') // ✅
const isCuring = computed(() => filters.value.processNameEng === 'curing')

async function loadInitial() {
  const eng = getEngFromRoute()
  processName.value = eng || '전체'
  filters.value.processNameEng = eng

  loading.value = true
  error.value = null
  try {
// 기본값 보장 (없으면 '1' 주입)
   if (!filters.value.workStatus) filters.value.workStatus = '1'
   // ✅ 항상 검색 API로, 진행중만 포함해서 호출
   const payload = {
     processNameEng: eng || '',
     workStatus: filters.value.workStatus,     // '1'
     partCode: filters.value.partCode || undefined,
     workNo: filters.value.workNo || undefined,
   }
   Object.keys(payload).forEach(k => payload[k] === undefined && delete payload[k])
   const res = await searchWorkOrders(payload)
   orders.value = pickData(res)
    // if (eng) {
    //   const res = await searchWorkOrders({ processNameEng: eng })
    //   orders.value = pickData(res)
    // } else {
    //   const res = await fetchWorkOrders('')
    //   orders.value = pickData(res)
    // }
    // ✅ 데이터 로드 시 선택 초기화
    selected.value.clear()
  } catch (e) {
    console.error(e)
    error.value = e
    orders.value = []
  } finally {
    loading.value = false
  }
}

async function onSearch() {
  loading.value = true
  error.value = null
  try {
    const res = await searchWorkOrders({ ...filters.value })
    orders.value = pickData(res)
    selected.value.clear() // ✅ 검색 시에도 초기화
  } catch (e) {
    console.error(e)
    error.value = e
    orders.value = []
  } finally {
    loading.value = false
  }
}

function onReset() {
  filters.value = {
    ...filters.value,
    partCode: '',
    workStatus: '1',
    workNo: ''
  }
  onSearch()
}

onMounted(loadInitial)
watch(() => route.path, loadInitial)

// ====== 상태/우선순위 표시 유틸 ======
const statusMap = {
  0: { label: '대기',   class: 'badge-yellow' },
  1: { label: '진행중', class: 'badge-green'  },
  2: { label: '완료',   class: 'badge-gray'   },
}
const priorityMap = {
  0: { label: '보통', class: 'badge-gray'   },
  1: { label: '높음', class: 'badge-red'    },
  2: { label: '긴급', class: 'badge-yellow' },
}
const toNum = (v) => (typeof v === 'string' ? Number(v) : v)
const statusClass   = (code) => statusMap[toNum(code)]?.class ?? 'badge-gray'
const statusLabel   = (code) => statusMap[toNum(code)]?.label ?? '알수없음'
const priorityClass = (code) => priorityMap[toNum(code)]?.class ?? 'badge-gray'
const priorityLabel = (code) => priorityMap[toNum(code)]?.label ?? '알수없음'

// ====== ✅ 선택/일괄 시작 로직 ======
const selected = ref(new Set()) // workNo 집합

const selectedCount = computed(() => selected.value.size)
const allChecked = computed(() => {
  if (!orders.value.length) return false
  return selected.value.size === orders.value.length
})

function toggleRow(workNo, ev) {
  if (ev.target.checked) selected.value.add(workNo)
  else selected.value.delete(workNo)
}

function toggleAll(ev) {
  selected.value.clear()
  if (ev.target.checked) {
    for (const o of orders.value) selected.value.add(o.workNo)
  }
}

function emitBulkStart() {
  if (selected.value.size === 0) return
  emit('bulk-start', {
    workNos: Array.from(selected.value),
    count: selected.value.size   // ✅ 개수도 함께 전달
  })
}
</script>

<style scoped>
.badge { padding: 2px 6px; border-radius: 4px; font-size: 0.875rem; color: white; }
.badge-green  { background-color: #22c55e; }
.badge-yellow { background-color: #eab308; }
.badge-gray   { background-color: #6b7280; }
.badge-red    { background-color: #ef4444; }

.btn-primary { background-color: #3b82f6; color: white; padding: 4px 8px; border-radius: 4px; cursor: pointer; }
.btn-primary:hover { background-color: #2563eb; }
.btn-primary:disabled { opacity: .6; cursor: not-allowed; }
</style>
