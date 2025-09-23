<template>
  <div class="p-4 space-y-4">
    <!-- Header -->
    <div class="flex items-center justify-between">
        <div class="text-3xl md:text-4xl font-semibold text-gray-900 tabular-nums leading-none">
            {{ nowFmt }}
        </div>
        <!-- <div class="text-sm text-gray-600 font-medium tabular-nums">
            {{ nowFmt }}
        </div> -->
      <div class="flex items-center gap-2">
        <StatusPill :ok="systemOk" />
        <button class="px-3 py-2 text-sm border rounded-md" @click="refreshAll" :disabled="loading">
          새로고침
        </button>
      </div>
    </div>

    <!-- KPI Cards -->
    <div class="grid grid-cols-1 md:grid-cols-4 gap-3">
      <KpiCard title="일일 생산량" :value="fmt(num.todayOutput)" :sub="trend(num.todayOutputDelta)" />
      <KpiCard title="품질 합격률" :value="fmtRate(num.yieldRate)" :sub="vsTarget(num.yieldVsTarget)" />
      <KpiCard title="설비 가동률" :value="fmtRate(num.utilRate)" :sub="trend(num.utilRateDelta, '평균 대비')" />
      <KpiCard title="납기 준수율" :value="fmtRate(num.otd)" :sub="trend(num.otdDelta, '전월 대비')" />
    </div>

    <div class="grid grid-cols-1 lg:grid-cols-3 gap-4">
      <!-- Plan vs Actual (ECharts) -->
      <Card title="계획 대비 실적 (월별)">
        <EBarLine :series="charts.planActual.series" :labels="charts.planActual.labels" />
      </Card>

      <!-- Defect Type (ECharts) -->
      <Card title="불량 유형 분석">
        <EPie :series="charts.defect.series" :labels="charts.defect.labels" />
      </Card>

      <!-- WIP by process -->
      <Card title="공정별 진행 현황 (WIP)">
        <ul class="divide-y">
          <li v-for="row in wip" :key="row.process" class="flex items-center justify-between py-2">
            <div class="flex items-center gap-3">
              <ProcessDot :name="row.process" />
              <div>
                <div class="text-sm font-medium">{{ row.process }}</div>
                <div class="text-xs text-gray-500">대기 {{ row.queue }} · 진행 {{ row.running }} · 완료 {{ row.done }}</div>
              </div>
            </div>
            <div class="w-40">
              <StackBar :a="row.running" :b="row.queue" :total="row.running + row.queue + 0.0001" />
            </div>
          </li>
        </ul>
      </Card>
    </div>

    <div class="grid grid-cols-1 lg:grid-cols-3 gap-4">
      <!-- Equipment OEE -->
      <Card title="설비 OEE / 알람">
        <div class="space-y-2">
          <div v-for="eq in equipment" :key="eq.code" class="flex items-center justify-between border rounded-lg p-3">
            <div>
              <div class="text-sm font-semibold">{{ eq.name }}</div>
              <div class="text-xs text-gray-500">가동률 {{ fmtRate(eq.util) }} · 품질 {{ fmtRate(eq.quality) }} · 성능 {{ fmtRate(eq.performance) }}</div>
            </div>
            <div class="flex items-center gap-3">
              <OeeBadge :value="eq.oee" />
              <AlarmDot :count="eq.alarms" />
            </div>
          </div>
        </div>
      </Card>

      <!-- Material Shortage -->
        <Card title="자재 재고 현황">
            <MaterialsTable />
        </Card>

      <!-- Alerts -->
      <Card title="알림 및 경고">
        <ul class="space-y-2">
          <li v-for="a in alerts" :key="a.id" class="flex items-start gap-3 p-3 border rounded-lg">
            <span class="mt-1 inline-block w-2 h-2 rounded-full" :class="a.level==='HIGH' ? 'bg-red-500' : a.level==='MID' ? 'bg-yellow-500' : 'bg-gray-400'" />
            <div>
              <div class="text-sm font-medium">{{ a.title }}</div>
              <div class="text-xs text-gray-500">{{ a.when }} · {{ a.source }}</div>
            </div>
            <button class="ml-auto text-xs text-blue-600" @click="ack(a.id)">확인</button>
          </li>
        </ul>
      </Card>
    </div>
  </div>
