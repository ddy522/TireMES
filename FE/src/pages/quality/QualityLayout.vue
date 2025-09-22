<template>
  <div class="space-y-4">
    <div class="flex items-center justify-between">
      <h1 class="text-xl font-bold text-gray-900">품질 관리</h1>
      <div class="text-sm text-gray-500">/quality</div>
    </div>

    <!-- 탭 -->
    <div class="flex gap-2">
      <RouterLink :to="{ name: 'QualityOverview' }" class="tab" :class="isActive('')">품질 현황</RouterLink>
      <RouterLink :to="{ name: 'VisionInspection' }" class="tab" :class="isActive('vision')">비전 검사</RouterLink>
      <RouterLink :to="{ name: 'InspectionAdmin' }" class="tab" :class="isActive('inspection')">검사 관리</RouterLink>
      <RouterLink :to="{ name: 'DefectAnalysis' }" class="tab" :class="isActive('defects')">불량 분석</RouterLink>
      <RouterLink :to="{ name: 'QualityReports' }" class="tab" :class="isActive('reports')">품질 보고서</RouterLink>
      <RouterLink :to="{ name: 'QualityStandards' }" class="tab" :class="isActive('standards')">품질 기준</RouterLink>
    </div>

    <RouterView />
  </div>
</template>

<script setup>
import { useRoute } from 'vue-router'
const route = useRoute()
function isActive(seg) {
  // /quality  = '' , /quality/vision = 'vision' ...
  const path = route.path.replace(/^\/quality\/?/, '')
  return path === seg ? 'tab-active' : ''
}
</script>

<style scoped>
.tab { @apply px-4 py-2 rounded border text-sm bg-white hover:bg-gray-50; }
.tab-active { @apply border-blue-500 text-blue-600; }
</style>
