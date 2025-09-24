<template>
  <div class="space-y-6">
    <!-- 상단: 뒤로가기 + 제목 -->
    <div class="flex items-center gap-2 text-sm text-gray-500">
      <RouterLink to="/extrusion" class="hover:underline">◀ 뒤로가기</RouterLink>
      <h1 class="text-xl font-bold text-gray-900">압출 작업화면</h1>
      <span class="ml-auto text-xs text-blue-600 bg-blue-50 border border-blue-200 rounded px-2 py-0.5">실시간 작업</span>
    </div>

    <section class="grid grid-cols-1 lg:grid-cols-2 gap-6">
      <!-- 좌측 컬럼: 작업지시 정보 -->
      <div class="space-y-6">
        <div class="card">
          <h3 class="section-title">작업지시 정보</h3>
          <div class="flex items-center gap-4">
            <div class="w-20 h-20 bg-gray-100 rounded-md flex items-center justify-center">
              <span class="text-3xl">🧵</span>
            </div>
            <div class="text-sm">
              <div class="text-gray-500">작업지시번호</div>
              <div class="font-semibold text-gray-900">{{ id }}</div>
              <div class="mt-2 grid grid-cols-3 gap-4">
                <div>
                  <div class="text-gray-500">제품명</div>
                  <div class="font-medium">프리미엄 타이어 225/60R17</div>
                </div>
                <div>
                  <div class="text-gray-500">계획수량</div>
                  <div class="font-medium">800개</div>
                </div>
                <div>
                  <div class="text-gray-500">진행상태</div>
                  <span class="badge badge-yellow">대기중</span>
                </div>
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
            
            <!-- ✅ 수정: 기존 단순 버튼을 상태 관리 버튼으로 변경 -->
            <button 
              @click="handleProductionComplete"
              :disabled="productionLoading || !doneQty"
              :class="['px-4 py-2 whitespace-nowrap rounded-md text-sm font-medium transition-colors', 
                      productionLoading || !doneQty 
                        ? 'bg-gray-100 text-gray-400 border border-gray-200 cursor-not-allowed' 
                        : 'btn-primary hover:bg-blue-600 active:bg-blue-700']"
            >
              {{ productionLoading ? '처리중...' : '발행' }}
            </button>
          </div>

          <!-- ✅ 추가: 생산완료 처리 결과 메시지 표시 영역 -->
          <div v-if="productionMessage" class="mt-2 p-2 text-sm rounded" 
              :class="productionError ? 'bg-red-50 text-red-600 border border-red-200' : 'bg-green-50 text-green-600 border border-green-200'">
            {{ productionMessage }}
          </div>
        </div>

        <!-- LOT 관리 시스템 -->
        <div class="card">
          <div class="flex items-center justify-between mb-2">
            <h3 class="section-title">LOT 관리 시스템</h3>
            <div class="text-xs text-gray-500">작업지시 {{ id }}의 LOT 현황 및 관리</div>
          </div>

          <!-- 25.09..17 하도이 lot 발행 주석처리  -->
          <!-- <LotCreateForm @create="createLot" /> -->

          <LotList :lots="lots" class="mt-4" />
        </div>
      </div>
      <!-- 우측 컬럼 end -->
    </section>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRoute } from 'vue-router'
import LotCreateForm from '../components/LotCreateForm.vue'
import LotList from '../components/LotList.vue'
// ✅ 추가: QR 생성을 위한 useProduction composable 임포트
import { useProduction } from '../composables/useProduction'

const route = useRoute()
const id = route.params.id

// ✅ 추가: 생산완료 처리 관련 상태 관리
const { 
  loading: productionLoading, 
  error: productionError, 
  completeProductionWithQR 
} = useProduction()
const productionMessage = ref('')

// 생산완료처리
const doneQty = ref(0)
const quality = ref('합격')
const remark = ref('')

// LOT 관리
const lots = ref([
  { no: 'LOT-W0001-001', qty: 25, createdAt: '14:30', status: '완료', currentProc: '재단', quality: '합격', history: ['믹싱', '압출'] },
  { no: 'LOT-W0001-002', qty: 30, createdAt: '15:15', status: '진행중', currentProc: '압출', quality: '검사중', history: ['믹싱'] },
  { no: 'LOT-W0001-003', qty: 20, createdAt: '15:45', status: '대기', currentProc: '믹싱', quality: '대기', history: ['믹싱'] },
])

function createLot(qty) {
  const idx = (lots.value.length + 1).toString().padStart(3, '0')
  lots.value.push({
    no: `LOT-W0001-${idx}`,
    qty,
    createdAt: new Date().toTimeString().slice(0,5),
    status: '대기',
    currentProc: '압출',
    quality: '대기',
    history: ['믹싱']
  })
}

// ✅ 추가: 생산완료 처리 및 QR 생성 함수 (완전히 새로운 함수)
async function handleProductionComplete() {
  if (!doneQty.value) {
    productionMessage.value = '완료 수량을 입력해주세요.'
    return
  }

  try {
    productionMessage.value = ''
    
    // 압출 공정용 생산완료 데이터
    const productionData = {
      workNo: id, // 작업지시번호
      doneQty: doneQty.value,
      remark: remark.value || '',
      processType: 'EXTRUSION', // 압출 공정 타입 추가
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