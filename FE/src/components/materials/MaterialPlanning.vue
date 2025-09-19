<template>
  <div class="space-y-4">
    <!-- 계획 입력 -->
    <div class="card">
      <div class="grid grid-cols-1 md:grid-cols-4 gap-3">
        <select v-model="period" class="border rounded-md px-3 py-2 text-sm">
          <option value="">기간 선택</option>
          <option value="7d">1주</option>
          <option value="1m">1개월</option>
          <option value="3m">3개월</option>
        </select>

        <select v-model="productCode" class="border rounded-md px-3 py-2 text-sm">
          <option value="">제품 선택</option>
          <option value="TI001">TI001</option>
          <option value="TI002">TI002</option>
        </select>

        <button class="btn-primary" @click="load" :disabled="loading">
          {{ loading ? '조회 중…' : '소요량 계산' }}
        </button>
      </div>
    </div>

    <!-- 예상 소요량 테이블 -->
    <div class="card overflow-x-auto">
      <table class="min-w-full text-sm">
        <thead>
          <tr class="bg-gray-100 text-gray-700">
            <th class="px-3 py-2 text-left">자재명</th>
            <th class="px-3 py-2 text-right">현재 재고</th>
            <th class="px-3 py-2 text-right">예상 소요량</th>
            <th class="px-3 py-2 text-right">부족량</th>
            <th class="px-3 py-2 text-left">발주 권장</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="r in rows" :key="r.partCode" class="border-t">
            <td class="px-3 py-2">{{ r.partName }}</td>
            <td class="px-3 py-2 text-right">{{ r.currInventory }}톤</td>
            <td class="px-3 py-2 text-right">{{ r.expectedConsumption }}톤</td>
            <td class="px-3 py-2 text-right">
              <span :class="r.shortageQty>0?'text-red-600 font-semibold':''">{{ shortageText(r.shortageQty) }}</span>
            </td>
            <td class="px-3 py-2">
              <button class="btn bg-white border" :disabled="r.shortage<=0">
                {{ r.shortageQty>0 ? '발주 필요' : '불필요' }}
              </button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { fetchMaterialPlan } from '../../api/materialplan' //연결할 API 모듈
import dayjs from 'dayjs'

// 필터 모델 (UI 유지)

const period = ref('')         // '7d', '1m', '3m'
const productCode = ref('')    // 예: 'TI001'
const fromDate = ref('')
const toDate = ref('')

const rows = ref([])
const loading = ref(false)
const error = ref('')

const n = v => (typeof v === 'number' ? v.toLocaleString() : v ?? '-')
const shortageText = v => (Number(v) > 0 ? `${n(v)}톤` : '-')

// ✅ 기간 선택 → from/to 변환
function applyPeriod() {
  const today = dayjs()
  if (period.value === '7d') {
    fromDate.value = today.subtract(6, 'day').format('YYYY-MM-DD')
    toDate.value = today.format('YYYY-MM-DD')
  } else if (period.value === '1m') {
    fromDate.value = today.subtract(1, 'month').add(1, 'day').format('YYYY-MM-DD')
    toDate.value = today.format('YYYY-MM-DD')
  } else if (period.value === '3m') {
    fromDate.value = today.subtract(3, 'month').add(1, 'day').format('YYYY-MM-DD')
    toDate.value = today.format('YYYY-MM-DD')
  } else {
    fromDate.value = ''
    toDate.value = ''
  }
}

// period 값 바뀔 때마다 자동 계산
watch(period, applyPeriod, { immediate: true })

// API 호출
async function load() {
  try {
    loading.value = true
    error.value = ''
    const params = {
      productCode: productCode.value || undefined,
      fromDate: fromDate.value || undefined,
      toDate: toDate.value || undefined,
    }
    rows.value = await fetchMaterialPlan(params)
  } catch (e) {
    error.value = '조회 중 오류가 발생했습니다.'
    console.error(e)
  } finally {
    loading.value = false
  }
}

// 페이지 들어오면 기본 조회
onMounted(() => {
  applyPeriod() // 기본 기간 계산
  load()
})
</script>

<!-- // // period를 from/to로 환산하고 싶으면 여기서 처리(선택)
// // 지금은 사용자가 직접 fromDate/toDate 넣는 형태 유지
// function periodToRange() {
//   // 선택 구현: 필요하면 추가
// }

// async function load() {
//   try {
//     loading.value = true
//     error.value = ''
//     // 필요한 값만 파라미터로 전달
//     const params = {
//       productCode: productCode.value || undefined,
//       fromDate: fromDate.value || undefined,
//       toDate: toDate.value || undefined,
//     }
//     rows.value = await fetchMaterialPlan(params)
//   } catch (e) {
//     error.value = '조회 중 오류가 발생했습니다.'
//     // 콘솔에 상세 로그
//     // eslint-disable-next-line no-console
//     console.error(e)
//   } finally {
//     loading.value = false
//   }
// }

// // 최초 1회 자동 조회가 필요하면 onMounted(load)
// onMounted(() => {
//   // 초기엔 비우고 버튼으로만 조회하고 싶으면 주석 처리
//   load()
// }) -->
<!-- // </script> -->

<style scoped>
.card { border: 1px solid #e5e7eb; border-radius: .5rem; padding: .75rem; }
.btn-primary { background:#2563eb; color:#fff; padding:.5rem .75rem; border-radius:.375rem; }
.btn { padding:.375rem .5rem; border-radius:.375rem; }
</style>