</template>

<script setup>
import { defineComponent, h, onMounted, onBeforeUnmount, reactive, ref, watch, nextTick,computed } from 'vue'
import * as echarts from 'echarts' // 순정 ECharts 사용
import MaterialsTable from '../components/materials/InventoryShortageTable.vue'
import { searchWorkOrders } from '../api/workOrderListSearch.js'

// ===== 현재 시간 표시 =====
// ===== 현재 시간 표시 =====
const now = ref(new Date())
let clockTimer // number | undefined

onMounted(() => {
  clockTimer = window.setInterval(() => {
    now.value = new Date()
  }, 1000)
})
onBeforeUnmount(() => {
  if (clockTimer) window.clearInterval(clockTimer)
})

const nowFmt = computed(() => {
  const d = now.value
  const days = ['일','월','화','수','목','금','토']
  const pad = (n) => String(n).padStart(2, '0')
  return `${d.getFullYear()}-${pad(d.getMonth()+1)}-${pad(d.getDate())} (${days[d.getDay()]}) `
       + `${pad(d.getHours())}:${pad(d.getMinutes())}:${pad(d.getSeconds())}`
})

/* ===== Utilities ===== */
const fmt = (n) => new Intl.NumberFormat().format(Math.round(n || 0))
const fmtRate = (x) => (x == null ? '-' : `${(x*100).toFixed(1)}%`)
const trend = (delta, label = '전일 대비') => {
  if (delta == null) return ''
  const sign = delta >= 0 ? '+' : ''
  return `${sign}${(delta*100).toFixed(1)}% ${label}`
}
const vsTarget = (delta) => {
  if (delta == null) return ''
  const sign = delta >= 0 ? '+' : ''
  return `${sign}${(delta*100).toFixed(1)}% 목표 대비`
}

/* ===== State ===== */
const loading = ref(false)
const systemOk = ref(true)

const num = reactive({
  todayOutput: 0,
  todayOutputDelta: 0.0,
  yieldRate: 0.0,
  yieldVsTarget: 0.0,
  utilRate: 0.0,
  utilRateDelta: 0.0,
  otd: 0.0,
  otdDelta: 0.0,
})

const charts = reactive({
  planActual: { labels: [], series: [] },
  defect: { labels: [], series: [] },
})

const wip = ref([])         // ✅ 실제 집계 결과가 들어감
const equipment = ref([])
const alerts = ref([])

/* ===== 공정별 WIP 집계 ===== */
// 표기명/영문코드 매핑
const PROCESS_DEFS = [
  { eng: 'mixing',    ko: '믹싱' },
  { eng: 'extrusion', ko: '압출' },
  { eng: 'molding',   ko: '성형' },
  { eng: 'curing',    ko: '가류' },
  { eng: 'inspection',ko: '검사' },
]

// API 응답 안전 추출
function pickData(res) {
  if (!res) return []
  if (Array.isArray(res)) return res
  if (Array.isArray(res.data)) return res.data
  return res.data?.content ?? res.data ?? []
}

// 단일 공정 집계
async function fetchWipFor(proc) {
  try {
    // 상태 미지정 → 해당 공정 전체 조회
    const res  = await searchWorkOrders({ processNameEng: proc.eng })
    const list = pickData(res)
    let queue = 0, running = 0, done = 0
    for (const o of list) {
      const s = Number(o.workStatus)
      if (s === 0) queue++
      else if (s === 1) running++
      else if (s === 2) done++
    }
    return { process: proc.ko, queue, running, done }
  } catch (e) {
    console.error('WIP 집계 실패:', proc.eng, e)
    return { process: proc.ko, queue: 0, running: 0, done: 0 }
  }
}

// 전체 공정 집계
async function loadWipSummary() {
  const rows = await Promise.all(PROCESS_DEFS.map(fetchWipFor))
  wip.value = rows
}

