<template>
  <div class="bg-white border rounded-xl p-4 shadow-sm">
    <div class="text-xs text-gray-500">{{ title }}</div>
    <div class="mt-1 text-2xl font-bold">{{ value }}</div>

    <div v-if="sub"
         class="mt-1 text-xs"
         :class="toneClass">
      {{ sub }}
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  title: { type: String, required: true },
  value: { type: [String, Number], required: true },
  sub:   { type: String, default: '' },
  // neutral | good | bad 선택적으로 색만 바꾸고 싶을 때 사용
  tone:  { type: String, default: 'neutral' }
})

const toneClass = computed(() => {
  if (props.tone === 'good') return 'text-green-600'
  if (props.tone === 'bad')  return 'text-red-600'
  return 'text-gray-500'
})
</script>
