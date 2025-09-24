<template>
  <div class="space-y-6">
    <!-- 상단: 뒤로가기 + 제목 -->
    <div class="flex items-center gap-2 text-sm text-gray-500">
      <RouterLink to="/mixing" class="hover:underline">◀ 뒤로가기</RouterLink>
      <h1 class="text-xl font-bold text-gray-900">믹싱 작업화면</h1>
      <span class="ml-auto text-xs text-blue-600 bg-blue-50 border border-blue-200 rounded px-2 py-0.5">
        원료 혼합 공정
      </span>
    </div>

    <section class="grid grid-cols-1 lg:grid-cols-2 gap-6">
      <!-- 좌측 컬럼: 작업지시 정보 / BOM 관리 -->
      <div class="space-y-6">
        <!-- 작업지시 정보 -->
        <div class="card">
          <h3 class="section-title">작업지시 정보</h3>
          <div v-if="loading && !worksheetInfo" class="text-center text-gray-500 py-4">작업지시 정보를 불러오는 중...</div>
          <div v-else-if="error && !worksheetInfo" class="text-center text-red-500 py-4">{{ error }}</div>
          <div v-else-if="worksheetInfo" class="flex items-center gap-4">
            <div class="w-20 h-20 bg-gray-100 rounded-md flex items-center justify-center">
              <span class="text-3xl">🛞</span>
            </div>
            <div class="text-sm">
              <div class="text-gray-500">작업지시번호</div>
              <div class="font-semibold text-gray-900">{{ worksheetInfo.workNo }}</div>
              <div class="mt-2 grid grid-cols-3 gap-4">
                <div>
                  <div class="text-gray-500">제품명</div>
                  <div class="font-medium">{{ worksheetInfo.partName }}</div>
                </div>
                <div>
                  <div class="text-gray-500">계획수량</div>
                  <div class="font-medium">{{ worksheetInfo.qty }}개</div>
                </div>
                <div>
                  <div class="text-gray-500">진행상태</div>
                  <span :class="getWorkStateClass(worksheetInfo.workState)">{{ getWorkStateText(worksheetInfo.workState) }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
        
        <!-- BOM 관리 -->
        <div class="card">
          <div class="flex items-center justify-between mb-2">
            <h3 class="section-title">BOM 관리 & 원자재 투입</h3>
            <div class="flex gap-2">
              <button @click="activeTab='bom'" :class="['btn', activeTab==='bom'?'btn-primary':'bg-gray-100 border']">BOM & 스캔</button>
              <button @click="activeTab='lots'" :class="['btn', activeTab==='lots'?'btn-primary':'bg-gray-100 border']">투입된 LOT</button>
            </div>
          </div>

          <div v-if="activeTab==='bom'">
            <LotScanInput 
              @add="onAddLot" 
              placeholder="원자재 LOT 번호를 스캔하세요 (예: MAT-003-LOT-001)"
              buttonText="투입"
            />
            <!-- API에서 가져온 BOM 데이터 표시 -->
            <div v-if="loading" class="text-center text-gray-500 py-4">BOM 정보를 불러오는 중...</div>
            <div v-else-if="error" class="text-center text-red-500 py-4">{{ error }}</div>
            <BomMaterialList v-else :materials="bomMaterials" />
          </div>

          <div v-else class="space-y-3">
            <div v-if="inputLots.length===0" class="text-center text-gray-500 py-4">투입된 원자재 LOT가 없습니다.</div>
            <div v-else>
              <div v-for="lot in inputLots" :key="lot.id" class="flex items-center justify-between border rounded p-2">
                <div>
                  <div class="font-mono font-medium">{{ lot.id }}</div>
                  <div class="text-sm text-gray-500">{{ lot.materialName }} ({{ lot.quantity }}kg)</div>
                  <div class="text-xs text-gray-400">투입시간: {{ lot.inputTime }}</div>
                </div>
                <button @click="removeLot(lot.id)" class="text-red-600 text-sm">삭제</button>
              </div>

              <!-- 합계 -->
              <div class="mt-3 grid grid-cols-2 text-sm border-t pt-2">
                <div>총 투입 LOT: {{ inputLots.length }}</div>
                <div>총 투입량: {{ totalQuantity }}kg</div>
              </div>
            </div>
          </div>
        </div>
      </div>
      <!-- 좌측 컬럼 end -->

      <!-- 우측 컬럼: 생산완료처리 + LOT 관리 -->
      <div class="space-y-6">
        <!-- 생산완료처리 -->
        <div class="card">
          <h3 class="section-title">생산완료처리</h3>
          <div class="flex gap-3 items-center">
            <input
              v-model.number="doneQty"
              type="number"
              placeholder="완료된 수량 입력"
              class="border rounded-md px-3 py-2 text-sm w-20"
            />
            <input
              v-model="remark"
              type="text"
              placeholder="특이사항이나 비고를 입력하세요"
              class="border rounded-md px-3 py-2 text-sm flex-1"
            />
            
            <button 
              @click="handleProductionComplete"
              :disabled="productionLoading || !doneQty"
              :class="['px-4 py-2 whitespace-nowrap', 
                      productionLoading || !doneQty 
                        ? 'bg-gray-300 text-gray-500 cursor-not-allowed' 
                        : 'btn-primary']"
            >
              {{ productionLoading ? '처리중...' : '발행' }}
            </button>

            <!-- 생산완료 처리 결과 메시지 (발행 버튼 div 아래에 추가) -->
            <div v-if="productionMessage" class="mt-2 p-2 text-sm rounded" 
                :class="productionError ? 'bg-red-50 text-red-600 border border-red-200' : 'bg-green-50 text-green-600 border border-green-200'">
              {{ productionMessage }}
            </div>
          </div>
        </div>

        <!-- LOT 관리 시스템 -->
        <div class="card">
          <div class="flex items-center justify-between mb-2">
            <h3 class="section-title">LOT 관리 시스템</h3>
            <div class="text-xs text-gray-500">작업지시 {{ id }}의 LOT 현황 및 관리</div>
          </div>

          <!-- 25.09.17 하도이 lot 등록 임시 주석 처리 -->
          <!-- <LotCreateForm @create="createLot" /> -->
          <LotList :lots="lots" class="mt-4" />
        </div>
      </div>
      <!-- 우측 컬럼 end -->

    </section>
  </div>
