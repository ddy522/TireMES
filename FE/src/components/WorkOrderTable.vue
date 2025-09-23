<template>
  <div class="card">
    <!-- 상단 타이틀/버튼 -->
    <div class="flex items-center justify-between mb-3">
      <div>
        <h3 class="font-semibold text-gray-900">작업지시 목록</h3>
        <p class="text-sm text-gray-500">현재 {{ processName }} 공정의 작업지시 현황</p>
      </div>

      <!-- inspection 전용: 선택 작업 시작 -->
      <button
        v-if="isInspection"
        class="btn-primary flex items-center gap-2"
        :disabled="selectedCount === 0 || loading"
        @click="emitBulkStart"
        title="선택된 모든 작업지시를 시작"
      >
        선택 작업 시작
        <!-- <span class="badge badge-yellow">{{ selectedCount }}</span> -->
      </button>

      <!-- delivery 전용: 출고(전체/조건부) -->
      <button
        v-if="isDelivery"
        class="btn-primary flex items-center gap-2"
        :disabled="!canShip || loading"
        @click="$emit('ship')"
        title="진행중 10건 이상일 때만 출고 가능"
      >
        출고
        <span class="badge" :class="canShip ? 'badge-green' : 'badge-gray'">
          {{ runningCount }} / 10
        </span>
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

    <!-- 테이블 -->
    <div class="overflow-x-auto">
      <table class="min-w-full text-sm">
        <thead>
          <tr class="bg-gray-100 text-gray-700">
            <!-- ✅ inspection 전용: 전체 선택 체크 -->
            <th v-if="isInspection" class="px-3 py-2 text-center w-10">
              <input
                type="checkbox"
                :checked="allChecked"
                @change="toggleAll($event)"
                aria-label="전체 선택"
              />
            </th>
            <th class="px-3 py-2 text-left">작업지시번호</th>
            <th class="px-3 py-2 text-left">제품명</th>
            <th class="px-3 py-2 text-right">수량</th>
            <th class="px-3 py-2 text-center">상태</th>
            <th class="px-3 py-2 text-center">우선순위</th>
            <th class="px-3 py-2 text-center">납기일</th>
            <!-- ✅ 액션 컬럼 헤더: inspection 제외 -->
            <th v-if="!isInspection" class="px-3 py-2 text-center">
              {{ isDelivery ? '출고' : '작업' }}
            </th>
          </tr>
        </thead>

        <tbody>
          <tr v-for="order in orders" :key="order.workNo" class="border-t">
            <!-- ✅ inspection 전용: 행 선택 체크 -->
            <td v-if="isInspection" class="px-3 py-2 text-center">
              <input
                type="checkbox"
                :checked="selected.has(order.workNo)"
                @change="toggleRow(order.workNo, $event)"
                :aria-label="`Select ${order.workNo}`"
              />
            </td>

            <td class="px-3 py-2">{{ order.workNo }}</td>
            <td class="px-3 py-2">{{ order.partName }}</td>
            <td class="px-3 py-2 text-right">{{ order.qty }}</td>
            <td class="px-3 py-2 text-center">
              <span :class="['badge', statusClass(order.workStatus)]">
                {{ statusLabel(order.workStatus) }}
              </span>
            </td>
            <td class="px-3 py-2 text-center">
              <span :class="['badge', priorityClass(order.priority)]">
                {{ priorityLabel(order.priority) }}
              </span>
            </td>
            <td class="px-3 py-2 text-center">{{ order.deadline }}</td>

            <!-- ✅ inspection이 아닐 때만 액션 버튼 노출 -->
            <td v-if="!isInspection" class="px-3 py-2 text-center">
              <button
                v-if="!isDelivery"
                class="btn-primary"
                @click="$emit('start', order.workNo)"
              >
                작업시작
              </button>

              <button
                v-else
                class="btn-primary"
                :disabled="!canShip || loading"
                @click="$emit('ship', order.workNo)"
              >
                출고
              </button>
            </td>
          </tr>

          <tr v-if="!loading && orders.length === 0">
            <td :colspan="isInspection ? 7 : 8"
                class="px-3 py-6 text-center text-gray-500">
              데이터가 없습니다.
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

const emit = defineEmits(['start', 'bulk-start', 'ship'])

/* ===== 상태 ===== */
const loading = ref(false)
const error   = ref(null)
const orders  = ref([])

const route = useRoute()
const processName = ref('전체')

/* ===== 응답 정규화 ===== */
function pickData(res) {
  if (!res) return []
  if (Array.isArray(res)) return res
  if (Array.isArray(res.data)) return res.data
  return res.data?.content ?? res.data ?? []
}

/* ===== 검색 상태 ===== */
const filters = ref({
  processNameEng: '',
  partCode: '',
  workStatus: '1',   // 기본: 진행중
  workNo: ''
})
function getEngFromRoute() {
  const eng = (route.path || '').replace('/', '').toLowerCase()
  return eng || ''
}

const isInspection = computed(() => filters.value.processNameEng === 'inspection')
const isDelivery   = computed(() => filters.value.processNameEng === 'delivery')

/* 진행중(1) 카운트 & 출고 가능 */
const runningCount = computed(() =>
  orders.value.reduce((acc, o) => acc + (Number(o.workStatus) === 1 ? 1 : 0), 0)
)
const canShip = computed(() => runningCount.value >= 10)

/* ===== 초기 로드/검색 ===== */
async function loadInitial() {
  const eng = getEngFromRoute()
  processName.value = eng || '전체'
  filters.value.processNameEng = eng

  loading.value = true
  error.value = null
  try {
    if (!filters.value.workStatus) filters.value.workStatus = '1'
    const payload = {
      processNameEng: eng || '',
      workStatus: filters.value.workStatus,
      partCode: filters.value.partCode || undefined,
      workNo: filters.value.workNo || undefined,
    }
    Object.keys(payload).forEach(k => payload[k] === undefined && delete payload[k])
    const res = await searchWorkOrders(payload)
    orders.value = pickData(res)
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
    selected.value.clear()
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

/* ===== 표시 유틸 ===== */
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
const toNum = v => (typeof v === 'string' ? Number(v) : v)
const statusClass   = code => statusMap[toNum(code)]?.class ?? 'badge-gray'
const statusLabel   = code => statusMap[toNum(code)]?.label ?? '알수없음'
const priorityClass = code => priorityMap[toNum(code)]?.class ?? 'badge-gray'
const priorityLabel = code => priorityMap[toNum(code)]?.label ?? '알수없음'

/* ===== ✅ 선택/일괄 시작 ===== */
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
    runCount: selectedCount.value   
  })
  // Array.from(selected.value))
}

defineExpose({
  reload: loadInitial
})
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
