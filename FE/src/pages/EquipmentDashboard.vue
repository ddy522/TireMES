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
            <select v-model="proc" class="border rounded px-2 py-1.5" @change="loadEquipmentOptions">
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
              <span class="px-1.5 py-0.5 rounded text-xs border bg-green-50 text-green-700 border-green-200">
                가동중
              </span>
            </div>
            <div class="text-gray-500">상태</div><div>정상</div>
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
            <button class="btn quick-btn bg-white border col-span-2" @click="openRealtimeInfo">실시간 정보</button>
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
  import axios from 'axios'

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

  async function loadAlarms() {
  try {
    const res = await axios.get('/api/alarms')  // 스프링 부트 API 호출
    alarms.value = res.data
  } catch (err) {
    console.error('알림 로드 실패', err)
    alarms.value = []
  }
}

// 컴포넌트가 마운트될 때 호출
onMounted(() => {
  loadAlarms()
})

  /* 스냅샷 정의 - 순서를 온도, 습도, 전압, 전력로 변경 */
  const metrics = [
  { key: 'DW1', label: '온도', proc: null },      // 공통 - 1순위
  { key: 'DW2', label: '습도', proc: null },      // 공통 - 2순위
  { key: 'DW0', label: '전압', proc: null },      // 공통 - 3순위
  { key: 'DW3', label: '전력', proc: null },      // 공통 - 4순위
  { key: 'DW4', label: '분진', proc: null },      // 공통
  { key: 'DW5', label: 'VOCs', proc: null },     // 공통
  { key: 'DW6', label: '토크', proc: '믹싱' },
  { key: 'DW7', label: '두께', proc: '압출' },
  { key: 'DW8', label: '길이', proc: '재단' },
  { key: 'DW9', label: '장력', proc: '성형' },
  { key: 'DW10', label: '압력', proc: '가류' },
  ]
  function isMetricVisible(m) { return !m.proc || m.proc === proc.value }
  function labelOf(key) { const m = metrics.find(x => x.key === key); return m ? m.label : key }

  /* 시간 단위(축 제목용) */
  function timeUnit(scale){
  return scale === 'minute' ? '분' : (scale === 'hour' ? '시' : '일')
  }

  /* 선택 & 차트 */
  const selectedMetric = ref('DW1')  // 기본값을 DW1(온도)로 변경
  const chartScale = ref('minute')
  const chartEl = ref(null)
  let metricChart = null

  /* 시간창 스크롤 상태 */
  const currentHour = new Date().getHours()
  const rangeOffset = ref(currentHour) // 초기값을 현재 시간으로 직접 설정
  const rangeMax    = ref(0)     // 움직일 수 있는 최대치(총길이-창길이)
  const isInitialLoad = ref(true) // 초기 로드 여부를 추적

  /* 색상 맵(DW별 그래프 색) */
  const metricColor = {
  DW0:'#9333ea',   // 전압 - 보라색
  DW1:'#ef4444',   // 온도 - 빨간색
  DW2:'#3b82f6',   // 습도 - 파란색
  DW3:'#8b5cf6',   // 전력 - 자주색
  DW4:'#64748b',   // 분진 - 회색
  DW5:'#10b981',   // VOCs - 초록색
  DW6:'#f59e0b',   // 토크 - 주황색
  DW7:'#06b6d4',   // 두께 - 청록색
  DW8:'#84cc16',   // 길이 - 연두색
  DW9:'#14b8a6',   // 장력 - 민트색
  DW10:'#6366f1'   // 압력 - 인디고색
  }

  function onPickMetric(m) {
  if (!isMetricVisible(m)) return
  selectedMetric.value = m.key
  updateChartForMetric()
  }

  /* API로 설비 옵션 로드 */
  async function loadEquipmentOptions() {
  try {
    const machineName = proc.value + '기'  // 예: "믹싱" -> "믹싱기"
    console.log('Loading equipment for:', machineName)
    
    const response = await axios.get('/api/machine/code', {
      params: { machineName: machineName }
    })
    
    console.log('API Response:', response.data) // 디버깅용 로그 추가
    
    // API 응답이 성공하면 equipmentOptions에 추가
    if (response.data && response.data.machineCode) {
      equipmentOptions.value = [
        { 
          id: response.data.machineCode, 
          name: response.data.machineCode  // machineCode를 name으로 사용
        }
      ]
      
      console.log('Equipment Options:', equipmentOptions.value) // 디버깅용 로그 추가
      
      // 첫 번째 설비를 자동 선택
      if (equipmentOptions.value.length > 0) {
        selEqId.value = equipmentOptions.value[0].id
        console.log('Selected Equipment ID:', selEqId.value) // 디버깅용 로그 추가
      }
    }
  } catch (error) {
    console.error('설비 정보 로드 실패:', error)
    // API 실패 시 빈 배열로 설정
    equipmentOptions.value = []
    selEqId.value = ''
  }
  }

  /* 더미 장비 & 차트 초기화 */
  onMounted(async () => {
  await loadEquipmentOptions() // 초기 설비 정보 로드
  await nextTick()
  buildChart()
  updateChartForMetric()

  // 타임라인 바 강제 업데이트
  setTimeout(() => {
    const timelineInput = document.querySelector('input[type="range"]')
    if (timelineInput && chartScale.value === 'minute') {
      timelineInput.value = rangeOffset.value
    }
  }, 100)
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

  function openRealtimeInfo() {
  if (!selEqId.value) {
    alert('설비를 먼저 선택해주세요.')
    return
  }

  const url = `http://192.168.203.84:11005/?Pro=group2#${selEqId.value}`

  // 팝업 창 옵션 설정
  const popupOptions = [
    'width=525',       // 창 너비
    'height=310',      // 창 높이
    'scrollbars=yes',  // 스크롤바 표시
    'resizable=yes',   // 크기 조정 가능
    'menubar=no',      // 메뉴바 숨김
    'toolbar=no',      // 툴바 숨김
    'location=no',     // 주소표시줄 숨김
    'status=no'        // 상태표시줄 숨김
  ].join(',')

  // 새 창에서 열기
  window.open(url, 'realtimeInfo', popupOptions)
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

  async function updateChartForMetric() {
  if (!metricChart) return
  if (!selEqId.value) {
    console.warn("설비가 선택되지 않았습니다.")
    return
  }

  try {
    console.log(`차트 데이터 요청 - EqId: ${selEqId.value}, metric: ${selectedMetric.value}, scale: ${chartScale.value}`)
    
    const res = await axios.get(`/api/equipment/${selEqId.value}/metrics`, {
      params: {
        metric: selectedMetric.value,  // DW0, DW1, DW2 등
        scale: chartScale.value         // minute/hour/day
      }
    })

    // API에서 모든 gubun 데이터가 오므로 선택된 metric(gubun)만 필터링
    const apiData = res.data
    let filteredData = apiData.filter(d => d.gubun === selectedMetric.value)
    
    let labels = []
    let data = []
    
    if (chartScale.value === 'minute') {
      // 분단위: 24시간으로 타임라인 설정 (00:00 ~ 23:59)
      labels = Array.from({ length: 24 }, (_, i) => {
        return String(i).padStart(2, '0') + ':00'
      })
      
      // 시간별로 60개의 분단위 데이터를 준비
      const hourlyData = new Array(24).fill(0).map(() => new Array(60).fill(0))
      
      // API 데이터를 시간별로 분류
      filteredData.forEach(d => {
        const timeMatch = d.label.match(/(\d{2}):(\d{2})/)
        if (timeMatch) {
          const hour = parseInt(timeMatch[1])
          const minute = parseInt(timeMatch[2])
          if (hour >= 0 && hour < 24 && minute >= 0 && minute < 60) {
            hourlyData[hour][minute] = d.value || 0
          }
        }
      })
      
      // rangeMax를 23으로 설정 (0~23시)
      rangeMax.value = 23
      
      // 분단위 모드에서 처음 로드될 때만 현재 시간으로 설정
      if (isInitialLoad.value && chartScale.value === 'minute') {
        const currentHour = new Date().getHours()
        rangeOffset.value = currentHour
        isInitialLoad.value = false
      }
      
      // 현재 rangeOffset에 따라 해당 시간의 60개 데이터를 표시
      const currentHour = Math.floor(rangeOffset.value)
      if (currentHour >= 0 && currentHour < 24) {
        // 해당 시간의 분단위 라벨 생성 (00:00 ~ 00:59)
        labels = Array.from({ length: 60 }, (_, i) => {
          const hour = String(currentHour).padStart(2, '0')
          const minute = String(i).padStart(2, '0')
          return `${hour}:${minute}`
        })
        data = hourlyData[currentHour]
      }
      
    } else if (chartScale.value === 'hour') {
      // 시간 단위는 최근 24시간
      const today = new Date().toISOString().split('T')[0]
      filteredData = filteredData.filter(d => {
        return d.label.includes(today) || d.label.length <= 5
      })
      
      // 24시간 데이터 준비
      const hourlyMap = new Map()
      filteredData.forEach(d => {
        hourlyMap.set(d.label, d.value || 0)
      })
      
      labels = Array.from({ length: 24 }, (_, i) => {
        return String(i).padStart(2, '0') + ':00'
      })
      
      data = labels.map(label => hourlyMap.get(label) || 0)
      
    } else if (chartScale.value === 'day') {
      // 일 단위는 최근 30일
      const thirtyDaysAgo = new Date()
      thirtyDaysAgo.setDate(thirtyDaysAgo.getDate() - 30)
      const cutoffDate = thirtyDaysAgo.toISOString().split('T')[0]
      
      filteredData = filteredData.filter(d => d.label >= cutoffDate)
      
      // 30일 데이터 준비
      const dailyMap = new Map()
      filteredData.forEach(d => {
        dailyMap.set(d.label, d.value || 0)
      })
      
      labels = []
      const currentDate = new Date()
      for (let i = 29; i >= 0; i--) {
        const date = new Date(currentDate)
        date.setDate(date.getDate() - i)
        const dateStr = date.toISOString().split('T')[0]
        labels.push(dateStr)
      }
      
      data = labels.map(label => dailyMap.get(label) || 0)
    }
    
    const color = metricColor[selectedMetric.value] ?? '#2563eb'

    metricChart.data.labels = labels
    metricChart.data.datasets[0].label = labelOf(selectedMetric.value)
    metricChart.data.datasets[0].data = data
    metricChart.data.datasets[0].borderColor = color
    metricChart.data.datasets[0].pointBackgroundColor = color

    metricChart.options.scales.x.title.text = timeUnit(chartScale.value)
    metricChart.options.scales.y.title.text = labelOf(selectedMetric.value)

    metricChart.update()
    
    console.log(`차트 업데이트 완료 - 데이터 개수: ${data.length}`)
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

  /* 공정 바뀌면 설비 옵션 다시 로드 */
  watch(proc, () => {
  loadEquipmentOptions()
  })

  /* 설비 바뀌면 차트 갱신 */
  watch(selEqId, () => updateChartForMetric())
  </script>

  <style scoped>
  /* 카드 여백 살짝 축소 */
  .card { @apply border rounded-md p-3 md:p-4 bg-white shadow-sm h-full; }
  .section-title { @apply text-lg font-semibold mb-1; }
  /* .btn { @apply px-3 py-1.5 rounded text-sm; } */
  .mini-box { @apply border rounded p-3; }

  /* 스냅샷 버튼(높이 수정) */
  /* .snapshot-btn { @apply border rounded py-1 px-1.5 bg-white text-left hover:bg-gray-50 transition h-9; } */

  /* 빠른 작업 버튼(높이 수정) */
  /* .quick-btn { @apply px-3 py-1.5 text-sm; } */

  /* 스냅샷 버튼 높이 조정 */
.snapshot-btn { 
  @apply border rounded py-4 px-2.5 bg-white text-left hover:bg-gray-50 transition; 
}

/* 빠른 작업 버튼 높이 조정 */
.quick-btn { 
  @apply px-3 py-4 text-sm; 
}

/* 정비 이력 / 실시간 정보 버튼도 높이 조절 가능 */
.btn {
  @apply px-3 py-4 rounded text-sm; /* 기존 py-1.5 -> py-2로 살짝 증가 */
}
  </style>