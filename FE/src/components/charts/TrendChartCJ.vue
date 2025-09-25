<template>
    <!-- 높이를 고정하면 대시보드 레이아웃이 안정적 -->
    <div style="height: 280px">
      <LineChart :chart-data="chartData" :options="options" />
    </div>
  </template>
  
  <script setup>
  import { LineChart } from 'vue-chart-3'
  import {
    Chart,
    LineElement,
    PointElement,
    LinearScale,
    CategoryScale,
    Tooltip,
    Filler,
    Legend,
  } from 'chart.js'
  import { computed, ref, watch } from 'vue'
  /* Chart.js 필수 요소 등록 */
  Chart.register(LineElement, PointElement, LinearScale, CategoryScale, Tooltip, Filler, Legend)
  /* 부모에서 내려줄 프롭스 */
  const props = defineProps({
    mode: { type: String, default: 'hourly' },         // 'hourly' | 'daily' | 'monthly'
    metric: { type: String, default: '온도' },         // 표시용 이름
    hourlyDate: { type: String, default: '' },         // 'YYYY-MM-DD'
    dailyMonth: { type: String, default: '' },         // 'YYYY-MM'
    monthlyYear: { type: Number, default: new Date().getFullYear() },
  })
  /* 단위 표기 */
  const unit = computed(() => {
    switch (props.metric) {
      case '온도': return '℃'
      case '습도': return '%'
      case '전력': return 'kW'
      case '진동': return 'g'
      default: return ''
    }
  })
  /* x축 라벨 생성 */
  const labels = computed(() => {
    if (props.mode === 'hourly') {
      // 10분 간격 (00:00 ~ 23:50)
      const out = []
      for (let h = 0; h < 24; h++) for (let m = 0; m < 60; m += 10)
        out.push(`${String(h).padStart(2,'0')}:${String(m).padStart(2,'0')}`)
      return out
    }
    if (props.mode === 'daily') {
      const [y, m] = props.dailyMonth.split('-').map(Number)
      const last = new Date(y, m, 0).getDate()
      return Array.from({ length: last }, (_, i) => `${i + 1}일`)
    }
    return Array.from({ length: 12 }, (_, i) => `${i + 1}월`)
  })
  /* 더미 데이터 — 실데이터 연동 시 이 부분만 교체 */
  function genData(n, base = 50, span = 10) {
    return Array.from({ length: n }, (_, i) =>
      Math.round(base + Math.sin(i / 2.7) * span + (Math.random() - 0.5) * span * 0.6)
    )
  }
  /* 데이터셋 */
  const data = ref([])
  watch(
    () => [props.mode, props.metric, props.hourlyDate, props.dailyMonth, props.monthlyYear],
    async () => {
      // ── 예시(실제 API 사용 시):
      // const resp = await fetch(`/api/metrics?metric=${props.metric}&mode=${props.mode}&date=${props.hourlyDate}...`)
      // data.value = await resp.json()
      // 데모 데이터
      if (props.mode === 'hourly')      data.value = genData(labels.value.length, 50, 12)
      else if (props.mode === 'daily')  data.value = genData(labels.value.length, 60, 15)
      else                              data.value = genData(labels.value.length, 55, 8)
    },
    { immediate: true }
  )
  /* 차트 데이터/옵션 */
  const chartData = computed(() => ({
    labels: labels.value,
    datasets: [{
      label: props.metric,
      data: data.value,
      borderWidth: 2,
      borderColor: '#2563eb',
      backgroundColor: 'rgba(37,99,235,0.15)',
      tension: 0.35,
      fill: true,
      pointRadius: 0,       // 포인트 점 제거(깔끔)
      pointHitRadius: 6,
    }]
  }))
  const options = computed(() => ({
    responsive: true,
    maintainAspectRatio: false,
    animation: { duration: 250 },
    plugins: {
      legend: { display: false },
      tooltip: {
        callbacks: { label: (ctx) => `${ctx.parsed.y}${unit.value}` }
      }
    },
    scales: {
      x: { ticks: { maxTicksLimit: 12, autoSkip: true }, grid: { display: false } },
      y: { ticks: { callback: (v) => `${v}${unit.value}` }, grid: { color: 'rgba(0,0,0,0.06)' } }
    }
  }))
  </script>