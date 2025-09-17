<template>
  <div class="card">
    <div class="flex items-start justify-between mb-2">
      <div>
        <div class="font-medium text-gray-900">{{ title }}</div>
        <div class="text-xs text-gray-500">{{ code }}</div>
      </div>
      <span class="badge badge-gray">{{ status }}</span>
    </div>

    <div class="text-xs text-gray-500 mb-1">검출 정확도</div>
    <div class="w-full bg-gray-100 h-2 rounded">
      <div class="h-2 rounded bg-gray-800" :style="{ width: accuracy + '%' }"></div>
    </div>
    <div class="text-right text-sm mt-1">{{ accuracy.toFixed(1) }}%</div>

    <div class="mt-3">
      <button class="btn bg-white border w-full">카메라 설정</button>
    </div>
  </div>
</template>

<script setup>
defineProps({
  title: String,
  code: String,
  accuracy: Number,
  status: String,
})
</script>
