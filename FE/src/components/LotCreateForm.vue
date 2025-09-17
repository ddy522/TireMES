<template>
  <div class="space-y-2">
    <h4 class="section-title">+ 새 LOT 생성</h4>
    <div class="flex gap-2">
      <input v-model.number="qty" type="number" min="1" placeholder="수량 입력"
             class="flex-1 border rounded-md px-3 py-2 text-sm">
      <button class="btn-primary" @click="create">LOT 생성</button>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
const qty = ref(20)
const emit = defineEmits(['create'])
function create() {
  if (!qty.value) return
  emit('create', Number(qty.value))
  qty.value = 0
}
</script>
