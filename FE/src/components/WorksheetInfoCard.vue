<template>
    <div class="card">
      <h3 class="section-title">작업지시 정보</h3>
  
      <!-- 로딩 -->
      <div v-if="loading && !worksheetInfo" class="text-center text-gray-500 py-4">
        작업지시 정보를 불러오는 중...
      </div>
  
      <!-- 에러 -->
      <div v-else-if="error && !worksheetInfo" class="text-center text-red-500 py-4">
        {{ error }}
      </div>
  
      <!-- 데이터 -->
      <div v-else-if="worksheetInfo" class="flex items-center gap-4">
        <div class="w-20 h-20 bg-gray-100 rounded-md flex items-center justify-center">
          <span class="text-3xl">🛞</span>
        </div>
        <div class="text-sm">
          <div class="text-gray-500">작업지시번호</div>
          <div class="font-semibold text-gray-900">{{ worksheetInfo.workNo }}</div>
          <div class="mt-2 grid grid-cols-3 gap-4">
            <div>
              <div class="text-gray-500">제품명</div>
              <div class="font-medium">{{ worksheetInfo.partName }}</div>
            </div>
            <div>
              <div class="text-gray-500">계획수량</div>
              <div class="font-medium">{{ worksheetInfo.qty }}개</div>
            </div>
            <div>
              <div class="text-gray-500">진행상태</div>
              <span :class="getWorkStateClass(worksheetInfo.workState)">
                {{ getWorkStateText(worksheetInfo.workState) }}
              </span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </template>
  
  <script setup>
  import { defineProps } from "vue"
  
  // 부모에서 내려줄 데이터 정의
  const props = defineProps({
    worksheetInfo: { type: Object, default: null },
    loading: { type: Boolean, default: false },
    error: { type: String, default: "" }
  })
  
  // 상태 표시용 함수들 (부모 코드 그대로 복붙 가능)
  function getWorkStateText(workState) {
    const stateMap = { '1':'진행중','2':'완료','3':'대기','4':'중단' }
    return stateMap[workState] || '알 수 없음'
  }
  
  function getWorkStateClass(workState) {
    const classMap = { '1':'badge badge-green','2':'badge badge-blue','3':'badge badge-yellow','4':'badge badge-red' }
    return classMap[workState] || 'badge badge-gray'
  }
  </script>
  