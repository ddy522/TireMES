<template>
    <!-- 상단 KPI -->
  <div class="space-y-4">
    <div class="grid grid-cols-1 md:grid-cols-4 gap-3">
      <KpiCard title="총 자재 가치" value="₩1.8B" sub="전월 +3.2%" tone="neutral" />
    <!-- 부족 자재: 동적 값/톤/서브텍스트 -->
      <KpiCard
        title="부족 자재"
        :value="shortageCount"
        :sub="shortageSub"
        :tone="shortageTone"
      />
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
      <button class="btn bg-white border" @click="exportExcel">내보내기</button>
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
                <button class="btn bg-white border" @click="openDetail(row)">상세</button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>

      <!-- LOT 목록 모달 -->
    <div v-if="lotModal.open" class="fixed inset-0 bg-black/50 flex justify-center items-start pt-20 z-50">
      <div class="bg-white rounded-lg shadow-lg w-[640px] max-h-[80vh] overflow-y-auto p-4">
        <div class="flex justify-between items-center mb-3">
          <h5 class="font-bold text-lg">{{ lotModal.partCode }} LOT 목록</h5>
          <button @click="closeLotModal" class="text-gray-500 hover:text-gray-800">&times;</button>
        </div>

        <div class="mb-2 text-sm text-gray-500" v-if="lotModal.partName">
          자재명: <span class="font-medium text-gray-700">{{ lotModal.partName }}</span>
        </div>

        <table class="w-full text-sm border border-gray-200">
          <thead class="bg-gray-100">
            <tr>
              <th class="border px-2 py-1 text-left">LOT 번호</th>
              <th class="border px-2 py-1 text-left">로트일자</th>
              <th class="border px-2 py-1 text-right">작업수량</th>
            </tr>
          </thead>
          <tbody>
            <tr v-if="lotModal.loading">
              <td class="border px-2 py-4 text-center" colspan="4">불러오는 중…</td>
            </tr>
            <tr v-else-if="lotRows.length === 0">
              <td class="border px-2 py-4 text-center" colspan="4">관련 LOT이 없습니다</td>
            </tr>
            <tr v-else v-for="r in lotRows" :key="r.lotNo">
              <td class="border px-2 py-1">{{ r.lotNo }}</td>
              <td class="border px-2 py-1">{{ r.createdAt }}</td>
              <td class="border px-2 py-1 text-right">{{ fmtNumber(r.qty) }}</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>






    </div>
  </div>
</template>





<script setup>
import { ref, computed, onMounted } from 'vue'
import { fetchInventoryAll } from '../../api/inventoryListSearch.js'
import { fetchLotsByPartCode } from '../../api/inventoryLots.js'
import KpiCard from './KpiCard.vue'
import * as XLSX from 'xlsx'

const lotModal = ref({ open: false, partCode: '', partName: '', loading: false })
const lotRows  = ref([])

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
// 부족 개수 집계 (KPI 카드)
const shortageCount = computed(() =>
  filteredRows.value.filter(r => (r.stockStatus ?? r.stockstatus) === '부족').length
)

// 표시용 텍스트/톤
const shortageTone = computed(() => (shortageCount.value > 0 ? 'bad' : 'good'))
const shortageSub  = computed(() =>
  shortageCount.value > 0 ? '긴급 보충 필요' : '모두 안전재고 이상'
)

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

// 상세 열기 → LOT API 호출
async function openDetail(row) {
  lotModal.value = {
    open: true,
    partCode: row.partCode,
    partName: row.partName,
    loading: true
  }
  lotRows.value = []
  try {
    lotRows.value = await fetchLotsByPartCode(row.partCode)
  } catch (e) {
    console.error(e)
    lotRows.value = []
  } finally {
    lotModal.value.loading = false
  }
}

function closeLotModal() {
  lotModal.value.open = false
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

async function exportExcel() {
  try {
    // 1) 자재 시트 데이터 구성
    const materials = filteredRows.value.map(r => ({
      자재번호: r.partCode,
      자재명: r.partName,
      현재재고: Number(r.currInventory ?? 0),
      안전재고: Number(r.safeQty ?? 0),
      단위: r.unit ?? 'kg',
      상태: (r.stockStatus ?? r.stockstatus ?? ''),
      공급업체: r.company ?? ''
    }))

    // 2) LOT 시트 데이터 구성 (자재별 LOT API 호출)
    const lotPromises = filteredRows.value.map(async r => {
      const lots = await fetchLotsByPartCode(r.partCode)  // [{ lotNo, createdAt, qty, ... }]
      return lots.map(l => ({
        자재번호: r.partCode,
        자재명: r.partName,
        LOT번호: l.lotNo,
        로트일자: l.createdAt,          // 백엔드에서 'YYYY-MM-DD HH:mm' 형식으로 내려오도록 했던 값
        작업수량: Number(l.qty ?? 0)
      }))
    })
    const lotResults = (await Promise.all(lotPromises)).flat()

    // 3) 워크북/시트 생성
    const wb = XLSX.utils.book_new()

    const wsMaterials = XLSX.utils.json_to_sheet(materials)
    wsMaterials['!cols'] = [
      { wch: 14 }, { wch: 22 }, { wch: 12 }, { wch: 12 }, { wch: 8 }, { wch: 8 }, { wch: 18 }
    ]
    XLSX.utils.book_append_sheet(wb, wsMaterials, '자재')

    const wsLots = XLSX.utils.json_to_sheet(lotResults)
    wsLots['!cols'] = [
      { wch: 14 }, { wch: 22 }, { wch: 18 }, { wch: 18 }, { wch: 10 }
    ]
    XLSX.utils.book_append_sheet(wb, wsLots, 'LOT')

    // 4) 파일 저장
    const date = new Date().toISOString().slice(0,10)
    XLSX.writeFile(wb, `materials_with_lots_${date}.xlsx`)
  } catch (err) {
    console.error('엑셀 내보내기 실패:', err)
    alert('엑셀 내보내기에 실패했습니다.')
  }
}

</script>
