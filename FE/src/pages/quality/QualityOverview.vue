<template>
  <div class="space-y-4">
    <!-- 상단 KPI -->
    <div class="grid grid-cols-1 md:grid-cols-5 gap-3">
      <KpiCard sub="전체 합격률" value="98.7%" caption="+0.3% 전일 대비" tone="good"/>
      <KpiCard sub="불량률" value="1.3%" caption="+0.1% 전일 대비" tone="bad"/>
      <KpiCard sub="검사 완료" value="1,847" caption="오늘 검사 수량"/>
      <KpiCard sub="재검사율" value="2.1%" caption="-0.2% 전일 대비" tone="good"/>
      <KpiCard sub="라인 경보" value="3건" caption="최근 4시간" tone="warn"/>
    </div>

    <!-- 제품별 품질 현황 -->
    <div class="bg-white border rounded-xl p-4">
      <div class="font-semibold mb-3">제품별 품질 현황</div>
      <div class="grid grid-cols-1 md:grid-cols-4 gap-3">
        <div class="stat"><div>타이어 A</div><div class="val">99.2%</div><div class="sub">TI001</div></div>
        <div class="stat"><div>타이어 B</div><div class="val">98.8%</div><div class="sub">TI002</div></div>
        <div class="stat"><div>타이어 C</div><div class="val">96.5%</div><div class="sub">TI002</div></div>
        <div class="stat off"><div>타이어 D</div><div class="val">-</div><div class="sub">생산중단</div></div>
      </div>
    </div>

    <!-- 이슈(1열, 슬림) + 차트(2열, 확장) -->
    <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
      <!-- 최근 품질 이슈: 슬림 카드 -->
      <div class="bg-white border rounded-xl p-3 md:col-span-1">
        <div class="font-semibold mb-2">최근 품질 이슈</div>
        <!-- 높이 제한 + 스크롤 -->
        <ul class="space-y-1.5 text-xs max-h-48 overflow-auto pr-1">
          <li class="issue bad">치수 불량 발생 · 라인 C · 배치 #TY-2024-1205 · 1시간 전 <button class="btn ml-2">상세</button></li>
          <li class="issue warn">재검사 요청 · 라인 A · 배치 #TY-2024-1204 · 2시간 전 <button class="btn ml-2">상세</button></li>
          <li class="issue ok">품질 검사 완료 · 라인 B · 배치 #TY-2024-1203 · 3시간 전 <button class="btn ml-2">상세</button></li>
        </ul>
      </div>

      <!-- 차트 영역: 2열 확장 -->
      <div class="md:col-span-2 grid grid-cols-1 md:grid-cols-2 gap-4">
        <div class="bg-white border rounded-xl p-4">
          <div class="font-semibold mb-3">불량 유형별 분석</div>
          <!-- 높이 크게 -->
          <div ref="barRef" class="h-[340px] md:h-[420px]"></div>
        </div>
        <div class="bg-white border rounded-xl p-4">
          <div class="font-semibold mb-3">제품별 불량 현황</div>
          <ul class="text-sm space-y-2">
            <li class="flex justify-between border rounded p-2">타이어 A <b class="text-red-600">7건</b> <span class="text-gray-500">0.8%</span></li>
            <li class="flex justify-between border rounded p-2">타이어 B <b class="text-red-600">8건</b> <span class="text-gray-500">1.2%</span></li>
            <li class="flex justify-between border rounded p-2">타이어 C <b class="text-red-600">25건</b><span class="text-gray-500">3.5%</span></li>
          </ul>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, onBeforeUnmount, ref } from 'vue'
import KpiCard from '../../components/quality/KpiCard.vue'

const barRef = ref(null)
let chart, echarts, ro

onMounted(async () => {
  // ECharts 동적 import
  echarts = (await import('echarts')).default || (await import('echarts'))
  if (!barRef.value) return

  chart = echarts.init(barRef.value)
  chart.setOption({
    tooltip: { trigger: 'axis' },
    grid: { left: 40, right: 20, top: 20, bottom: 40 }, // 여백 조정으로 플롯 공간 극대화
    xAxis: { type: 'category', data: ['치수', '외관', '강도', '기타'] },
    yAxis: { type: 'value' },
    series: [{ type: 'bar', data: [18, 12, 6, 4] }]
  })

  // 반응형 리사이즈 (ResizeObserver 권장)
  ro = new ResizeObserver(() => chart && chart.resize())
  ro.observe(barRef.value)
  window.addEventListener('resize', handleResize)
})

function handleResize() {
  if (chart) chart.resize()
}

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  if (ro && barRef.value) ro.unobserve(barRef.value)
  if (chart) {
    chart.dispose()
    chart = null
  }
})
</script>

<style scoped>
.stat { @apply border rounded-lg p-3 bg-white; }
.stat .val { @apply text-2xl font-bold; }
.stat .sub { @apply text-xs text-gray-500; }
.stat.warn { @apply border-yellow-300; }
.stat.off { @apply text-gray-400; }

/* 이슈 카드: 컴팩트 */
.issue { @apply p-2 rounded border leading-5; }
.issue.bad { @apply border-red-300 bg-red-50; }
.issue.warn { @apply border-yellow-300 bg-yellow-50; }
.issue.ok { @apply border-green-300 bg-green-50; }

.btn { @apply px-2 py-1 border rounded bg-white hover:bg-gray-50; }
</style>
