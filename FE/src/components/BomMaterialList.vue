<template>
  <div class="space-y-3">
    <div v-for="m in materials" :key="m.id" class="border rounded-md p-3">
      <div class="flex items-center justify-between">
        <div>
          <div class="text-sm font-medium text-gray-900">{{ m.name }}</div>
          <div class="text-xs text-gray-500">{{ m.id }}</div>
        </div>
        <div :class="ratioClass(m)" class="text-sm font-semibold">
          {{ m.used }} / {{ m.total }} kg
        </div>
      </div>
      <div class="w-full bg-gray-100 rounded h-2 mt-2">
        <div class="h-2 rounded" :style="{ width: progress(m) }"
             :class="barClass(m)"></div>
      </div>
    </div>

    <!-- 요약 -->
    <div class="grid grid-cols-3 gap-2 text-center text-sm">
      <div class="card"><div class="text-gray-500">완료</div><div class="text-lg font-semibold">{{ doneCount }}</div></div>
      <div class="card"><div class="text-gray-500">부족</div><div class="text-lg font-semibold">{{ shortageCount }}</div></div>
      <div class="card"><div class="text-gray-500">대기</div><div class="text-lg font-semibold">{{ waitCount }}</div></div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
const props = defineProps({
  materials: { type: Array, default: () => [] }
})

const doneCount = computed(() => props.materials.filter(m => m.used >= m.total).length)
const shortageCount = computed(() => props.materials.filter(m => m.used < m.total && (m.used / m.total) >= 0.7).length)
const waitCount = computed(() => props.materials.filter(m => (m.used / m.total) < 0.7).length)

function progress(m) {
  const ratio = Math.min(m.used / m.total, 1)
  return `${(ratio * 100).toFixed(0)}%`
}
function ratioClass(m) {
  const r = m.used / m.total
  if (r >= 1) return 'text-green-700'
  if (r >= 0.7) return 'text-yellow-700'
  return 'text-red-700'
}
function barClass(m) {
  const r = m.used / m.total
  if (r >= 1) return 'bg-green-500'
  if (r >= 0.7) return 'bg-yellow-500'
  return 'bg-red-500'
}
</script>
