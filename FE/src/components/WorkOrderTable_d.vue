<template>
    <div class="card">
      <div class="flex items-center justify-between mb-3">
        <div>
          <h3 class="font-semibold text-gray-900">작업지시 목록</h3>
          <p class="text-sm text-gray-500">현재 {{ processName }} 공정의 작업지시 현황</p>
        </div>
      </div>
  
      <!-- 검색 조건 -->
      <div class="flex items-center gap-3 mb-3">
        <CommonSelect type="part" label="품번" v-model="filters.partCode" width="220px" />
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
              <th class="px-3 py-2 text-left">작업지시번호</th>
              <th class="px-3 py-2 text-left">제품명</th>
              <th class="px-3 py-2 text-right">수량</th>
              <th class="px-3 py-2 text-center">상태</th>
              <th class="px-3 py-2 text-center">우선순위</th>
              <th class="px-3 py-2 text-center">납기일</th>
              <th class="px-3 py-2 text-center">작업</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="order in orders" :key="order.workNo" class="border-t">
              <td class="px-3 py-2">{{ order.workNo }}</td>
              <td class="px-3 py-2">{{ order.partName }}</td>
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
              <td class="px-3 py-2 text-center">
                <button
                  class="btn-primary"
                  :disabled="loadingShipment"
                  @click="confirmShipment(order)"
                >
                  출하처리
                </button>
              </td>
            </tr>
            <tr v-if="!loading && orders.length === 0">
              <td colspan="7" class="px-3 py-6 text-center text-gray-500">
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
  import axios from 'axios'
  import CommonSelect from './CommonSelect.vue'
  import { fetchWorkOrders, searchWorkOrders } from '../api/workOrderListSearch.js'
  
  const loading = ref(false)
  const loadingShipment = ref(false)
  const error = ref(null)
  const orders = ref([])
  const processName = ref('전체')
  
  const route = useRoute()
  const filters = ref({
    processNameEng: '',
    partCode: '',
    workStatus: '1',
    workNo: ''
  })
  
  // 상태/우선순위 표시
  const statusMap = { 0: { label: '대기', class: 'badge-yellow' }, 1: { label: '진행중', class: 'badge-green' }, 2: { label: '완료', class: 'badge-gray' } }
  const priorityMap = { 0: { label: '보통', class: 'badge-gray' }, 1: { label: '높음', class: 'badge-red' }, 2: { label: '긴급', class: 'badge-yellow' } }
  const toNum = v => (typeof v === 'string' ? Number(v) : v)
  const statusClass = code => statusMap[toNum(code)]?.class ?? 'badge-gray'
  const statusLabel = code => statusMap[toNum(code)]?.label ?? '알수없음'
  const priorityClass = code => priorityMap[toNum(code)]?.class ?? 'badge-gray'
  const priorityLabel = code => priorityMap[toNum(code)]?.label ?? '알수없음'
  
  // 초기 로딩
  function pickData(res) {
    if (!res) return []
    if (Array.isArray(res)) return res
    if (Array.isArray(res.data)) return res.data
    return res.data?.content ?? res.data ?? []
  }
  
  function getEngFromRoute() {
    const eng = (route.path || '').replace('/', '').toLowerCase()
    return eng || ''
  }
  
  async function loadInitial() {
    const eng = getEngFromRoute()
    processName.value = eng || '전체'
    filters.value.processNameEng = eng
    loading.value = true
    try {
      if (!filters.value.workStatus) filters.value.workStatus = '1'
      const payload = {
        processNameEng: eng || '',
        workStatus: filters.value.workStatus,
        partCode: filters.value.partCode || undefined,
        workNo: filters.value.workNo || undefined
      }
      Object.keys(payload).forEach(k => payload[k] === undefined && delete payload[k])
      const res = await searchWorkOrders(payload)
      orders.value = pickData(res)
    } catch (e) {
      console.error(e)
      orders.value = []
    } finally {
      loading.value = false
    }
  }
  
  async function onSearch() {
    loading.value = true
    try {
      const res = await searchWorkOrders({ ...filters.value })
      orders.value = pickData(res)
    } catch (e) {
      console.error(e)
      orders.value = []
    } finally {
      loading.value = false
    }
  }
  
  function onReset() {
    filters.value = { ...filters.value, partCode: '', workStatus: '1', workNo: '' }
    onSearch()
  }
  
  onMounted(loadInitial)
  watch(() => route.path, loadInitial)
  
 // ===== 출하처리 로직 =====
    async function confirmShipment(order) {
    // doneQty, remark를 order에서 가져오거나 별도로 입력받도록 처리
    const doneQty = order.doneQty ?? 0     // 없으면 0
    const remark  = order.remark ?? ''     // 없으면 빈 문자열

    if (!confirm(`작업지시 [${order.workNo}]를 출하처리 하시겠습니까?`)) return
    loadingShipment.value = true
    try {
        await axios.post('/api/worksheet/insertProduction', {
        worksheetSkey: order.worksheetSkey,
        doneQty: order.qty,
        remark: ''
        })
        alert(`작업지시 [${order.workNo}]가 출하처리 되었습니다.`)
        await onSearch() // 처리 후 목록 갱신
    } catch (e) {
        console.error(e)
        alert('출하처리 중 오류가 발생했습니다.')
    } finally {
        loadingShipment.value = false
    }
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
  