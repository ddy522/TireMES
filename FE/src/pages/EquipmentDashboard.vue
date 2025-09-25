<template>
    <div class="space-y-4 min-h-screen">
      <div class="flex items-center gap-2 text-sm text-gray-500">
        <h1 class="text-2xl font-bold text-gray-900">설비 관리</h1>
        <span class="ml-auto text-xs text-blue-600 bg-blue-50 border border-blue-200 rounded px-2 py-0.5">
          /equipment
        </span>
      </div>
  
      <div class="grid grid-cols-12 gap-3 h-[calc(100vh-110px)]">
        <section class="card col-span-12 lg:col-span-3 lg:row-span-2 flex flex-col overflow-auto">
          <h3 class="section-title">설비 정보</h3>
  
          <div class="grid grid-cols-2 gap-2 text-sm">
            <select v-model="proc" class="border rounded px-2 py-1.5">
              <option v-for="p in procs" :key="p" :value="p">{{ p }}</option>
            </select>
            <select v-model="selEqId" class="border rounded px-2 py-1.5">
              <option v-for="e in equipmentOptions" :key="e.id" :value="e.id">{{ e.name }}</option>
            </select>
          </div>
  
          <button class="btn bg-white border w-full mt-2" @click="openHistory">
            정비 이력 보기
          </button>
  
          <div v-if="eq" class="mt-2 text-sm grid grid-cols-[60px_1fr] gap-y-1">
            <div class="text-gray-500">공정</div><div>{{ proc }}</div>
            <div class="text-gray-500">설비</div><div>{{ eq.name }}</div>
            <div class="text-gray-500">가동</div>
            <div>
              <span :class="statusClass(eq.status)" class="px-1.5 py-0.5 rounded text-xs border">
                {{ eq.status }}
              </span>
            </div>
            <div class="text-gray-500">상태</div><div>{{ eq.state }}</div>
          </div>
  
          <div class="h-px bg-gray-100 my-1.5"></div>
  
          <h4 class="font-semibold text-sm text-gray-900">상태 스냅샷</h4>
          <div class="grid grid-cols-2 gap-1 mt-1">
            <button
              v-for="m in metrics"
              :key="m.key"
              class="snapshot-btn flex items-center justify-center text-sm font-medium"
              :class="[
                !isMetricVisible(m) ? 'opacity-50 cursor-not-allowed' : '',
                selectedMetric === m.key ? 'ring-2 ring-blue-400' : ''
              ]"
              :disabled="!isMetricVisible(m)"
              @click="onPickMetric(m)"
            >
              {{ m.label }}
            </button>
          </div>
  
          <div class="h-px bg-gray-100 my-1.5"></div>
  
          <h4 class="font-semibold text-sm text-gray-900">빠른 작업</h4>
          <div class="grid grid-cols-2 gap-2 mt-2">
            <button class="btn quick-btn bg-white border">고장 등록</button>
            <button class="btn quick-btn bg-white border">이상 징후</button>
            <button class="btn quick-btn bg-white border">긴급 경고</button>
            <button class="btn quick-btn bg-white border">예비품 요청</button>
            <button class="btn quick-btn bg-white border col-span-2">실시간 정보</button>
          </div>
        </section>
  
        <section class="card col-span-12 lg:col-span-9 flex flex-col">
          <h3 class="section-title">설비 상태 데이터</h3>
  
          <div class="grid grid-cols-1 md:grid-cols-3 gap-2 text-sm mb-2">
            <div class="mini-box">
              <div class="text-gray-500 text-xs mb-1">정비 유형</div>
              <div class="font-medium">{{ maint.type }}</div>
            </div>
            <div class="mini-box">
              <div class="text-gray-500 text-xs mb-1">담당자</div>
              <div class="font-medium">{{ maint.owner }}</div>
            </div>
            <div class="mini-box">
              <div class="text-gray-500 text-xs mb-1">최근 정비</div>
              <div class="font-medium">{{ maint.last }}</div>
            </div>
          </div>
  
          <div class="flex items-center flex-wrap gap-2 mb-2">
            <button
              class="btn border bg-white text-xs"
              :class="chartScale==='minute' ? 'bg-blue-50 border-blue-300 text-blue-700' : ''"
              @click="onChangeScale('minute')"
            >minute</button>
            <button
              class="btn border bg-white text-xs"
              :class="chartScale==='hour' ? 'bg-blue-50 border-blue-300 text-blue-700' : ''"
              @click="onChangeScale('hour')"
            >hour</button>
            <button
              class="btn border bg-white text-xs"
              :class="chartScale==='day' ? 'bg-blue-50 border-blue-300 text-blue-700' : ''"
              @click="onChangeScale('day')"
            >day</button>
  
            <div class="ml-3 text-xs text-gray-500">
              • 선택 항목: <span class="font-medium text-gray-800">{{ labelOf(selectedMetric) }}</span>
              <span class="mx-1 text-gray-300">|</span>
              <span class="text-xs">현재값: <b>{{ eq?.[selectedMetric] ?? 'N/A' }}</b></span>
            </div>
  
            <div class="ml-auto flex items-center gap-1">
              <button class="btn border bg-white text-xs" @click="goStart">« 처음</button>
              <button class="btn border bg-white text-xs" @click="goPrev">‹ 이전</button>
              <button class="btn border bg-white text-xs" @click="goNext">다음 ›</button>
              <button class="btn border bg-white text-xs" @click="goEnd">끝 »</button>
            </div>
          </div>
  
          <div class="border rounded p-3 flex-1 min-h-[220px]">
            <canvas ref="chartEl"></canvas>
          </div>
  
          <div class="mt-2 flex items-center gap-3">
            <span class="text-xs text-gray-500 w-16 text-right">타임라인</span>
            <input
              class="w-full"
              type="range"
              :min="0"
              :max="rangeMax"
              :step="1"
              v-model.number="rangeOffset"
              @input="updateChartForMetric"
            />
            <span class="text-xs text-gray-500 w-20 text-right">
              {{ rangeOffset }}/{{ rangeMax }}
            </span>
          </div>
        </section>
  
        <section class="card col-span-12 lg:col-start-4 lg:col-span-9">
          <div class="flex items-center justify-between">
            <h3 class="section-title mb-0">알림</h3>
            <button class="btn bg-white border text-xs" @click="ackAll">모두 확인</button>
          </div>
  
          <ul class="space-y-2 text-sm mt-1 max-h-[160px] overflow-auto pr-1">
            <li v-for="(a,i) in alarms" :key="i" class="flex items-start gap-2">
              <span
                class="mt-1 w-2 h-2 rounded-full"
                :class="a.level==='high' ? 'bg-red-500' : (a.level==='mid' ? 'bg-yellow-500' : 'bg-blue-500')"
              ></span>
              <div>
                <div class="font-medium text-gray-900">{{ a.title }}</div>
                <div class="text-gray-500">{{ a.time }}</div>
              </div>
            </li>
          </ul>
        </section>
      </div>
  
      <div
        v-if="showModal"
        class="fixed inset-0 bg-black/30 flex items-center justify-center z-[9999]"
        @click.self="closeModal"
      >
        <div class="bg-white rounded shadow-lg p-4 w-[min(720px,92vw)] max-h-[80vh] overflow-auto">
          <div class="flex justify-between items-center mb-2 border-b pb-2">
            <h2 class="text-lg font-bold">{{ modalTitle }}</h2>
            <button @click="closeModal" class="text-gray-500 hover:text-gray-800">X</button>
          </div>
  
          <template v-if="modalType==='history'">
            <table class="w-full border text-sm">
              <thead class="bg-gray-50">
                <tr>
                  <th class="border px-2 py-1">일자</th>
                  <th class="border px-2 py-1">내용</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="(r,i) in modalData" :key="i">
                  <td class="border px-2 py-1">{{ r.date }}</td>
                  <td class="border px-2 py-1">{{ r.desc }}</td>
                </tr>
              </tbody>
            </table>
          </template>
  
          <template v-else-if="modalType==='ack'">
            <div class="text-sm">알림이 모두 확인 처리되었습니다.</div>
          </template>
  
          <div class="text-right border-t mt-3 pt-2">
            <button @click="closeModal" class="btn bg-white border">닫기</button>
          </div>
        </div>
      </div>
      </div>
  </template>
  
  <script setup>
  import { ref, reactive, watch, onMounted, nextTick } from 'vue'
  import Chart from 'chart.js/auto'
  
  /* 모달 상태 */
  const showModal = ref(false)
  const modalTitle = ref('')
  const modalType = ref('')
  const modalData = ref([])
  
  /* 공정/설비 */
  const procs = ['믹싱', '압출', '재단', '성형', '가류', '검사']
  const proc = ref('믹싱')
  const equipmentOptions = ref([])
  const selEqId = ref('')
  const eq = ref(null)
  watch(selEqId, v => { eq.value = equipmentOptions.value.find(e => e.id === v) || null })
  
  /* 지시서 */
  const maint = reactive({ last: '2025-09-10', owner: '박민수', type: '정기점검' })
  
  /* 알림 */
  const alarms = ref([
    { title: '온도 경고: 85℃ 초과', time: '오늘 14:22', level: 'high' },
    { title: '점검 미이행 항목', time: '어제 17:05', level: 'mid' },
    { title: '예비품 재고 부족(벨트)', time: '3일 전', level: 'low' },
  ])
  
  /* 스냅샷 정의 */
  const metrics = [
    { key: 'temp', label: '온도' },
    { key: 'humid', label: '습도' },
    { key: 'power', label: '전력' },
    { key: 'dust', label: '분진' },
    { key: 'vocs', label: 'VOCs' },
    { key: 'torque', label: '토크', proc: '믹싱' },
    { key: 'thickness', label: '두께', proc: '압출' },
    { key: 'length', label: '길이', proc: '압출' },
    { key: 'tension', label: '장력', proc: '성형' },
    { key: 'pressure', label: '압력', proc: '가류' },
  ]
  function isMetricVisible(m) { return !m.proc || m.proc === proc.value }
  function labelOf(key) { const m = metrics.find(x => x.key === key); return m ? m.label : key }
  
  /* 시간 단위(축 제목용) */
  function timeUnit(scale){
    return scale === 'minute' ? '분' : (scale === 'hour' ? '시' : '일')
  }
  
  /* 선택 & 차트 */
  const selectedMetric = ref('temp')
  const chartScale = ref('day')
  const chartEl = ref(null)
  let metricChart = null
  
  /* 시간창 스크롤 상태 */
  const rangeOffset = ref(0)     // 현재 창의 시작 인덱스
  const rangeMax    = ref(0)     // 움직일 수 있는 최대치(총길이-창길이)
  
  /* 색상 맵(항목별 그래프 색) */
  const metricColor = {
    temp:'#ef4444', humid:'#3b82f6', power:'#8b5cf6', dust:'#64748b', vocs:'#10b981',
    torque:'#f59e0b', thickness:'#06b6d4', length:'#84cc16', tension:'#14b8a6', pressure:'#6366f1'
  }
  
  function onPickMetric(m) {
    if (!isMetricVisible(m)) return
    selectedMetric.value = m.key
    updateChartForMetric()
  }
  
  /* 더미 장비 & 차트 초기화 */
  onMounted(async () => {
    equipmentOptions.value = [
      { id: 'eq-01', name: '믹싱기 A', status: '가동', state: '정상', temp: 70, humid: 42, power: 11.2, dust: 0.15, vocs: 0.03, torque: 50 },
      { id: 'eq-02', name: '압출기 A', status: '가동', state: '정상', temp: 65, humid: 40, power: 9.3, dust: 0.03, vocs: 0.02, thickness: 2.5, length: 120 },
      { id: 'eq-03', name: '성형기 A', status: '정지', state: '점검 중', temp: 30, humid: 60, power: 5.2, dust: 0.2, vocs: 0.05, tension: 18 },
      { id: 'eq-04', name: '가류기 A', status: '가동', state: '정상', temp: 120, humid: 35, power: 15.4, dust: 0.1, vocs: 0.01, pressure: 5.6 }
    ]
    selEqId.value = 'eq-01'
    await nextTick()
    buildChart()
    updateChartForMetric()
  })
  
  /* 상태 뱃지 */
  const statusClass = s => ({
    '가동': 'bg-green-50 text-green-700 border-green-200',
    '정지': 'bg-gray-50 text-gray-700 border-gray-200',
    '점검': 'bg-yellow-50 text-yellow-700 border-yellow-200',
  }[s] || 'bg-gray-50 text-gray-700 border-gray-200')
  
  /* 모달 */
  function openHistory() {
    modalTitle.value = '정비 이력'
    modalType.value = 'history'
    modalData.value = [
      { date: '2025-09-10', desc: '정기점검 완료' },
      { date: '2025-08-20', desc: '모터 교체' },
      { date: '2025-07-05', desc: '베어링 윤활 작업' },
      { date: '2025-06-12', desc: '온도 센서 교정' },
    ]
    showModal.value = true
  }
  function closeModal() { showModal.value = false }
  function ackAll() { alarms.value = []; modalTitle.value = '알림'; modalType.value = 'ack'; modalData.value = []; showModal.value = true }
  
  /* 차트 */
  function onChangeScale(next) {
    chartScale.value = next
    // 스케일 바뀌면 스크롤 위치 초기화
    rangeOffset.value = 0
    updateChartForMetric()
  }
  
  function buildChart() {
    if (!chartEl.value) return
    if (metricChart) { metricChart.destroy(); metricChart = null }
    metricChart = new Chart(chartEl.value, {
      type: 'line',
      data: {
        labels: [],
        datasets: [{
          label: labelOf(selectedMetric.value),     // 초기에도 한글 항목명
          data: [],
          fill: false,
          borderWidth: 2,
          tension: 0.2
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: { legend: { display: true } },
        interaction: { mode: 'index', intersect: false },
        scales: {
          x: { title: { display: true, text: timeUnit(chartScale.value) } },  // 분/시/일
          y: { title: { display: true, text: labelOf(selectedMetric.value) }, // 온도/분진 등
               beginAtZero: false }
        }
      }
    })
  }
  
  function getWindowLen(scale){ return scale==='minute'?60:scale==='hour'?24:30 }
  function getTotalLen(scale){  return getWindowLen(scale) * 3 } // 예: 3배 길이(슬라이드 용 더미)
  
  import axios from 'axios'

async function updateChartForMetric() {
  if (!metricChart) return

  try {
    const res = await axios.get(`/api/equipment/${selEqId.value}/metrics`, {
      params: {
        metric: selectedMetric.value,  // ← 여기가 핵심! 온도/습도/전력 등 선택 항목
        scale: chartScale.value         // minute/hour/day
      }
    })

    const apiData = res.data
    const labels = apiData.map(d => d.label)
    const data   = apiData.map(d => d.value)
    const color  = metricColor[selectedMetric.value] ?? '#2563eb'

    metricChart.data.labels = labels
    metricChart.data.datasets[0].label = labelOf(selectedMetric.value)
    metricChart.data.datasets[0].data  = data
    metricChart.data.datasets[0].borderColor = color
    metricChart.data.datasets[0].pointBackgroundColor = color

    metricChart.options.scales.x.title.text = timeUnit(chartScale.value)
    metricChart.options.scales.y.title.text = labelOf(selectedMetric.value)

    metricChart.update()
  } catch (err) {
    console.error("차트 데이터 불러오기 실패", err)
  }
}

  
  /* 시간축 이동 버튼 */
  function goStart(){ rangeOffset.value = 0; updateChartForMetric() }
  function goEnd(){ rangeOffset.value = rangeMax.value; updateChartForMetric() }
  function goPrev(){
    const step = Math.ceil(getWindowLen(chartScale.value) * 0.2)
    rangeOffset.value = Math.max(0, rangeOffset.value - step)
    updateChartForMetric()
  }
  function goNext(){
    const step = Math.ceil(getWindowLen(chartScale.value) * 0.2)
    rangeOffset.value = Math.min(rangeMax.value, rangeOffset.value + step)
    updateChartForMetric()
  }
  
  /* 라벨/시계열(전체 길이) */
  function makeFullLabels(scale, totalLen) {
    const unit = scale==='minute'?'분':(scale==='hour'?'시':'일')
    return Array.from({ length: totalLen }, (_, i) => `${i+1}${unit}`)
  }
  function makeFullSeries(scale, metricKey, totalLen) {
    const base = (() => {
      switch (metricKey) {
        case 'temp': return 60
        case 'humid': return 40
        case 'power': return 10
        case 'dust': return 0.1
        case 'vocs': return 0.05
        case 'torque': return 80
        case 'thickness': return 2.5
        case 'length': return 120
        case 'tension': return 15
        case 'pressure': return 5
        default: return 10
      }
    })()
    return Array.from({ length: totalLen }, (_, i) =>
      +(base + (Math.sin(i / 3) * base * 0.1) + (Math.random() - 0.5) * base * 0.05).toFixed(2)
    )
  }
  
  /* 공정/설비 바뀌면 차트 갱신 */
  watch([proc, selEqId], () => updateChartForMetric())
  </script>
  
  <style scoped>
  /* 카드 여백 살짝 축소 */
  .card { @apply border rounded-md p-3 md:p-4 bg-white shadow-sm h-full; }
  .section-title { @apply text-lg font-semibold mb-1; }
  .btn { @apply px-3 py-1.5 rounded text-sm; }
  .mini-box { @apply border rounded p-3; }
  
  /* 스냅샷 버튼(높이 수정) */
  .snapshot-btn { @apply border rounded py-1 px-1.5 bg-white text-left hover:bg-gray-50 transition h-9; }
  
  /* 빠른 작업 버튼(높이 수정) */
  .quick-btn { @apply px-3 py-1.5 text-sm; }
  </style>