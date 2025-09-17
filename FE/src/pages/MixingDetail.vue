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
          <div class="flex items-center gap-4">
            <div class="w-20 h-20 bg-gray-100 rounded-md flex items-center justify-center">
              <span class="text-3xl">🛞</span>
            </div>
            <div class="text-sm">
              <div class="text-gray-500">작업지시번호</div>
              <div class="font-semibold text-gray-900">{{ id }}</div>
              <div class="mt-2 grid grid-cols-3 gap-4">
                <div>
                  <div class="text-gray-500">제품명</div>
                  <div class="font-medium">고성능 타이어 205/55R16</div>
                </div>
                <div>
                  <div class="text-gray-500">계획수량</div>
                  <div class="font-medium">1,000개</div>
                </div>
                <div>
                  <div class="text-gray-500">진행상태</div>
                  <span class="badge badge-green">진행중</span>
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
            <LotScanInput @add="onAddLot" />
            <BomMaterialList :materials="materials" />
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
            <button class="btn-primary px-4 py-2 whitespace-nowrap">발행</button>
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
  import { ref, computed } from 'vue'
  import { useRoute } from 'vue-router'
  import LotScanInput from '../components/LotScanInput.vue'
  import BomMaterialList from '../components/BomMaterialList.vue'
  import LotCreateForm from '../components/LotCreateForm.vue'
  import LotList from '../components/LotList.vue'

  const route = useRoute()
  const id = route.params.id

  // 생산완료처리
  const doneQty = ref(0)
  const quality = ref('합격')
  const remark = ref('')

  // BOM
  const activeTab = ref('bom')
  const materials = ref([
    { id: 'MAT-001', name: '천연고무', used: 45.5, total: 45.5 },
    { id: 'MAT-002', name: '합성고무', used: 30.2, total: 30.2 },
    { id: 'MAT-003', name: '카본블랙', used: 12.3, total: 15.8 },
  ])

  // LOT 관리
  const inputLots = ref([])

  function onAddLot(lotNo) {
    const lot = {
      id: lotNo,
      materialName: '카본블랙',
      quantity: 1.0,
      inputTime: new Date().toLocaleTimeString(),
    }
    inputLots.value.push(lot)

    const m = materials.value.find(x => x.id === 'MAT-003')
    if (m) m.used = Math.min(m.used + lot.quantity, m.total)
  }

  function removeLot(lotId) {
    inputLots.value = inputLots.value.filter(l => l.id !== lotId)
  }

  const totalQuantity = computed(() =>
    inputLots.value.reduce((sum, lot) => sum + lot.quantity, 0).toFixed(1)
  )
</script>
