<template>
  <div class="space-y-4">
    <!-- 상단 KPI -->
    <div class="grid grid-cols-1 md:grid-cols-4 gap-3">
      <KpiCard sub="비전 시스템 상태" value="정상" caption="4대 모두 가동중" tone="good"/>
      <KpiCard sub="자동 검출률" value="99.5%" caption="+0.2% 전일 대비" tone="good"/>
      <KpiCard sub="오늘 검사량" value="2,847" caption="개 제품 검사 완료"/>
      <KpiCard sub="평균 검사 시간" value="1.2초" caption="제품당 평균"/>
    </div>

    <!-- 카메라 카드 -->
    <div class="grid grid-cols-1 md:grid-cols-4 gap-3">
      <CameraCard name="CAM-01" line="라인 A" status="정상"  acc="99.8%" defects="3개"/>
      <CameraCard name="CAM-02" line="라인 B" status="정상"  acc="99.9%" defects="1개"/>
      <CameraCard name="CAM-03" line="라인 C" status="주의"  acc="98.5%" defects="8개"/>
      <CameraCard name="CAM-04" line="라인 D" status="정비중" acc="-"     defects="0개"/>
    </div>

    <!-- 최근 비전 검사 결과 -->
    <div class="bg-white border rounded-xl p-4">
      <div class="font-semibold mb-3">최근 비전 검사 결과</div>
      <table class="w-full text-sm">
        <thead class="text-gray-500"><tr>
          <th class="text-left p-2">유형</th><th class="text-left p-2">라인</th><th class="text-left p-2">시각</th><th class="text-left p-2">조치</th><th class="text-left p-2">작업</th>
        </tr></thead>
        <tbody>
          <tr class="border-t"><td class="p-2">표면 스크래치</td><td class="p-2">CAM-01 · A</td><td class="p-2">15:42:33</td><td class="p-2">자동 분류</td><td class="p-2"><button class="btn">상세보기</button></td></tr>
          <tr class="border-t"><td class="p-2">치수 이상</td><td class="p-2">CAM-03 · C</td><td class="p-2">15:41:18</td><td class="p-2">라인 정지</td><td class="p-2"><button class="btn">상세보기</button></td></tr>
        </tbody>
      </table>
    </div>
  </div>
</template>
<script setup>
import KpiCard from '../../components/quality/KpiCard.vue'
import CameraCard from '../../components/quality/CameraCard.vue'
</script>
<style scoped>
.btn { @apply px-2 py-1 border rounded bg-white hover:bg-gray-50; }
</style>
