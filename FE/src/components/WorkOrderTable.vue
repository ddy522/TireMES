<template>
  <div class="card">
    <div class="flex items-center justify-between mb-3">
      <h3 class="font-semibold text-gray-900">작업지시 목록</h3>
      <p class="text-sm text-gray-500">현재 믹싱 공정의 작업지시 현황</p>
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
          <tr v-for="order in orders" :key="order.id" class="border-t">
            <td class="px-3 py-2">{{ order.id }}</td>
            <td class="px-3 py-2">{{ order.product }}</td>
            <td class="px-3 py-2 text-right">{{ order.qty }}</td>
            <td class="px-3 py-2 text-center">
              <span :class="statusClass(order.status)" class="badge">
                {{ order.status }}
              </span>
            </td>
            <td class="px-3 py-2 text-center">
              <span :class="priorityClass(order.priority)" class="badge">
                {{ order.priority }}
              </span>
            </td>
            <td class="px-3 py-2 text-center">{{ order.start }}</td>
            <td class="px-3 py-2 text-center">{{ order.end }}</td>
            <td class="px-3 py-2 text-center">
              <button class="btn-primary" @click="$emit('start', order.id)">작업시작</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup>
const props = defineProps({ orders: { type: Array, default: () => [] } })

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
