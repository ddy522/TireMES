<template>
  <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
    <div class="bg-white border rounded-xl p-4">
      <div class="font-semibold mb-3">불량 유형별 분석</div>
      <div ref="barRef" style="height:260px;"></div>
    </div>
    <div class="bg-white border rounded-xl p-4">
      <div class="font-semibold mb-3">라인별 불량 현황</div>
      <ul class="text-sm space-y-2">
        <li class="flex justify-between border rounded p-2">라인 A <b class="text-red-600">7건</b> <span class="text-gray-500">0.8%</span></li>
        <li class="flex justify-between border rounded p-2">라인 B <b class="text-red-600">8건</b> <span class="text-gray-500">1.2%</span></li>
        <li class="flex justify-between border rounded p-2">라인 C <b class="text-red-600">25건</b><span class="text-gray-500">3.5%</span></li>
      </ul>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'

const barRef = ref(null)

onMounted(async () => {
  // ✅ 여기서 동적으로 불러오면 setup은 동기 유지
  const echarts = (await import('echarts')).default || (await import('echarts'))
  if (!barRef.value) return
  const chart = echarts.init(barRef.value)
  chart.setOption({
    tooltip: { trigger: 'axis' },
    xAxis: { type: 'category', data: ['치수', '외관', '강도', '기타'] },
    yAxis: { type: 'value' },
    series: [{ type: 'bar', data: [18, 12, 6, 4] }]
  })
})
</script>
