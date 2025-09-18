<template>
    <div class="flex items-center gap-2">
      <!-- 라벨 -->
      <label v-if="label" class="text-sm font-medium text-gray-700">
        {{ label }} :
      </label>
  
      <!-- 셀렉트 박스 -->
      <select
        v-model="selectedValue"
        @change="onChange"
        :style="{ width: width }"
        class="border rounded px-3 py-2 text-sm focus:outline-none focus:ring focus:ring-blue-300"
      >
        <option value="">-- 선택하세요 --</option>
        <option v-for="opt in options" :key="opt.value" :value="opt.value">
          {{ opt.label }}
        </option>
      </select>
    </div>
  </template>
  
  <script setup>
  import { ref, onMounted, watch } from 'vue'
  import axios from 'axios'
  
  // props 정의
  const props = defineProps({
    type: { type: String, required: true },      // API 호출용 type
    label: { type: String, default: '' },       // 라벨 텍스트
    modelValue: { type: String, default: '' },  // v-model
    width: { type: String, default: '100%' }    // select 너비
  })
  
  // 이벤트 정의
  const emit = defineEmits(['update:modelValue'])
  
  // 상태값
  const options = ref([])
  const selectedValue = ref(props.modelValue)
  
  // 컴포넌트 마운트 시 API 호출
  onMounted(async () => {
    try {
      const res = await axios.get(`/api/common/select?type=${props.type}`)
      options.value = res.data
    } catch (err) {
      console.error('CommonSelect API 호출 실패:', err)
    }
  })
  
  // selectedValue 변경 시 v-model 업데이트
  watch(selectedValue, (val) => {
    emit('update:modelValue', val)
  })
  
  // select 변경 이벤트
  const onChange = () => {
    emit('update:modelValue', selectedValue.value)
  }
  </script>
  