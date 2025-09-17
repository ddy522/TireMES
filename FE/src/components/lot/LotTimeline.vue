<template>
  <div class="card">
    <h3 class="section-title">공정 진행 현황</h3>
    <p class="text-sm text-gray-600 mb-3">각 공정별 상세 진행 상황</p>

    <div class="space-y-2">
      <div
        v-for="s in steps"
        :key="s.key"
        class="flex items-center justify-between border rounded-md px-3 py-2 bg-white"
      >
        <div class="flex items-center gap-3">
          <StatusDot :status="s.status" />
          <div>
            <div class="font-medium text-gray-900">{{ s.name }}</div>
            <div class="text-xs text-gray-500">시간: {{ s.window }}</div>
          </div>
        </div>
        <div class="flex items-center gap-4 text-sm">
          <div class="text-gray-500">작업자: <span class="text-gray-900">{{ s.worker }}</span></div>
          <button class="btn bg-white border">상세</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
const props = defineProps({
  steps: { type: Array, default: () => [] }
})

function dotColor(status) {
  if (status === '완료') return 'bg-green-500'
  if (status === '진행중') return 'bg-blue-500'
  if (status === '대기') return 'bg-gray-300'
  return 'bg-gray-300'
}
const StatusDot = {
  props: { status: String },
  template: `<span class="inline-block w-2.5 h-2.5 rounded-full" :class="dotClass"></span>`,
  computed: {
    dotClass() {
      if (this.status === '완료') return 'bg-green-500'
      if (this.status === '진행중') return 'bg-blue-500'
      return 'bg-gray-300'
    }
  }
}
</script>
