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

      <tr v-else-if="shortageRows.length === 0">
        <td class="py-4 text-center" colspan="5">부족 자재가 없습니다</td>
      </tr>

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

/** 옵션: 화면에 몇 줄만 보여줄지 (기본 5줄). 전체를 보여주려면 :limit="0" */
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

/** 부족 계산:
 * - 우선 expectedConsumption 사용
 * - 없으면 safeQty 사용
 * - shortage = max(0, expected - currInventory)
 */
const shortageRows = computed(() => {
  return (rows.value || [])
    .map(r => {
      const curr = Number(r.currInventory ?? 0)
      const expected = Number(
        r.expectedConsumption ?? r.safeQty ?? 0
      )
      const shortage = Math.max(0, expected - curr)
      return {
        partCode: r.partCode,
        partName: r.partName,
        currInventory: curr,
        expected,
        shortage,
      }
    })
    .filter(r => r.shortage > 0)
    .sort((a, b) => b.shortage - a.shortage) // 부족 많은 순
})

const limitedRows = computed(() =>
  props.limit > 0 ? shortageRows.value.slice(0, props.limit) : shortageRows.value
)

function fmt(n) {
  const v = Number(n)
  return Number.isFinite(v) ? v.toLocaleString() : n ?? '-'
}
</script>
