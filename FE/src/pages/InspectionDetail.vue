<template>
  <div class="space-y-6">
    <!-- 상단: 뒤로가기 + 제목 -->
    <div class="flex items-center gap-2 text-sm text-gray-500">
      <RouterLink to="/inspection" class="hover:underline">◀ 뒤로가기</RouterLink>
      <h1 class="text-xl font-bold text-gray-900">검사 진행</h1>
      <span class="ml-auto text-xs text-blue-600 bg-blue-50 border border-blue-200 rounded px-2 py-0.5">실시간 작업</span>
    </div>

    <div class="overflow-x-auto">
      <table class="min-w-full text-sm">
        <thead>
          <tr class="bg-gray-100 text-gray-700">
            <th class="px-3 py-2 text-left">작업지시</th>
            <th class="px-3 py-2 text-center">판정</th>
            <th class="px-3 py-2 text-center">이미지</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="row in items" :key="row.workNo" class="border-t">
            <td class="px-3 py-2 font-mono">{{ row.workNo }}</td>
            <td class="px-3 py-2 text-center">
              <span v-if="row.decisionText === 'OK'" class="px-2 py-1 rounded bg-green-100 text-green-700 font-medium">OK</span>
              <span v-else-if="row.decisionText === 'NG'" class="px-2 py-1 rounded bg-red-100 text-red-700 font-medium">NG</span>
              <span v-else class="px-2 py-1 rounded bg-gray-100 text-gray-500">미판정</span>
            </td>
            <td class="px-3 py-2 text-center">
              <img
                v-if="row.imageUrl"
                :src="row.imageUrl"
                alt="검사 이미지"
                class="h-12 w-auto mx-auto cursor-pointer rounded border"
                @click="openPreview(row.imageUrl)"
              />
              <span v-else class="text-gray-400">-</span>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- 검사 완료 버튼 -->
    <!-- <div class="mt-6 text-right">
      <button class="btn-primary" :disabled="loading || items.length === 0" @click="onComplete">
        {{ loading ? '처리 중…' : '검사 완료' }}
      </button>
    </div> -->

    <!-- 검사 완료 버튼 -->
    <div class="mt-6 text-right">
      <button
        class="btn-primary"
        :disabled="loading || items.length === 0 || completed"
        @click="onComplete"
      >
        {{ loading ? '처리 중…' : completed ? '완료됨' : '검사 완료' }}
      </button>
    </div>

    <!-- 이미지 미리보기 모달 -->
    <div v-if="previewUrl" class="fixed inset-0 bg-black bg-opacity-60 flex items-center justify-center z-50">
      <div class="bg-white p-4 rounded shadow-lg">
        <img :src="previewUrl" alt="미리보기" class="max-h-[80vh] max-w-[80vw] rounded" />
        <div class="text-right mt-2">
          <button @click="previewUrl=null" class="btn-primary">닫기</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import axios from 'axios'

const route = useRoute()
const workNos = ref([])
const items = ref([])
const previewUrl = ref(null)
const loading = ref(false)

function openPreview(url) {
  previewUrl.value = url
}

const completed = ref(false)

onMounted(async () => { 
  const q = (route.query.workNos || '').toString().trim()
  workNos.value = q ? q.split(',').map(s => s.trim()).filter(Boolean) : []

  const rows = []
  for (const workNo of workNos.value) {
    try {
      const { data } = await axios.get('/api/inspection/decision', { params: { workNo } })
      rows.push({
        workNo,
        decisionText: data?.decisionText ?? null,      // 'OK' | 'NG' | null
        inspectionResult: data?.inspectionResult ?? null,
        imageUrl: data?.imageUrl ?? null
      })
    } catch (e) {
      console.error('판정 조회 실패:', workNo, e)
      rows.push({ workNo, decisionText: null, inspectionResult: null, imageUrl: null })
    }
  }
  items.value = rows
})

// 판정 문자열 → 숫자
function toDecision(text) {
  if (text === 'OK') return 1
  if (text === 'NG') return 0
  return 0
}

// ✅ 순차 전송(레이스 컨디션 방지) + 로딩/요약
async function onComplete() {
  if (!items.value.length) {
    alert('완료할 항목이 없습니다.')
    return
  }

  loading.value = true
  const ok = []
  const fail = []

  try {
    for (const row of items.value) {
      try {
        await axios.post('/api/worksheet/insertInspectionProduction', {
          workNo: row.workNo,
          decision: toDecision(row.decisionText)
        })
        ok.push(row.workNo)
      } catch (err) {
        console.error('[실패]', row.workNo, err)
        fail.push(row.workNo)
      }
    }

    let msg = `처리 완료\n\n성공: ${ok.length}건\n실패: ${fail.length}건`
    if (ok.length) msg += `\n\n성공 목록: ${ok.join(', ')}`
    if (fail.length) msg += `\n실패 목록: ${fail.join(', ')}`
    alert(msg)

        // ✅ 처리 끝나면 버튼 비활성화
    completed.value = true
  } finally {
    loading.value = false
  }


//   } finally {
//     loading.value = false
//   }
 }
</script>

<style scoped>
.card {
  background: #fff;
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  padding: 16px;
}
.btn-primary {
  background: #3b82f6;
  color: #fff;
  padding: 8px 16px;
  border-radius: 6px;
  font-weight: 500;
}
.btn-primary:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}
</style>