/* ===== 데모/기타 데이터 ===== */
function mockFetch() {
  // --- KPIs ---
  num.todayOutput = 2847
  num.todayOutputDelta = 0.125
  num.yieldRate = 0.987
  num.yieldVsTarget = 0.003
  num.utilRate = 0.942
  num.utilRateDelta = -0.012
  num.otd = 0.961
  num.otdDelta = 0.006

  // --- Charts data ---
  charts.planActual.labels = ['1월','2월','3월','4월','5월','6월']
  charts.planActual.series = [
    { name: '계획', data: [8000,8200,8600,9000,8800,9100] },
    { name: '실적', data: [7900,8400,8700,9200,8600,9300] },
    { name: '달성(%)', data: [98.7,102.4,101.2,102.2,97.7,102.2] }
  ]

  charts.defect.labels = ['표면 결함','치수 불량','균열','강도 불량','기타']
  charts.defect.series = [35,28,12,20,5]

  equipment.value = [
    { code:'EQ-01', name:'가류기-01', util:0.93, performance:0.95, quality:0.99, oee:0.87, alarms:1 },
    { code:'EQ-02', name:'가류기-02', util:0.88, performance:0.92, quality:0.98, oee:0.79, alarms:3 },
    { code:'EQ-03', name:'성형기-01', util:0.95, performance:0.96, quality:0.985, oee:0.90, alarms:0 },
  ]

  alerts.value = [
    { id:1, level:'HIGH', title:'EQ-02 온도 편차 초과 (±5℃)', source:'설비/가류', when:'5분 전' },
    { id:2, level:'MID', title:'검사 불량률 상승 (주간 +1.2%)', source:'품질/검사', when:'12분 전' },
    { id:3, level:'LOW', title:'자재 RB-1001 부족 예상 (2일 내)', source:'자재/계획', when:'1시간 전' },
  ]
}

const refreshAll = async () => {
  loading.value = true
  await new Promise(r => setTimeout(r, 200))
  mockFetch()
  await loadWipSummary()     // ✅ 실제 WIP 반영
  loading.value = false
}

const ack = (id) => { alerts.value = alerts.value.filter(a=>a.id!==id) }

onMounted(refreshAll)

/* ===== ECharts Composables ===== */
function useEchart(getOption) {
  const elRef = ref(null)
  let chart = null
  const init = () => {
    if (!elRef.value) return
    chart = echarts.init(elRef.value)
    chart.setOption(getOption(), true)
  }
  const resize = () => chart && chart.resize()
  onMounted(async ()=>{ await nextTick(); init(); window.addEventListener('resize', resize) })
  onBeforeUnmount(()=>{ window.removeEventListener('resize', resize); if(chart){ chart.dispose(); chart=null } })
  const set = (opt) => chart && chart.setOption(opt, true)
  return { elRef, set }
}

/* ===== ECharts Components ===== */
const EBarLine = defineComponent({
  name:'EBarLine', props:{ series:Array, labels:Array },
  setup(props){
    const makeOpt = () => {
      const labels = props.labels || []
      const plan   = props.series?.[0]?.data || []
      const actual = props.series?.[1]?.data || []
      const achieve= props.series?.[2]?.data || []
      return {
        tooltip:{ trigger:'axis' },
        legend:{ data:['계획','실적','달성(%)'] },
        grid:{ left:40, right:40, top:40, bottom:30 },
        xAxis:{ type:'category', data: labels },
        yAxis:[ { type:'value', name:'수량' }, { type:'value', name:'%', min:0, max:120 } ],
        series:[
          { name:'계획', type:'bar', data: plan, barWidth: 14 },
          { name:'실적', type:'bar', data: actual, barWidth: 14 },
          { name:'달성(%)', type:'line', data: achieve, yAxisIndex:1, smooth:true }
        ]
      }
    }
    const { elRef, set } = useEchart(makeOpt)
    watch(()=>[props.series, props.labels], ()=> set(makeOpt()), { deep:true })
    return () => h('div', { ref: elRef, style:'width:100%;height:320px;' })
  }
})

