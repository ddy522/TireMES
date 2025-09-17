<template>
  <div class="space-y-4">
    <h2 class="text-lg font-semibold text-gray-900">불량 분석</h2>

    <div class="grid grid-cols-1 lg:grid-cols-3 gap-4">
      <DefectCard title="표면 결함" today="12 건" rate="45%" />
      <DefectCard title="치수 불량" today="8 건" rate="30%" />
      <DefectCard title="패턴 불량" today="4 건" rate="15%" />
      <DefectCard title="기타" today="3 건" rate="10%" />
    </div>
  </div>
</template>

<script setup>
import DefectCard from './DefectCard.vue'
</script>