</template>

<script setup>
  import { ref, computed, onMounted } from 'vue'
  import { useRoute } from 'vue-router'
  import LotScanInput from '../components/LotScanInput.vue'
  import BomMaterialList from '../components/BomMaterialList.vue'
  import LotCreateForm from '../components/LotCreateForm.vue'
  import LotList from '../components/LotList.vue'
  import { useProduction } from '../composables/useProduction' //qr 추가

// qr 추가
// 생산완료 처리 관련
const { 
  loading: productionLoading, 
  error: productionError, 
  completeProductionWithQR 
} = useProduction()
const productionMessage = ref('')


  const route = useRoute()
  const id = route.params.id

  // API 상태 관리
  const loading = ref(false)
  const error = ref('')
  const worksheetInfo = ref(null)
  const bomData = ref([])

  // 생산완료처리
  const doneQty = ref(0)
  const quality = ref('합격')
  const remark = ref('')

  // BOM
  const activeTab = ref('bom')

  // BOM 데이터를 화면 표시용으로 변환
  const bomMaterials = computed(() => {
    return bomData.value.map(bom => ({
      id: bom.childPartCode,
      name: bom.childPartName || bom.childPartCode, // childPartName을 우선 사용하고, 없으면 childPartCode 사용
      used: 0, // 실제 사용량은 별도 로직으로 계산
      total: (bom.useage || 0) * (worksheetInfo.value?.qty || 0)
    }))
  })

  // LOT 관리
  const inputLots = ref([])
  const lots = ref([]) // LOT 리스트 (기존 코드에서 사용하던 변수)

  // 작업상태 텍스트 변환 함수
  function getWorkStateText(workState) {
    const stateMap = {
      '1': '진행중',
      '2': '완료',
      '3': '대기',
      '4': '중단'
    }
    return stateMap[workState] || '알 수 없음'
  }

  // 작업상태 CSS 클래스 변환 함수
  function getWorkStateClass(workState) {
    const classMap = {
      '1': 'badge badge-green',  // 진행중
      '2': 'badge badge-blue',   // 완료
      '3': 'badge badge-yellow', // 대기
      '4': 'badge badge-red'     // 중단
    }
    return classMap[workState] || 'badge badge-gray'
  }

  // API 호출 함수들
  async function fetchWorksheetDetails() {
    try {
      loading.value = true
      error.value = ''
      
      const response = await fetch(`http://localhost:8080/api/mixing-detail/worksheet/${id}`)
      
      if (!response.ok) {
        throw new Error(`작업지시서 정보를 가져올 수 없습니다: ${response.status}`)
      }
      
      worksheetInfo.value = await response.json()
      console.log('작업지시서 정보:', worksheetInfo.value)
    } catch (err) {
      error.value = err.message
      console.error('작업지시서 정보 조회 오류:', err)
    } finally {
      loading.value = false
    }
  }

  async function fetchBomDetails() {
    try {
      loading.value = true
      error.value = ''
      
      const response = await fetch(`http://localhost:8080/api/mixing-detail/bom/${id}`)
      
      if (!response.ok) {
        throw new Error(`BOM 정보를 가져올 수 없습니다: ${response.status}`)
      }
      
      bomData.value = await response.json()
      console.log('BOM 정보:', bomData.value)
    } catch (err) {
      error.value = err.message
      console.error('BOM 정보 조회 오류:', err)
    } finally {
      loading.value = false
    }
  }

  // 전체 정보 한 번에 가져오기 (선택사항)
  async function fetchCompleteDetails() {
    try {
      loading.value = true
      error.value = ''
      
      console.log('API 호출 시작:', `http://localhost:8080/api/mixing-detail/complete/${id}`)
      
      const response = await fetch(`http://localhost:8080/api/mixing-detail/complete/${id}`)
      
      console.log('응답 상태:', response.status)
      
      if (!response.ok) {
        throw new Error(`작업 정보를 가져올 수 없습니다: ${response.status}`)
      }
      
      const data = await response.json()
      console.log('받은 데이터:', data)
      
      worksheetInfo.value = data.worksheet
      bomData.value = data.bomList || []
      
      console.log('worksheetInfo:', worksheetInfo.value)
      console.log('bomData:', bomData.value)
      console.log('변환된 bomMaterials:', bomMaterials.value)
      
    } catch (err) {
      error.value = err.message
      console.error('작업 정보 조회 오류:', err)
    } finally {
      loading.value = false
    }
  }

  function onAddLot(lotNo) {
    const lot = {
      id: lotNo,
      materialName: '카본블랙',
      quantity: 1.0,
      inputTime: new Date().toLocaleTimeString(),
    }
    inputLots.value.push(lot)

    // BOM 사용량 업데이트 로직은 실제 비즈니스 로직에 맞게 수정 필요
  }

  function removeLot(lotId) {
    inputLots.value = inputLots.value.filter(l => l.id !== lotId)
  }

  const totalQuantity = computed(() =>
    inputLots.value.reduce((sum, lot) => sum + lot.quantity, 0).toFixed(1)
  )

  // 컴포넌트 마운트 시 데이터 로드
  onMounted(() => {
    // 작업지시서 정보와 BOM 데이터 모두 가져오기
    fetchWorksheetDetails()
    fetchBomDetails()
    
    // 또는 전체 정보를 한번에 가져오려면 아래 함수 사용
    // fetchCompleteDetails()
  })

  // 생산완료 처리 함수 (기존 함수들과 함께 추가)
async function handleProductionComplete() {
  if (!doneQty.value || !worksheetInfo.value) {
    productionMessage.value = '완료 수량을 입력해주세요.'
    return
  }

  try {
    productionMessage.value = ''
    
    const productionData = {
      workNo: worksheetInfo.value.workNo,
      doneQty: doneQty.value,
      remark: remark.value || '',
      inputLots: inputLots.value
    }

    const qrOptions = {
      width: 300,
      margin: 2,
      color: {
        dark: '#000000',
        light: '#FFFFFF'
      }
    }

    const result = await completeProductionWithQR(productionData, qrOptions)
    
    if (result.success) {
      productionMessage.value = `LOT ${result.lotNumber} 생성 완료! QR 코드가 다운로드되었습니다.`
      
      // 폼 초기화
      doneQty.value = 0
      remark.value = ''
      
      // 성공 메시지를 5초 후 자동으로 숨김
      setTimeout(() => {
        productionMessage.value = ''
      }, 5000)
    }
  } catch (error) {
    productionMessage.value = error.message || '생산완료 처리에 실패했습니다.'
    console.error('생산완료 처리 오류:', error)
  }
}
</script>