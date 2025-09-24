<template>
  <div class="bg-white border rounded-xl p-4">
    <div class="text-xs text-gray-500">{{ sub }}</div>
    <div class="mt-1 text-2xl font-bold">{{ value }}</div>
    <div class="mt-1 text-sm" :class="toneClass">{{ caption }}</div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
const props = defineProps({
  value: String, sub: String, caption: String,
  tone: { type: String, default: 'neutral' } // neutral|good|bad|warn
})
const toneClass = computed(() => ({
  good: 'text-green-600',
  bad: 'text-red-600',
  warn: 'text-yellow-600',
  neutral: 'text-gray-500'
}[props.tone] || 'text-gray-500'))
</script>
