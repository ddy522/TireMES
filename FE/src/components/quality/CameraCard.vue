<template>
  <div class="bg-white border rounded-xl p-4">
    <div class="flex items-center justify-between">
      <div>
        <div class="text-xs text-gray-500">{{ line }}</div>
        <div class="font-semibold">{{ name }}</div>
      </div>
      <span class="px-2 py-1 rounded text-xs text-white"
            :class="{'bg-green-500': status==='정상', 'bg-yellow-500': status==='주의', 'bg-gray-400': status==='정비중'}">
        {{ status }}
      </span>
    </div>
    <div class="mt-2 grid grid-cols-2 text-sm text-gray-600 gap-2">
      <div>정확도: <b class="text-gray-900">{{ acc }}</b></div>
      <div>불량 검출: <b class="text-gray-900">{{ defects }}</b></div>
    </div>
    <div class="mt-3 flex gap-2">
      <button class="btn">실시간 보기</button>
      <button class="btn">설정</button>
    </div>
  </div>
</template>

<script setup>
defineProps({ name: String, line: String, status: String, acc: String, defects: String })
</script>
<style scoped>
.btn { @apply px-3 py-1 border rounded text-sm bg-white hover:bg-gray-50; }
</style>
