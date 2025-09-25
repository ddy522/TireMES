<router-view :key="$route.path" />

<template>
  <div class="flex h-screen overflow-hidden">
    <!-- Sidebar -->
    <aside class="w-72 bg-white border-r border-gray-200 flex flex-col">
      <div class="p-6 border-b border-gray-200">
        <h2 class="text-2xl font-bold text-gray-900">MES 시스템</h2>
        <p class="text-base text-gray-600">제조실행시스템</p>
      </div>
    <nav class="flex-1 p-4 space-y-2">
      <!-- 대시보드 -->
      <RouterLink
        to="/dashboard"
        class="flex items-center w-full text-left px-5 py-4 rounded-md text-lg font-medium hover:bg-gray-100"
        :class="$route.path.startsWith('/dashboard') ? 'bg-indigo-50 text-indigo-700' : ''"
      >
        <LayoutDashboard class="w-7 h-7 mr-3" />
        대시보드
      </RouterLink>

      <!-- 믹싱 -->
      <RouterLink
        to="/mixing"
        class="flex items-center w-full text-left px-5 py-4 rounded-md text-lg font-medium hover:bg-gray-100"
        :class="$route.path.startsWith('/mixing') ? 'bg-indigo-50 text-indigo-700' : ''"
      >
        <RotateCcw class="w-7 h-7 mr-3"/>
        믹싱
      </RouterLink>

      <!-- 압출 -->
      <RouterLink
        to="/extrusion"
        class="flex items-center w-full text-left px-5 py-4 rounded-md text-lg font-medium hover:bg-gray-100"
        :class="$route.path.startsWith('/extrusion') ? 'bg-indigo-50 text-indigo-700' : ''"
      >
        <Factory class="w-7 h-7 mr-3"/>
        압출
      </RouterLink>

      <!-- 재단 -->
      <RouterLink
        to="/cutting"
        class="flex items-center w-full text-left px-5 py-4 rounded-md text-lg font-medium hover:bg-gray-100"
        :class="$route.path.startsWith('/cutting') ? 'bg-indigo-50 text-indigo-700' : ''"
      >
        <Package class="w-7 h-7 mr-3"/>
        재단
      </RouterLink>

      <!-- 성형 -->
      <RouterLink
        to="/molding"
        class="flex items-center w-full text-left px-5 py-4 rounded-md text-lg font-medium hover:bg-gray-100"
        :class="$route.path.startsWith('/molding') ? 'bg-indigo-50 text-indigo-700' : ''"
      >
        <BarChart3 class="w-7 h-7 mr-3"/>
        성형
      </RouterLink>

      <!-- 가류 -->
      <RouterLink
        to="/curing"
        class="flex items-center w-full text-left px-5 py-4 rounded-md text-lg font-medium hover:bg-gray-100"
        :class="$route.path.startsWith('/curing') ? 'bg-indigo-50 text-indigo-700' : ''"
      >
        <Zap class="w-7 h-7 mr-3"/>
        가류
      </RouterLink>

      <!-- 검사 -->
      <RouterLink
        to="/inspection"
        class="flex items-center w-full text-left px-5 py-4 rounded-md text-lg font-medium hover:bg-gray-100"
        :class="$route.path.startsWith('/inspection') ? 'bg-indigo-50 text-indigo-700' : ''"
      >
        <CheckCircle class="w-7 h-7 mr-3"/>
        검사
      </RouterLink>

      <!-- 출하 -->
      <RouterLink
        to="/delivery"
        class="flex items-center w-full text-left px-5 py-4 rounded-md text-lg font-medium hover:bg-gray-100"
        :class="$route.path.startsWith('/delivery') ? 'bg-indigo-50 text-indigo-700' : ''"
      >
        <Truck class="w-7 h-7 mr-3"/>
        출하
      </RouterLink>

      <!-- 품질 관리 -->
      <RouterLink
        to="/quality"
        class="flex items-center w-full text-left px-5 py-4 rounded-md text-lg font-medium hover:bg-gray-100"
        :class="$route.path.startsWith('/quality') ? 'bg-indigo-50 text-indigo-700' : ''"
      >
        <Shield class="w-7 h-7 mr-3" />
        품질 관리
      </RouterLink>

      <!-- 자재 관리 (아이콘 추가) -->
      <RouterLink
        to="/materials"
        class="flex items-center w-full text-left px-5 py-4 rounded-md text-lg font-medium hover:bg-gray-100"
        :class="$route.path.startsWith('/materials') ? 'bg-indigo-50 text-indigo-700' : ''"
      >
        <Boxes class="w-7 h-7 mr-3" />
        자재 관리
      </RouterLink>

      <!-- 로트 추적 (아이콘 추가) -->
      <RouterLink
        to="/lot-tracking"
        class="flex items-center w-full text-left px-5 py-4 rounded-md text-lg font-medium hover:bg-gray-100"
        :class="$route.path.startsWith('/lot-tracking') ? 'bg-indigo-50 text-indigo-700' : ''"
      >
        <Route class="w-7 h-7 mr-3" />
        로트 추적
      </RouterLink>
      <RouterLink class="flex items-center w-full text-left px-5 py-4 rounded-md text-lg font-medium hover:bg-gray-100" to="/equipment">
        설비 관리
      </RouterLink>

      <!-- 보고서 관리 (아이콘 + 비활성 스타일) -->
      <button
        class="flex items-center w-full text-left px-5 py-4 rounded-md text-lg font-medium text-gray-400 bg-gray-50 cursor-not-allowed"
        disabled
        title="준비 중"
      >
        <FileBarChart class="w-7 h-7 mr-3" />
        보고서 관리
      </button>
    </nav>


      <div class="p-4 text-sm text-gray-400 border-t">v1.0.0 | 2024.12.16</div>
    </aside>

    <!-- Main -->
    <main class="flex-1 overflow-y-auto p-6 bg-gray-50">
      <RouterView />
    </main>
  </div>
</template>

<script setup>
import { 
  Factory, Package, BarChart3, Truck, CheckCircle, RotateCcw, Zap, Shield,LayoutDashboard, Boxes, Route, FileBarChart
} from 'lucide-vue-next'
</script>
