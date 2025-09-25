<template>
  <table class="w-full text-sm">
    <thead>
      <tr class="text-gray-500 bg-gray-50">
        <th class="text-left py-2 px-3">품번</th>
        <th class="text-left px-3">품명</th>
        <th class="text-right px-3">현재고</th>
        <th class="text-right px-3">안전재고</th>
        <th class="text-right px-3">부족</th>
      </tr>
    </thead>

    <tbody>
      <tr v-if="loading">
        <td class="py-4 text-center" colspan="5">불러오는 중…</td>
      </tr>

      <!-- ✅ 전체 보기: rows 기준으로 빈 상태 판단 -->
      <tr v-else-if="rows.length === 0">
        <td class="py-4 text-center" colspan="5">데이터가 없습니다</td>
      </tr>

      <!-- ✅ 전체 행 출력 -->
      <tr v-else v-for="r in limitedRows" :key="r.partCode" class="border-t">
        <td class="py-2 px-3">{{ r.partCode }}</td>
        <td class="px-3">{{ r.partName }}</td>
        <td class="text-right px-3">{{ fmt(r.currInventory) }}</td>
        <td class="text-right px-3">{{ fmt(r.expected) }}</td>
        <td class="text-right px-3 font-semibold" :class="r.shortage>0 ? 'text-red-600' : 'text-gray-500'">
          {{ fmt(r.shortage) }}
        </td>
      </tr>
    </tbody>
  </table>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { fetchInventoryAll } from '../../api/inventoryListSearch.js'

const props = defineProps({ limit: { type: Number, default: 5 } })

const loading = ref(false)
const rows = ref([])

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

/* ✅ 전체 행 계산 (필터 제거)
 * - expected = expectedConsumption ?? safeQty ?? 0
 * - shortage = max(0, expected - currInventory)
 * - 중요도 높게 보기 위해 부족 많은 순 정렬(원본 순서 유지하려면 sort 라인 주석 처리)
 */
const displayRows = computed(() => {
  const mapped = (rows.value || []).map(r => {
    const curr = Number(r.currInventory ?? 0)
    const expected = Number(r.expectedConsumption ?? r.safeQty ?? 0)
    const shortage = Math.max(0, expected - curr)
    return {
      partCode: r.partCode,
      partName: r.partName,
      currInventory: curr,
      expected,
      shortage,
    }
  })
  // 중요도 순서로 보고 싶다면 유지:
  return mapped.sort((a, b) => b.shortage - a.shortage)

  // 원본 순서를 유지하려면 위 sort를 주석 처리하고 아래 반환:
  // return mapped
})

const limitedRows = computed(() =>
  props.limit > 0 ? displayRows.value.slice(0, props.limit) : displayRows.value
)

function fmt(n) {
  const v = Number(n)
  return Number.isFinite(v) ? v.toLocaleString() : (n ?? '-')
}
</script>
