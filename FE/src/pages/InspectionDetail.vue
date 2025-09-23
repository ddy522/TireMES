<template>
  <div class="card space-y-4">
    <h3 class="font-semibold text-gray-900">검사 진행 (Demo)</h3>
    <p class="text-sm text-gray-500">
      대상 작업지시: <span class="font-mono">{{ workNos.join(', ') }}</span>
    </p>

    <div class="overflow-x-auto">
      <table class="min-w-full text-sm">
        <thead>
          <tr class="bg-gray-100 text-gray-700">
            <th class="px-3 py-2 text-left">작업지시</th>
            <th class="px-3 py-2 text-left">제품명</th>
            <th class="px-3 py-2 text-center">판정</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="row in items" :key="row.id" class="border-t">
            <td class="px-3 py-2 font-mono">{{ row.workNo }}</td>
            <td class="px-3 py-2">{{ row.partName }}</td>
            <td class="px-3 py-2 text-center">
              <label class="mr-3">
                <input type="radio" :name="'r'+row.id" value="OK" v-model="row.result" />
                OK
              </label>
              <label>
                <input type="radio" :name="'r'+row.id" value="NG" v-model="row.result" />
                NG
              </label>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <div>
      <button class="btn-primary" @click="onSave">저장</button>
    </div>

    <!-- 요약 -->
    <div class="mt-4">
      <h4 class="font-semibold">검사 결과 요약</h4>
      <ul class="text-sm mt-2 space-y-1">
        <li v-for="row in items" :key="row.id">
          {{ row.workNo }} → {{ row.result || '미판정' }}
        </li>
      </ul>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const workNos = ref([])
const items = ref([])

onMounted(() => {
  const q = (route.query.workNos || '').toString().trim()
  workNos.value = q ? q.split(',').map(s => s.trim()).filter(Boolean) : []

  // ✅ 임시 더미 데이터
  items.value = workNos.value.map((w, i) => ({
    id: i,
    workNo: w,
    partName: '타이어A',
    result: ''  // OK/NG 선택값
  }))
})

function onSave() {
  alert('저장되었습니다! (Demo)\n' + JSON.stringify(items.value, null, 2))
}
</script>

<style scoped>
.card{background:#fff;border:1px solid #e5e7eb;border-radius:12px;padding:16px}
.btn-primary{background:#3b82f6;color:#fff;padding:6px 12px;border-radius:6px}
</style>
