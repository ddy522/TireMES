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

    <!-- 라인별 현황 -->
    <div class="bg-white border rounded-xl p-4">
      <div class="font-semibold mb-3">라인별 품질 현황</div>
      <div class="grid grid-cols-1 md:grid-cols-4 gap-3">
        <div class="stat"><div>라인 A</div><div class="val">99.2%</div><div class="sub">승용차 타이어</div></div>
        <div class="stat"><div>라인 B</div><div class="val">98.8%</div><div class="sub">트럭 타이어</div></div>
        <div class="stat warn"><div>라인 C</div><div class="val">96.5%</div><div class="sub">특수 타이어</div></div>
        <div class="stat off"><div>라인 D</div><div class="val">-</div><div class="sub">정비 중</div></div>
      </div>
    </div>

    <!-- 최근 품질 이슈 -->
    <div class="bg-white border rounded-xl p-4">
      <div class="font-semibold mb-3">최근 품질 이슈</div>
      <ul class="space-y-2 text-sm">
        <li class="issue bad">치수 불량 발생 · 라인 C · 배치 #TY-2024-1205 · 1시간 전 <button class="btn ml-2">상세</button></li>
        <li class="issue warn">재검사 요청 · 라인 A · 배치 #TY-2024-1204 · 2시간 전 <button class="btn ml-2">상세</button></li>
        <li class="issue ok">품질 검사 완료 · 라인 B · 배치 #TY-2024-1203 · 3시간 전 <button class="btn ml-2">상세</button></li>
      </ul>
    </div>
  </div>
</template>
<script setup>
import KpiCard from '../../components/quality/KpiCard.vue'
</script>
<style scoped>
.stat { @apply border rounded-lg p-3 bg-white; }
.stat .val { @apply text-2xl font-bold; }
.stat .sub { @apply text-xs text-gray-500; }
.stat.warn { @apply border-yellow-300; }
.stat.off { @apply text-gray-400; }
.issue { @apply p-3 rounded border; }
.issue.bad { @apply border-red-300 bg-red-50; }
.issue.warn { @apply border-yellow-300 bg-yellow-50; }
.issue.ok { @apply border-green-300 bg-green-50; }
.btn { @apply px-2 py-1 border rounded bg-white hover:bg-gray-50; }
</style>