const EPie = defineComponent({
  name:'EPie', props:{ series:Array, labels:Array },
  setup(props){
    const makeOpt = () => {
      const data = (props.series||[]).map((v,i)=>({ name: props.labels?.[i]||`항목${i+1}`, value: v }))
      return {
        tooltip:{ trigger:'item', formatter:'{b}: {c}건 ({d}%)' },
        legend:{ top:0, left:'center' },
        series:[{ type:'pie', radius:['45%','75%'], center:['50%','60%'], data }]
      }
    }
    const { elRef, set } = useEchart(makeOpt)
    watch(()=>[props.series, props.labels], ()=> set(makeOpt()), { deep:true })
    return () => h('div', { ref: elRef, style:'width:100%;height:260px;' })
  }
})

/* ===== Local UI Components ===== */
const Card = defineComponent({
  name:'Card', props:{ title:String },
  setup(props, { slots }){
    return () => h('div', { class:'bg-white border rounded-xl p-4 shadow-sm' }, [
      h('div', { class:'text-sm font-semibold mb-3' }, props.title),
      slots.default?.()
    ])
  }
})

const KpiCard = defineComponent({
  name:'KpiCard', props:{ title:String, value:[String,Number], sub:String },
  setup(p){
    return () => h('div', { class:'bg-white border rounded-xl p-4 shadow-sm' }, [
      h('div', { class:'text-xs text-gray-500' }, p.title),
      h('div', { class:'mt-1 text-2xl font-bold' }, p.value ?? '-'),
      p.sub ? h('div', { class:'mt-1 text-xs text-gray-500' }, p.sub) : null,
    ])
  }
})

const StatusPill = defineComponent({
  name:'StatusPill', props:{ ok:Boolean },
  setup(p){
    return () => h('span', { class:`px-2 py-1 text-xs rounded-full ${p.ok?'bg-emerald-50 text-emerald-700':'bg-red-50 text-red-700'} border` }, p.ok?'시스템 정상':'이상 감지')
  }
})

const ProcessDot = defineComponent({
  name:'ProcessDot', props:{ name:String },
  setup(p){
    const color = ({ '믹싱':'bg-indigo-500', '압출':'bg-blue-500', '성형':'bg-purple-500', '가류':'bg-amber-500', '검사':'bg-emerald-500' }[p.name]) || 'bg-gray-400'
    return () => h('span', { class:`inline-block w-3 h-3 rounded-full ${color}` })
  }
})

const StackBar = defineComponent({
  name:'StackBar', props:{ a:Number, b:Number, total:Number },
  setup(p){
    const aPct = Math.min(100, Math.max(0, (p.a/(p.total||1))*100))
    const bPct = Math.max(0, 100 - aPct)
    return () => h('div', { class:'w-full h-2 bg-gray-100 rounded-full overflow-hidden flex' }, [
      h('div', { class:'h-full bg-emerald-500', style:{ width: aPct+'%' } }),
      h('div', { class:'h-full bg-gray-300', style:{ width: bPct+'%' } }),
    ])
  }
})

const OeeBadge = defineComponent({
  name:'OeeBadge', props:{ value:Number },
  setup(p){
    const cls = p.value>=0.85? 'bg-emerald-50 text-emerald-700' : p.value>=0.75? 'bg-yellow-50 text-yellow-700':'bg-red-50 text-red-700'
    return () => h('span', { class:`px-2 py-1 text-xs rounded-md border ${cls}` }, `OEE ${Math.round((p.value||0)*100)}%`)
  }
})

const AlarmDot = defineComponent({
  name:'AlarmDot', props:{ count:Number },
  setup(p){
    const color = p.count>2? 'bg-red-500' : p.count>0? 'bg-yellow-500':'bg-gray-300'
    return () => h('div', { class:'flex items-center gap-1' }, [
      h('span', { class:`inline-block w-2 h-2 rounded-full ${color}` }),
      h('span', { class:'text-xs text-gray-500' }, `${p.count}건`)
    ])
  }
})
</script>