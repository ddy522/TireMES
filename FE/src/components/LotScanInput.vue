<template>
  <div class="mb-3">
    <label class="sr-only" for="lot-input">원자재 LOT 스캔</label>
    <div class="flex gap-2">
      <input
        id="lot-input"
        v-model="lotNo"
        type="text"
        placeholder="원자재 LOT 번호를 스캔하세요 (예: MAT-003-LOT-001)"
        class="flex-1 border rounded-md px-3 py-2 text-sm"
        @keyup.enter="emitAdd"
      />
      <button class="btn-primary" @click="emitAdd">투입</button>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
const lotNo = ref('')
const emit = defineEmits(['add'])

function emitAdd() {
  if (!lotNo.value) return
  emit('add', lotNo.value)
  lotNo.value = ''
}
</script>
