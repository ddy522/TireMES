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
        <WorksheetInfoCard 
          :worksheet-info="worksheetInfo" 
          :loading="loading" 
          :error="error" 
          process="02"
        />

        <!-- BOM 관리 -->
        <div class="card">
          <div class="flex items-center justify-between mb-2">
            <h3 class="section-title">BOM 관리 & 원자재 투입</h3>
            <div class="flex gap-2">
              <button
                @click="activeTab='bom'"
                :class="['btn', activeTab==='bom'?'btn-primary':'bg-gray-100 border']"
              >
                BOM & 스캔
              </button>
              <button
                @click="activeTab='lots'"
                :class="['btn', activeTab==='lots'?'btn-primary':'bg-gray-100 border']"
              >
                투입된 LOT
              </button>
            </div>
          </div>

          <div v-if="activeTab==='bom'">
            <!-- worksheetSkey prop 전달 -->
            <LotScanInput
              :worksheet-skey="worksheetInfo?.worksheetSkey"
              @add="onAddLot"
            />
            <div v-if="loading" class="text-center text-gray-500 py-4">BOM 정보를 불러오는 중...</div>
            <div v-else-if="error" class="text-center text-red-500 py-4">{{ error }}</div>
            <BomMaterialList v-else :materials="bomMaterials" />
          </div>

          <div v-else class="space-y-3">
            <div v-if="inputLots.length===0" class="text-center text-gray-500 py-4">
              투입된 원자재 LOT가 없습니다.
            </div>
            <div v-else>
              <div v-for="lot in inputLots" :key="lot.id" class="flex items-center justify-between border rounded p-2">
                <div>
                  <div class="font-mono font-medium">{{ lot.lotno }}</div>
                  <div class="text-sm text-gray-500">{{ lot.partCode }}/{{ lot.partName }} ({{ lot.qty || 0 }}{{ lot.unitNm }})</div>
                  <div class="text-xs text-gray-400">투입시간: {{ lot.createdAt || '-' }}</div>
                </div>
                <button @click="removeLot(lot)" class="text-red-600 text-sm">삭제</button>
              </div>

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
        <ProductionCompleteForm
          v-model:doneQty="doneQty"
          v-model:remark="remark"
          v-model:worksheetSkey="worksheetSkey"
          @submit="handleComplete"
        />

        <div class="card">
          <div class="flex items-center justify-between mb-2">
            <h3 class="section-title">LOT 관리 시스템</h3>
            <div class="text-xs text-gray-500">작업지시 {{ id }}의 LOT 현황 및 관리</div>
          </div>
          <LotList 
            :lots="lots" 
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
import LotScanInput from '../components/LotScanInput.vue'
import WorksheetInfoCard from '../components/WorksheetInfoCard.vue'
import BomMaterialList from '../components/BomMaterialList.vue'
import LotList from '../components/LotList.vue'
import ProductionCompleteForm from '../components/ProductionCompleteForm.vue'
import axios from 'axios'

const route = useRoute()
const id = route.params.id

const loading = ref(false)
const error = ref('')
const worksheetInfo = ref(null)
const bomData = ref([])

const doneQty = ref(0)
const remark = ref('')
const worksheetSkey = computed(() => worksheetInfo.value?.worksheetSkey || '')

const activeTab = ref('bom')

const bomMaterials = computed(() =>
  bomData.value.map(bom => ({
    id: bom.childPartCode,
    name: bom.childPartName || bom.childPartCode,
    used: bom.inputQty,
    total: bom.needQty
  }))
)

const inputLots = ref([])
const lots = ref([])

function getWorkStateText(workState) {
  const stateMap = { '1':'진행중','2':'완료','3':'대기','4':'중단' }
  return stateMap[workState] || '알 수 없음'
}

function getWorkStateClass(workState) {
  const classMap = { '1':'badge badge-green','2':'badge badge-blue','3':'badge badge-yellow','4':'badge badge-red' }
  return classMap[workState] || 'badge badge-gray'
}

// API 호출
async function fetchWorksheetDetails() {
  try {
    loading.value = true
    error.value = ''

    const process = '02'  // 공정 코드
    const res = await fetch(`http://localhost:8080/api/mixing-detail/worksheet/${id}?process=${process}`)
    if (!res.ok) throw new Error(`작업지시서 정보를 가져올 수 없습니다: ${res.status}`)

    worksheetInfo.value = await res.json()
  } catch (err) {
    error.value = err.message
  } finally {
    loading.value = false
  }
}

async function fetchBomDetails() {
  try {
    loading.value = true
    error.value = ''
    const res = await fetch(`http://localhost:8080/api/mixing-detail/bom/${id}`)
    if (!res.ok) throw new Error(`BOM 정보를 가져올 수 없습니다: ${res.status}`)
    bomData.value = await res.json()
  } catch (err) { error.value = err.message }
  finally { loading.value = false }
}

// LOT DB 조회
async function fetchInputLots() {
  try {
    loading.value = true

    if (!worksheetInfo.value?.worksheetSkey) {
      console.warn("worksheetSkey가 아직 준비되지 않았습니다.")
      return
    }

    const skey = worksheetInfo.value.worksheetSkey
    const res = await fetch(`http://localhost:8080/api/bom/lotList/${id}?worksheetSkey=${skey}`)
    
    if (!res.ok) throw new Error(`투입된 LOT 정보를 가져올 수 없습니다: ${res.status}`)
    
    inputLots.value = await res.json()
  } catch (err) {
    console.error(err)
    inputLots.value = []
  } finally {
    loading.value = false
  }
}

// LOT 추가 시
function onAddLot(payload) {
  // payload = { lotNo, worksheetSkey, result }
  fetchBomDetails()
  console.log("LOT 등록 payload:", payload)
  if (payload.result) {
    inputLots.value.push({
      id: payload.lotNo,
      worksheetSkey: payload.worksheetSkey,
      result: payload.result
    })
  }
}

// LOT 삭제
// LOT 삭제
async function removeLot(lot) {
  try {
    // 삭제 확인
    const isConfirmed = confirm(`LOT ${lot.lotno}를 삭제하시겠습니까?`)
    if (!isConfirmed) return

    const skey = worksheetInfo.value.worksheetSkey
    const res = await fetch(
      `http://localhost:8080/api/bom/deleteLot?lotNo=${lot.lotno}&worksheetSkey=${skey}`,
      {
        method: 'DELETE'
      }
    )
    if (!res.ok) throw new Error(`LOT 삭제 실패: ${res.status}`)

    // 화면에서 제거
    inputLots.value = inputLots.value.filter(l => l.lotno !== lot.lotno)

    // 성공 알림
    alert(`LOT ${lot.lotno}가 삭제되었습니다.`)
    fetchBomDetails()
  } catch (err) {
    console.error(err)
    alert('LOT 삭제 중 오류가 발생했습니다.')
  }
}

function handleComplete() {
  console.log("작업 완료 처리 실행!")
  // 여기에 axios 호출 or 상태 업데이트 넣으면 됨
}



const totalQuantity = computed(() =>
  inputLots.value.reduce((sum, lot) => sum + (lot.qty || 0), 0).toFixed(1)
)

// activeTab이 'lots'일 때만 inputLots 조회
watch(activeTab, (newTab) => {
  if (newTab === 'lots') {
    fetchInputLots()
  }
})

onMounted(() => {
  fetchWorksheetDetails()
  fetchBomDetails()
})
</script>
