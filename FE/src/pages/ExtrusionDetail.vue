<template>
  <div class="space-y-4 min-h-screen">
    <!-- 헤더 -->
    <div class="flex items-center gap-2 text-sm text-gray-500">
      <h1 class="text-2xl font-bold text-gray-900">설비 관리</h1>
      <span class="ml-auto text-xs text-blue-600 bg-blue-50 border border-blue-200 rounded px-2 py-0.5">
        /equipment
      </span>
    </div>

    <!-- ===== 전체 레이아웃 ===== -->
    <div class="grid grid-cols-12 gap-3 h-[calc(100vh-110px)]">
      <!-- 좌측: 설비 정보(값 제거된 스냅샷 + 실시간 정보 버튼 포함) -->
      <section class="card col-span-12 lg:col-span-3 lg:row-span-2 flex flex-col overflow-auto">
        <h3 class="section-title">설비 정보</h3>

        <!-- 선택 -->
        <div class="grid grid-cols-2 gap-2 text-sm">
          <select v-model="proc" class="border rounded px-2 py-1">
            <option value="믹싱">믹싱</option>
            <option value="압출">압출</option>
            <option value="재단">재단</option>
            <option value="검사">검사</option>
          </select>
          <select v-model="equip" class="border rounded px-2 py-1">
            <option value="믹싱기 A">믹싱기 A</option>
            <option value="믹싱기 B">믹싱기 B</option>
            <option value="압출기 A">압출기 A</option>
            <option value="압출기 B">압출기 B</option>
          </select>
          <button class="col-span-2 btn-secondary">정비 이력 보기</button>
        </div>

        <!-- 기본 정보 -->
        <div class="mt-4 space-y-1 text-sm">
          <div>공정: {{ proc }}</div>
          <div>설비: {{ equip }}</div>
          <div>
            가동:
            <span class="inline-flex items-center gap-1">
              <span class="h-2 w-2 rounded-full bg-green-500"></span>
              <span class="text-green-700 font-semibold">가동</span>
            </span>
          </div>
          <div>상태: 정상</div>
        </div>

        <!-- ✅ 상태 스냅샷: 값 제거, 텍스트만 표시 -->
        <div class="mt-4">
          <h4 class="text-sm font-semibold text-gray-800 mb-2">상태 스냅샷</h4>
          <div class="grid grid-cols-2 gap-2 text-sm">
            <!-- 값(number) 렌더링 없음: 라벨만 출력 -->
            <div
              v-for="label in snapshotLabels"
              :key="label"
              class="box"
            >
              {{ label }}
            </div>
          </div>
        </div>

        <!-- 빠른 작업 + 실시간 정보 -->
        <div class="mt-4">
          <h4 class="text-sm font-semibold text-gray-800 mb-2">빠른 작업</h4>
          <div class="grid grid-cols-2 gap-2">
            <button class="btn-secondary">고장 등록</button>
            <button class="btn-secondary">이상 징후</button>
            <button class="btn-secondary">긴급 경고</button>
            <button class="btn-secondary">예비품 요청</button>
            <button class="col-span-2 btn-primary">실시간 정보</button>
          </div>
        </div>
      </section>

      <!-- 우측 상단: 설비 상태 데이터(기존 유지) -->
      <section class="card col-span-12 lg:col-span-9">
        <h3 class="section-title">설비 상태 데이터</h3>

        <!-- 헤더 정보 -->
        <div class="grid grid-cols-1 md:grid-cols-3 gap-3 text-sm text-gray-600 mb-2">
          <div class="border rounded p-2 bg-gray-50">
            <div class="text-gray-500">정비 유형</div>
            <div class="font-medium">정기점검</div>
          </div>
          <div class="border rounded p-2 bg-gray-50">
            <div class="text-gray-500">담당자</div>
            <div class="font-medium">박민수</div>
          </div>
          <div class="border rounded p-2 bg-gray-50">
            <div class="text-gray-500">최근 정비</div>
            <div class="font-medium">2025-09-10</div>
          </div>
        </div>

        <!-- 뷰 토글 -->
        <div class="flex items-center gap-2 mb-2">
          <button class="btn-secondary" :class="{ 'ring-2 ring-blue-500': view==='minute' }" @click="view='minute'">minute</button>
          <button class="btn-secondary" :class="{ 'ring-2 ring-blue-500': view==='hour' }" @click="view='hour'">hour</button>
          <button class="btn-secondary" :class="{ 'ring-2 ring-blue-500': view==='day' }" @click="view='day'">day</button>
          <span class="text-sm text-gray-500">· 선택 항목: 온도 · 현재값: 70</span>
          <div class="ml-auto flex gap-2">
            <button class="btn-secondary">« 처음</button>
            <button class="btn-secondary">( 이전</button>
            <button class="btn-secondary">다음 )</button>
            <button class="btn-secondary">끝 »</button>
          </div>
        </div>

        <!-- 차트 자리 -->
        <div class="h-64 flex items-center justify-center border rounded bg-white">
          <div class="text-gray-400">📊 차트 영역</div>
        </div>

        <!-- 타임라인 -->
        <div class="mt-3">
          <div class="flex items-center gap-2">
            <span class="text-sm text-gray-600">타임라인</span>
            <input type="range" min="0" max="60" v-model="timeline" class="flex-1" />
            <span class="text-xs text-gray-500">{{ timeline }}/60</span>
          </div>
        </div>
      </section>

      <!-- 우측 하단: 알림(기존 유지) -->
      <section class="card col-span-12">
        <h3 class="section-title">알림</h3>
        <ul class="text-sm text-gray-700 space-y-2">
          <li class="flex gap-2 items-start">
            <span class="mt-1 h-2 w-2 rounded-full bg-red-500"></span>
            <div>
              <div class="text-red-600">온도 경고: 85℃ 초과</div>
              <div class="text-gray-500 text-xs">오늘 14:22</div>
            </div>
          </li>
          <li class="flex gap-2 items-start">
            <span class="mt-1 h-2 w-2 rounded-full bg-yellow-500"></span>
            <div>
              <div class="text-yellow-700">점검 미이행 항목</div>
              <div class="text-gray-500 text-xs">어제 17:05</div>
            </div>
          </li>
          <li class="flex gap-2 items-start">
            <span class="mt-1 h-2 w-2 rounded-full bg-blue-600"></span>
            <div>
              <div class="text-blue-700">예비품 재고 부족(벨트)</div>
              <div class="text-gray-500 text-xs">3일 전</div>
            </div>
          </li>
        </ul>
        <div class="text-right mt-3">
          <button class="btn-secondary">모두 확인</button>
        </div>
      </section>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const proc = ref('믹싱')
const equip = ref('믹싱기 A')
const view = ref('day')
const timeline = ref(0)

/** 스냅샷에 숫자/값은 포함하지 않음 — 라벨만 */
const snapshotLabels = [
  '온도','습도','전력','분진','VOCs','토크','두께','길이','장력','압력'
]
</script>

<style scoped>
.card { @apply bg-white border rounded-lg p-4 shadow-sm; }
.section-title { @apply font-semibold text-gray-800 mb-2; }

/* 스냅샷 박스: 값 대신 라벨만 보이도록 단순 스타일 */
.box { @apply border rounded p-2 bg-gray-50 text-gray-700; }

.btn-primary { @apply bg-blue-600 text-white rounded px-3 py-2 text-sm hover:bg-blue-700; }
.btn-secondary { @apply bg-gray-100 text-gray-700 rounded px-3 py-2 text-sm hover:bg-gray-200; }
</style>
