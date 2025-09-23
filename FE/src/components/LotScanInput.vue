<template>
  <div class="mb-3">
    <label class="sr-only" for="lot-input">원자재 LOT 스캔</label>
    <div class="flex gap-2">
      <input
        id="lot-input"
        v-model="lotNo"
        type="text"
        placeholder="원자재 LOT 번호를 스캔하세요 (예: 0120250919001)"
        class="flex-1 border rounded-md px-3 py-2 text-sm"
        @keyup.enter="emitAdd"
      />
      <button class="btn-primary" @click="emitAdd">투입</button>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRoute } from 'vue-router'
import axios from 'axios'

const props = defineProps({
  worksheetSkey: String
})

const lotNo = ref('')
const emit = defineEmits(['add'])
const route = useRoute()

async function emitAdd() {
  if (!lotNo.value) return

  const process = route.meta.process || "mixing"

  try {
    // 서버 호출
    const response = await axios.post('http://localhost:8080/api/bom/bomInsert', {
      lotno: lotNo.value,
      proc: process,
      worksheetSkey: props.worksheetSkey
    })

    // 서버 응답 예시: { success: true/false, message: "..." }
    const result = response.data

    if (result.success) {
      alert(`✅ ${result.message}`)  // 성공 메시지
      // 부모 컴포넌트에 알림
      emit('add', { lotNo: lotNo.value, process, result: result.success })
    } else {
      alert(`❌ ${result.message}`)  // 실패 메시지
    }

  } catch (err) {
    console.error('등록 중 오류:', err)
    alert("등록 중 오류가 발생했습니다.")
  }

  lotNo.value = ''
}
</script>
