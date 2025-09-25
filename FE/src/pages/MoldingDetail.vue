<template>
    <div class="space-y-6">
      <!-- 상단: 뒤로가기 + 제목 -->
      <div class="flex items-center gap-2 text-sm text-gray-500">
        <RouterLink to="/extrusion" class="hover:underline">◀ 뒤로가기</RouterLink>
        <h1 class="text-xl font-bold text-gray-900">성형 작업화면</h1>
        <span class="ml-auto text-xs text-blue-600 bg-blue-50 border border-blue-200 rounded px-2 py-0.5">실시간 작업</span>
      </div>
  
      <section class="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <!-- 좌측 컬럼: 작업지시 정보 -->
        <div class="space-y-6">
          <WorksheetInfoCard 
            :worksheet-info="worksheetInfo" 
            :loading="loading" 
            :error="error" 
            process="05"
          />
        </div>
        <!-- 좌측 컬럼 end -->
  
        <!-- 우측 컬럼: 생산완료처리 + LOT 관리 -->
        <div class="space-y-6">
          <ProductionCompleteForm
            v-model:doneQty="doneQty"
            v-model:remark="remark"
            v-model:worksheetSkey="worksheetSkey"
            @submit="handleComplete"
          />
          
  
          <!-- LOT 관리 시스템 -->
          <div class="card">
            <div class="flex items-center justify-between mb-2">
              <h3 class="section-title">LOT 관리 시스템</h3>
              <div class="text-xs text-gray-500">작업지시 {{ id }}의 LOT 현황 및 관리</div>
            </div>
  
            <!-- 25.09..17 하도이 lot 발행 주석처리  -->
            <!-- <LotCreateForm @create="createLot" /> -->
  
            <LotList 
              :lots="lots" 
              ref="lotListRef"
              v-model:worksheetSkey="worksheetSkey" 
              class="mt-4" 
            />
          </div>
        </div>
        <!-- 우측 컬럼 end -->
      </section>
    </div>
  </template>
  
  <script setup>
  import { ref, computed, onMounted, watch } from 'vue'
  import { useRoute } from 'vue-router'
  import LotCreateForm from '../components/LotCreateForm.vue'
  import LotList from '../components/LotList.vue'
  import ProductionCompleteForm from '../components/ProductionCompleteForm.vue'
  import WorksheetInfoCard from '../components/WorksheetInfoCard.vue'
  
  const route = useRoute()
  const id = route.params.id
  
  // 생산완료처리
  const doneQty = ref(0)
  const quality = ref('합격')
  const remark = ref('')
  
  const lotListRef = ref(null)
  
  // 상태 관리
  const loading = ref(false)
  const error = ref('')
  const worksheetInfo = ref(null)
  const worksheetSkey = computed(() => worksheetInfo.value?.worksheetSkey || '')
  
  
  // LOT 관리
  const lots = ref([
    { no: 'LOT-W0001-001', qty: 25, createdAt: '14:30', status: '완료', currentProc: '재단', quality: '합격', history: ['믹싱', '압출'] },
    { no: 'LOT-W0001-002', qty: 30, createdAt: '15:15', status: '진행중', currentProc: '압출', quality: '검사중', history: ['믹싱'] },
    { no: 'LOT-W0001-003', qty: 20, createdAt: '15:45', status: '대기', currentProc: '믹싱', quality: '대기', history: ['믹싱'] },
  ])
  
  // function createLot(qty) {
  //   const idx = (lots.value.length + 1).toString().padStart(3, '0')
  //   lots.value.push({
  //     no: `LOT-W0001-${idx}`,
  //     qty,
  //     createdAt: new Date().toTimeString().slice(0,5),
  //     status: '대기',
  //     currentProc: '압출',
  //     quality: '대기',
  //     history: ['믹싱']
  //   })
  // }
  
  async function fetchWorksheetDetails() {
    try {
      loading.value = true
      error.value = ''
  
      const process = '05'  // 공정 코드
      const res = await fetch(`http://localhost:8080/api/mixing-detail/worksheet/${id}?process=${process}`)
      if (!res.ok) throw new Error(`작업지시서 정보를 가져올 수 없습니다: ${res.status}`)
  
      worksheetInfo.value = await res.json()
    } catch (err) {
      error.value = err.message
    } finally {
      loading.value = false
    }
  }
  
  
  function handleComplete() {
    console.log("작업 완료 처리 실행!")
    if (lotListRef.value && worksheetSkey.value) {
      lotListRef.value.fetchLots(worksheetSkey.value)
    } else {
      console.warn("LotList ref 또는 worksheetSkey가 아직 준비되지 않음")
    }
  }
  
  
  onMounted(() => {
    fetchWorksheetDetails()
  })
  </script>
  