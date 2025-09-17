<!-- src/components/inspection/DefectCard.vue -->
<template>
  <div class="card">
    <div class="text-sm text-gray-500">{{ title }}</div>
    <div class="text-2xl font-bold text-red-600 mt-1">{{ today }}</div>
    <div class="text-xs text-gray-500">전체의 {{ rate }}</div>
    <div class="w-full bg-gray-100 h-2 rounded mt-2">
      <div class="h-2 rounded bg-gray-800" :style="{ width: rateNum + '%' }"></div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  title: String,
  today: String,
  rate: String,
})

const rateNum = computed(() =>
  props.rate ? parseInt(props.rate.replace('%', '')) : 0
)
</script>
