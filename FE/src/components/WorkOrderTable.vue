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
            <th class="px-3 py-2 text-center">시작시간</th>
            <th class="px-3 py-2 text-center">종료시간</th>
            <th class="px-3 py-2 text-center">작업</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="order in orders" :key="order.workNo" class="border-t">
            <td class="px-3 py-2">{{ order.workNo }}</td>
            <td class="px-3 py-2">{{ order.partName }}</td>
            <td class="px-3 py-2 text-right">{{ order.qty }}</td>
            <td class="px-3 py-2 text-center">
              <span :class="statusClass(order.workStatus)" class="badge">
                {{ order.workStatus }}
              </span>
            </td>
            <td class="px-3 py-2 text-center">
              <span :class="priorityClass(order.priority)" class="badge">
                {{ order.priority }}
              </span>
            </td>
            <td class="px-3 py-2 text-center">{{ order.startTime || '-' }}</td>
            <td class="px-3 py-2 text-center">{{ order.endTime || '-' }}</td>
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
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { fetchWorkOrders } from '../api/workOrderListSearch.js'

const orders = ref([])
const route = useRoute()
const processName = ref('전체')

onMounted(async () => {
  // path 기반으로 공정명 가져오기
  const path = route.path.replace('/', '') // '/mixing' → 'mixing'
  processName.value = path || '전체'
  orders.value = await fetchWorkOrders(processName.value)
})

function statusClass(s) {
  if (s === '진행중') return 'badge-green'
  if (s === '대기') return 'badge-yellow'
  if (s === '완료') return 'badge-gray'
  return 'badge-gray'
}

function priorityClass(p) {
  if (p === '높음') return 'badge-red'
  if (p === '보통') return 'badge-gray'
  return 'badge-gray'
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
