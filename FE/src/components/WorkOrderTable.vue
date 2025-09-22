<template>
  <div class="card">
    <div class="flex items-center justify-between mb-3">
      <h3 class="font-semibold text-gray-900">작업지시 목록</h3>
      <p class="text-sm text-gray-500">현재 {{ processName }} 공정의 작업지시 현황</p>
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
                {{ statusLabel(order.workStatus) }}   <!-- ✅ 글자로 출력 -->
              </span>
            </td>
            <td class="px-3 py-2 text-center">
              <span :class="[priorityClass(order.priority), 'badge']">
                {{ priorityLabel(order.priority) }}   <!-- ✅ 글자로 출력 -->
              </span>
            </td>
            <td class="px-3 py-2 text-center">{{ order.deadline }}</td>

            <td class="px-3 py-2 text-center">
              <button class="btn-primary" @click="$emit('start', order.workNo)">작업시작</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import { fetchWorkOrders } from '../api/workOrderListSearch.js'

const orders = ref([])
const route = useRoute()
const processName = ref('전체')

async function load() {
  const path = route.path.replace('/', '') // '/mixing' → 'mixing'
  processName.value = path || '전체'
  orders.value = await fetchWorkOrders(processName.value)
}

onMounted(load)

// ✅ 경로 바뀔 때마다 다시 불러오기
watch(() => route.path, load)

// onMounted(async () => {
//   // path 기반으로 공정명 가져오기
//   const path = route.path.replace('/', '') // '/mixing' → 'mixing'
//   processName.value = path || '전체'
//   orders.value = await fetchWorkOrders(processName.value)
// })

// // onMounted(load)

// // // ✅ 경로 바뀔 때마다 다시 불러오기
// // watch(() => route.path, load)

// 상태 맵핑 (0=대기, 1=진행중, 2=완료)
const statusMap = {
  0: { label: '대기', class: 'badge-yellow' },
  1: { label: '진행중', class: 'badge-green' },
  2: { label: '완료', class: 'badge-gray' },
}

// 우선순위 맵핑 (0=보통, 1=높음, 2=긴급)
const priorityMap = {
  0: { label: '보통', class: 'badge-gray' },
  1: { label: '높음', class: 'badge-red' },
  2: { label: '긴급', class: 'badge-yellow' },
}

function statusClass(code) {
  return statusMap[code]?.class || 'badge-gray'
}
function statusLabel(code) {
  return statusMap[code]?.label || '알수없음'
}

function priorityClass(code) {
  return priorityMap[code]?.class || 'badge-gray'
}
function priorityLabel(code) {
  return priorityMap[code]?.label || '알수없음'
}

</script>

<style scoped>
.badge {
  padding: 2px 6px;
  border-radius: 4px;
  font-size: 0.875rem;
  color: white;
}
.badge-green { background-color: #22c55e; }
.badge-yellow { background-color: #eab308; }
.badge-gray { background-color: #6b7280; }
.badge-red { background-color: #ef4444; }

.btn-primary {
  background-color: #3b82f6;
  color: white;
  padding: 4px 8px;
  border-radius: 4px;
  cursor: pointer;
}
.btn-primary:hover {
  background-color: #2563eb;
}
</style>
