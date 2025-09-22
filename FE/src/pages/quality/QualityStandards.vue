<template>
  <div class="bg-white border rounded-xl p-4 space-y-3">
    <div class="font-semibold">품질 기준</div>
    <table class="w-full text-sm">
      <thead class="text-gray-500"><tr>
        <th class="text-left p-2">항목</th><th class="text-left p-2">기준</th><th class="text-left p-2">측정 방법</th><th class="text-left p-2">비고</th>
      </tr></thead>
      <tbody>
        <tr class="border-t"><td class="p-2">치수 공차</td><td class="p-2">±0.5 mm</td><td class="p-2">CCD + 게이지</td><td class="p-2">라인 C 보정 필요</td></tr>
        <tr class="border-t"><td class="p-2">외관 결함</td><td class="p-2">AQL 0.65</td><td class="p-2">비전 인스펙션</td><td class="p-2">스크래치 집중</td></tr>
      </tbody>
    </table>
  </div>
</template>
<script setup></script>
