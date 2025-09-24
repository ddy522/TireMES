<template>
  <div class="space-y-4">
    <div class="bg-white border rounded-xl p-4">
      <div class="font-semibold mb-3">검사 항목/레시피 관리</div>
      <div class="flex gap-2">
        <select class="border rounded px-3 py-2 text-sm">
          <option>라인 선택</option><option>라인 A</option><option>라인 B</option>
        </select>
        <button class="btn">항목 추가</button>
        <button class="btn">불러오기</button>
        <button class="btn">저장</button>
      </div>
      <div class="mt-3 text-sm text-gray-500">※ 여기에서 치수/외관/강도 등 검사 기준을 편집합니다.</div>
    </div>

    <div class="bg-white border rounded-xl p-4">
      <div class="font-semibold mb-3">검사 Queue</div>
      <ul class="text-sm space-y-2">
        <li class="border rounded p-2">#TY-2024-1205 · 라인 C · 진행중</li>
        <li class="border rounded p-2">#TY-2024-1206 · 라인 B · 대기</li>
      </ul>
    </div>
  </div>
</template>
<script setup>
</script>
<style scoped>
.btn { @apply px-3 py-2 border rounded bg-white hover:bg-gray-50 text-sm; }
</style>
