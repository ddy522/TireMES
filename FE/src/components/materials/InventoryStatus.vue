<template>
    <!-- 상단 KPI -->
  <div class="space-y-4">
    <div class="grid grid-cols-1 md:grid-cols-4 gap-3">
      <KpiCard title="총 자재 가치" value="₩1.8B" sub="전월 +3.2%" tone="neutral" />
      <KpiCard title="부족 자재" value="3" sub="긴급 보충 필요" tone="bad" />
      <KpiCard title="자재 회전율" value="5.2" sub="전월 +0.4" tone="good" />
      <KpiCard title="재고 정확도" value="99.1%" sub="전월 +0.2%" tone="good" />
    </div>

    <!-- 검색/필터 -->
    <div class="flex items-center gap-2">
      <input
        v-model="keyword"
        class="border rounded-md px-3 py-2 text-sm flex-1"
        placeholder="자재 검색… (코드/명)"
      />
      <!-- <select class="border rounded-md px-3 py-2 text-sm">
        <option>카테고리</option>
        <option>고무원료</option>
        <option>충전재</option>
        <option>보강재</option>
      </select>
      <button class="btn bg-white border">필터</button> -->
      <button class="btn bg-white border" @click="exportCsv">내보내기</button>
      <!-- <button class="btn-primary">+ 자재 등록</button> -->
    </div>

    <!-- 재고 테이블 -->
    <div class="card overflow-x-auto">
      <table class="min-w-full text-sm">
        <thead>
          <tr class="bg-gray-100 text-gray-700">
            <th class="px-3 py-2 text-left">자재번호</th>
            <th class="px-3 py-2 text-left">자재명</th>
            <th class="px-3 py-2 text-right">현재 재고</th>
            <th class="px-3 py-2 text-right">안전 재고</th>
            <th class="px-3 py-2 text-left">단위</th>
            <th class="px-3 py-2 text-left">상태</th>
            <th class="px-3 py-2 text-left">공급업체</th>
            <th class="px-3 py-2 text-left">작업</th>
          </tr>
        </thead>
        <tbody>
          <tr v-if="loading">
            <td class="px-3 py-4 text-center" colspan="8">불러오는 중…</td>
          </tr>
          <tr v-else-if="filteredRows.length === 0">
            <td class="px-3 py-4 text-center" colspan="8">데이터가 없습니다</td>
          </tr>
          <tr v-else v-for="row in filteredRows" :key="row.partCode" class="border-t">
            <td class="px-3 py-2">{{ row.partCode }}</td>
            <td class="px-3 py-2">{{ row.partName }}</td>
            <td class="px-3 py-2 text-right">{{ fmtNumber(row.currInventory) }}</td>
            <td class="px-3 py-2 text-right">{{ fmtNumber(row.safeQty) }}</td>
            <td class="px-3 py-2">{{ row.unit}}</td>
            <td class="px-3 py-2">
              <!-- stockStatus/stockstatus 중 실제 응답 키에 맞춰 사용 -->
              <span :class="['badge', badge(row.stockStatus ?? row.stockstatus)]">
                {{ row.stockStatus ?? row.stockstatus }}
              </span>
            </td>
            <td class="px-3 py-2">{{ row.company }}</td>
            <td class="px-3 py-2">
              <div class="flex items-center gap-2">
                <button class="btn bg-white border">상세</button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
// ✅ API 함수명 통일: inventoryListSearch.js에서 이렇게 export 하세요: export async function fetchInventoryAll() { ... }
import { fetchInventoryAll } from '../../api/inventoryListSearch.js'

const rows = ref([])
const loading = ref(false)
const keyword = ref('')

onMounted(async () => {
  loading.value = true
  try {
    rows.value = await fetchInventoryAll()
  } catch (e) {
    console.error(e)
    rows.value = []
  } finally {
    loading.value = false
  }
})

const filteredRows = computed(() => {
  const q = keyword.value.trim().toLowerCase()
  if (!q) return rows.value
  return rows.value.filter(r =>
    String(r.partName ?? '').toLowerCase().includes(q) ||
    String(r.partCode ?? '').toLowerCase().includes(q)
  )
})

function badge(status) {
  // 백엔드 CASE 결과 예: '부족' | '여유' | 기타
  if (status === '부족') return 'badge-red'
  if (status === '여유') return 'badge-green'
  return 'badge-gray'
}

function fmtNumber(n) {
  const v = Number(n)
  return Number.isFinite(v) ? v.toLocaleString() : n ?? '-'
}

function exportCsv() {
  // 간단 CSV 내보내기 (원하면 실제 구현)
  const headers = ['partCode','partName','currInventory','safeQty','unit','stockStatus','company']
  const data = filteredRows.value.map(r => [
    r.partCode, r.partName, r.currInventory, r.safeQty, r.unit ?? 'kg', (r.stockStatus ?? r.stockstatus ?? ''), r.company
  ])
  const csv = [headers.join(','), ...data.map(row => row.join(','))].join('\n')
  const blob = new Blob([csv], { type: 'text/csv;charset=utf-8;' })
  const url = URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url
  a.download = 'inventory.csv'
  a.click()
  URL.revokeObjectURL(url)
}
</script>
