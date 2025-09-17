<template>
  <div class="space-y-4">
    <!-- 상단 KPI -->
    <div class="grid grid-cols-1 md:grid-cols-4 gap-4">
      <KpiCard title="일일 소모량" value="45.2톤" sub="전일 대비 +2.1%" />
      <KpiCard title="주간 소모량" value="316.4톤" sub="전주 대비 +1.8%" />
      <KpiCard title="월간 소모량" value="1,347톤" sub="전월 대비 +3.2%" />
      <KpiCard title="소모 효율" value="94.7%" sub="목표: 95%" />
    </div>

    <!-- 자재별 소모 분석(간단 막대) -->
    <div class="card">
      <div class="space-y-3">
        <BarRow label="천연고무" value="12.5톤" :pct="62" delta="+1.2%" />
        <BarRow label="합성고무" value="8.3톤"  :pct="41" delta="+0.8%" />
        <BarRow label="스틸코드" value="3.2톤"  :pct="21" delta="-0.5%" />
        <BarRow label="카본블랙" value="2.1톤"  :pct="15" delta="+2.1%" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { defineComponent, h } from 'vue'

const KpiCard = defineComponent({
  props: { title: String, value: String, sub: String },
  setup(p){ return () =>
    h('div',{class:'card'},[
      h('div',{class:'text-sm text-gray-500'},p.title),
      h('div',{class:'text-2xl font-bold mt-1'},p.value),
      h('div',{class:'text-xs text-gray-500'},p.sub),
    ])
  }
})

const BarRow = defineComponent({
  props: { label: String, value: String, pct: Number, delta: String },
  setup(p){ return () =>
    h('div', {class:'grid grid-cols-12 items-center gap-2'}, [
      h('div',{class:'col-span-3 text-sm text-gray-700'}, p.label),
      h('div',{class:'col-span-7 w-full bg-gray-100 h-2 rounded'}, [
        h('div',{class:'h-2 rounded bg-gray-800', style:{ width: (p.pct||0) + '%' }})
      ]),
      h('div',{class:'col-span-2 text-right text-sm'}, [
        h('span',{class:'font-medium mr-1'}, p.value),
        h('span',{class:'text-xs text-gray-500'}, p.delta)
      ])
    ])
  }
})
</script>
