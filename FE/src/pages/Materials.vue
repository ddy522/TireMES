<!-- import InspectionDefect from '../components/materials/ComsumptionAnalysis.vue'
import InspectionDefect from '../components/materials/InventoryStatus.vue'
import InspectionDefect from '../components/materials/MaterialPlanning.vue' -->

<template>
  <div class="space-y-6">
    <!-- 헤더 + 탭 -->
    <div class="flex items-center gap-2 text-sm text-gray-500">
      <h1 class="text-2xl font-bold text-gray-900">자재 관리</h1>
      <span class="ml-2 text-sm text-gray-500">원자재 재고 관리 및 소모 분석</span>
      <span class="ml-auto text-xs text-blue-600 bg-blue-50 border border-blue-200 rounded px-2 py-0.5">/materials</span>
    </div>

    <div class="flex gap-2 text-sm">
      <button
        v-for="t in tabs"
        :key="t.key"
        class="px-3 py-1.5 rounded border"
        :class="t.key === tab ? 'bg-blue-50 text-blue-700 border-blue-200' : 'bg-white text-gray-700 hover:bg-gray-50'"
        @click="tab = t.key"
      >
        {{ t.label }}
      </button>
    </div>

    <InventoryStatus v-if="tab==='stock'" />
    <ConsumptionAnalysis v-else-if="tab==='usage'" />
    <MaterialPlanning v-else />
  </div>
</template>

<script setup>
import { ref } from 'vue'
import InventoryStatus from '../components/materials/InventoryStatus.vue'
import ConsumptionAnalysis from '../components/materials/ConsumptionAnalysis.vue'
import MaterialPlanning from '../components/materials/MaterialPlanning.vue'
import KpiCard from '../components/materials/KpiCard.vue'

const tabs = [
  { key: 'stock', label: '재고 현황' },
  { key: 'usage', label: '소모 분석' },
  { key: 'plan',  label: '자재 계획' },
]
const tab = ref('stock')
</script>

