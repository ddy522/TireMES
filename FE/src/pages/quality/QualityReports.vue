<template>
  <div class="space-y-4">
    <div class="bg-white border rounded-xl p-4">
      <div class="font-semibold mb-3">품질 보고서 생성</div>
      <div class="grid grid-cols-1 md:grid-cols-4 gap-3">
        <select class="border rounded px-3 py-2 text-sm"><option>유형 선택</option><option>일일</option><option>주간</option></select>
        <input type="date" class="border rounded px-3 py-2 text-sm"/>
        <input type="date" class="border rounded px-3 py-2 text-sm"/>
        <button class="btn w-full md:w-auto">보고서 생성</button>
      </div>
    </div>

    <div class="bg-white border rounded-xl p-4">
      <div class="font-semibold mb-3">최근 생성된 보고서</div>
      <table class="w-full text-sm">
        <thead class="text-gray-500"><tr>
          <th class="text-left p-2">보고서명</th><th class="text-left p-2">유형</th><th class="text-left p-2">생성일</th><th class="text-left p-2">생성자</th><th class="text-left p-2">작업</th>
        </tr></thead>
        <tbody>
          <tr class="border-t"><td class="p-2">2024-12-05 일일 품질 보고서</td><td class="p-2">일일</td><td class="p-2">2024-12-05 18:00</td><td class="p-2">시스템 자동</td><td class="p-2"><button class="btn">다운로드</button></td></tr>
          <tr class="border-t"><td class="p-2">2024-12주차 주간 품질 보고서</td><td class="p-2">주간</td><td class="p-2">2024-12-02 09:00</td><td class="p-2">김품질</td><td class="p-2"><button class="btn">다운로드</button></td></tr>
        </tbody>
      </table>
    </div>
  </div>
</template>
<script setup></script>
<style scoped>
.btn { @apply px-3 py-2 border rounded bg-white hover:bg-gray-50 text-sm; }
</style>
