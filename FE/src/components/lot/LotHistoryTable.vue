<template>
  <div class="card overflow-x-auto">
    <h3 class="section-title">공정별 이력 LOT 번호</h3>
    <p class="text-sm text-gray-600 mb-3">
      해당 로트가 지금까지 거쳐온 모든 공정에서 <b>새로 부여받았던 LOT 번호</b>를 시간순으로 표시합니다.
    </p>

    <table class="min-w-full text-sm">
      <thead>
        <tr class="bg-gray-100 text-gray-700">
          <th class="px-3 py-2 text-left">공정</th>
          <th class="px-3 py-2 text-left">LOT 번호</th>
          <th class="px-3 py-2 text-left">품번</th>
          <th class="px-3 py-2 text-left">품명</th>
          <th class="px-3 py-2 text-right">수량</th>
          <th class="px-3 py-2 text-left">등록시각</th>
          
        </tr>
      </thead>
      <tbody>
        <tr v-for="r in rows" :key="r.order" class="border-t">
          <td class="px-3 py-2 text-left">{{ r.processName }}</td>
          <td class="px-3 py-2 text-left font-medium">{{ r.lotno }}</td>
          <td class="px-3 py-2 text-left">{{ r.partCode }}</td>
          <td class="px-3 py-2 text-left">{{ r.partName }}</td>
          <td class="px-3 py-2 text-right">{{ r.qty }}</td>
          <td class="px-3 py-2 text-left">{{ r.createdAt }}</td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script setup>
const props = defineProps({
  rows: { type: Array, default: () => [] }
})

function badge(s) {
  if (s === '완료') return 'badge-green'
  if (s === '진행중') return 'badge-blue'
  if (s === '대기') return 'badge-gray'
  return 'badge-gray'
}
</script>
