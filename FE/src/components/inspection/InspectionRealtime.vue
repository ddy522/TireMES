<template>
  <div class="space-y-4">
    <h2 class="text-lg font-semibold text-gray-900">실시간 모니터링</h2>

    <div class="card">
      <div class="flex items-center justify-between mb-2">
        <div class="font-medium">검사 스테이션 1호기</div>
        <span class="badge badge-yellow">대기중</span>
      </div>

      <div class="grid grid-cols-2 md:grid-cols-4 gap-4 text-sm">
        <Metric label="검사 완료" value="0개" sub="오늘 검사량" />
        <Metric label="합격률" value="0%" />
        <Metric label="검사 상태" value="정상" />
        <Metric label="품질 달성률" value="0%" />
      </div>

      <div class="mt-4 grid grid-cols-3 gap-2">
        <button class="btn bg-white border">검사 시작</button>
        <button class="btn bg-white border">일시정지</button>
        <button class="btn bg-white border">검사 완료</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { defineComponent } from 'vue'

const Metric = defineComponent({
  props: { label: String, value: String, sub: String },
  template: `
    <div>
      <div class="text-gray-500">{{ label }}</div>
      <div class="text-xl font-semibold">{{ value }}</div>
      <div v-if="sub" class="text-xs text-gray-500">{{ sub }}</div>
      <div class="w-full bg-gray-100 h-2 rounded mt-2">
        <div class="h-2 rounded bg-gray-800" style="width: 0%"></div>
      </div>
    </div>
  `
})
</script>
