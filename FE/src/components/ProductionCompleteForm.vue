<template>
  <div class="card">
    <h3 class="section-title">생산완료처리</h3>
    <div class="flex gap-3 items-center">
      <input
        v-model.number="localDoneQty"
        type="number"
        placeholder="완료된 수량 입력"
        class="border rounded-md px-3 py-2 text-sm w-20 no-spinner"
        @keydown="preventMinus"
      />
      <input
        v-model="localRemark"
        type="text"
        placeholder="특이사항이나 비고를 입력하세요"
        class="border rounded-md px-3 py-2 text-sm flex-1"
      />
      <!-- 버튼 클릭 시 handleCompleteSubmit 실행 후 부모 emit -->
      <button 
        class="btn-primary px-4 py-2 whitespace-nowrap"
        @click="handleSubmit"
      >
        발행
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import axios from "axios"

const emit = defineEmits(['submit']) // 부모로 이벤트 전달

const props = defineProps({
  doneQty: { type: Number, default: 0 },
  remark: { type: String, default: '' },
  worksheetSkey: { type: Number, required: true } // 서버에 보낼 키도 props로 받도록
})

// 로컬 상태
const localDoneQty = ref(props.doneQty)
const localRemark = ref(props.remark)

// 서버 전송 + 부모 emit
async function handleCompleteSubmit({ doneQty, remark }) {
  try {
    const res = await axios.post("http://localhost:8080/api/worksheet/insertProduction", {
      worksheetSkey: props.worksheetSkey,
      doneQty,
      remark,
    })
    alert("✅ 생산 완료가 등록되었습니다!")
    console.log("서버 응답:", res.data)
  } catch (err) {
    console.error("생산완료 등록 실패:", err)
    alert("❌ 등록 실패: " + err.message)
  }
}

// 버튼 클릭 시 실행: 서버 등록 후 부모에 이벤트 emit
function handleSubmit() {
  handleCompleteSubmit({ doneQty: localDoneQty.value, remark: localRemark.value })
  emit('submit') // 부모 컴포넌트 handleComplete 호출
}

function preventMinus(e) {
  if (e.key === '-' || e.key === 'e' || e.key === '+' ) {
    e.preventDefault()
  }
}
</script>

<style scoped>
/* Chrome, Edge, Safari */
.no-spinner::-webkit-outer-spin-button,
.no-spinner::-webkit-inner-spin-button {
  -webkit-appearance: none;
  margin: 0;
}

/* Firefox */
.no-spinner[type=number] {
  -moz-appearance: textfield;
}
</style>