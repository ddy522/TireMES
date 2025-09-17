<template>
  <div class="space-y-4">
    <h2 class="text-lg font-semibold text-gray-900">검사 보고서</h2>

    <div class="card">
      <div class="grid grid-cols-1 md:grid-cols-4 gap-2">
        <input type="date" class="border rounded-md px-3 py-2 text-sm" />
        <input type="date" class="border rounded-md px-3 py-2 text-sm" />
        <select class="border rounded-md px-3 py-2 text-sm">
          <option>전체 라인</option>
          <option>라인 A</option>
          <option>라인 B</option>
        </select>
        <button class="btn-primary">검색</button>
      </div>
    </div>

    <div class="card overflow-x-auto">
      <table class="min-w-full text-sm">
        <thead>
          <tr class="bg-gray-100 text-gray-700">
            <th class="px-3 py-2 text-left">일자</th>
            <th class="px-3 py-2 text-left">작업지시</th>
            <th class="px-3 py-2 text-left">검사 수량</th>
            <th class="px-3 py-2 text-left">합격</th>
            <th class="px-3 py-2 text-left">불합격</th>
            <th class="px-3 py-2 text-left">합격률</th>
          </tr>
        </thead>
        <tbody>
          <tr class="border-t" v-for="row in rows" :key="row.wo">
            <td class="px-3 py-2">{{ row.date }}</td>
            <td class="px-3 py-2">{{ row.wo }}</td>
            <td class="px-3 py-2">{{ row.total }}</td>
            <td class="px-3 py-2">{{ row.pass }}</td>
            <td class="px-3 py-2">{{ row.fail }}</td>
            <td class="px-3 py-2">{{ row.rate }}%</td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup>
const rows = [
  { date: '2024-12-16', wo: 'WO-2024-001', total: 100, pass: 98, fail: 2, rate: 98 },
  { date: '2024-12-16', wo: 'WO-2024-002', total: 80,  pass: 79, fail: 1, rate: 98.8 },
]
</script>
