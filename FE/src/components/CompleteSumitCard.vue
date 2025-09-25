<template>
  <div class="card">
    <h3 class="section-title">
      {{ isInspection ? '검사완료처리' : '생산완료처리' }}
    </h3>

    <!-- 생산 완료 전용 입력 -->
    <div v-if="!isInspection" class="flex gap-3 items-center">
      <input
        v-model.number="localDoneQty"
        type="number"
        placeholder="완료된 수량 입력"
        class="border rounded-md px-3 py-2 text-sm w-24"
      />
      <input
        v-model="localRemark"
        type="text"
        placeholder="특이사항이나 비고를 입력하세요"
        class="border rounded-md px-3 py-2 text-sm flex-1"
      />
      <button class="btn-primary px-4 py-2 whitespace-nowrap" @click="handleSubmit">
        발행
      </button>
    </div>

    <!-- 검사 완료 전용 입력 -->
    <div v-else class="flex gap-3 items-center">
      <div class="flex items-center gap-2 text-sm">
        <label class="font-medium">판정</label>
        <label class="flex items-center gap-1">
          <input type="radio" value="1" v-model.number="localDecision" /> 합격
        </label>
        <label class="flex items-center gap-1">
          <input type="radio" value="0" v-model.number="localDecision" /> 불합격
        </label>
      </div>
      <button class="btn-primary px-4 py-2 whitespace-nowrap" @click="handleSubmit">
        검사완료
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import axios from 'axios'

const emit = defineEmits(['submit'])

const props = defineProps({
  doneQty: { type: Number, default: 0 },
  remark: { type: String, default: '' },
  // DB에서 worksheet skey가 보통 UUID(문자열) 이라 String 권장
  worksheetSkey: { type: String, required: true },
  // 검사 페이지 여부
  isInspection: { type: Boolean, default: false },
})

const localDoneQty = ref(props.doneQty)
const localRemark = ref(props.remark)
const localDecision = ref(1) // 1: 합격, 0: 불합격 (기본 합격)

// props 변경 시 동기화(선택)
watch(() => props.doneQty, v => (localDoneQty.value = v))
watch(() => props.remark, v => (localRemark.value = v))

const endpoint = computed(() =>
  props.isInspection
    ? 'http://localhost:8080/api/worksheet/insertInspectionProduction'
    : 'http://localhost:8080/api/worksheet/insertProduction'
)

async function handleSubmit() {
  try {
    const payload = props.isInspection
      ? { worksheetSkey: props.worksheetSkey, decision: localDecision.value }
      : { worksheetSkey: props.worksheetSkey, doneQty: localDoneQty.value, remark: localRemark.value }

    const res = await axios.post(endpoint.value, payload)

    alert(props.isInspection ? '✅ 검사 완료가 등록되었습니다!' : '✅ 생산 완료가 등록되었습니다!')
    console.log('서버 응답:', res.data)
    emit('submit', payload) // 부모에 완료 알림(+필요하면 payload 전달)
  } catch (err) {
    console.error('등록 실패:', err)
    alert('❌ 등록 실패: ' + (err.response?.data?.message ?? err.message))
  }
}
</script>
