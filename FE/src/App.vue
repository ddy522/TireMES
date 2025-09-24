<router-view :key="$route.path" />

<template>
  <div class="flex h-screen overflow-hidden">
    <!-- Sidebar -->
    <aside class="w-64 bg-white border-r border-gray-200 flex flex-col">
      <div class="p-6 border-b border-gray-200">
        <h2 class="text-xl font-bold text-gray-900">MES 시스템</h2>
        <p class="text-sm text-gray-600">제조실행시스템</p>
      </div>
      <nav class="flex-1 p-4 space-y-2">
        <!-- 공정 버튼 -->
        <RouterLink class="flex items-center w-full text-left px-3 py-2 rounded hover:bg-gray-100" to="/mixing">
          <RotateCcw class="w-5 h-5 mr-2"/>
          믹싱
        </RouterLink>

        <RouterLink class="flex items-center w-full text-left px-3 py-2 rounded hover:bg-gray-100" to="/extrusion">
          <Factory class="w-5 h-5 mr-2"/>
          압출
        </RouterLink>

        <RouterLink class="flex items-center w-full text-left px-3 py-2 rounded hover:bg-gray-100" to="/cutting">
          <Package class="w-5 h-5 mr-2"/>
          재단
        </RouterLink>

        <RouterLink class="flex items-center w-full text-left px-3 py-2 rounded hover:bg-gray-100" to="/molding">
          <BarChart3 class="w-5 h-5 mr-2"/>
          성형
        </RouterLink>

        <RouterLink class="flex items-center w-full text-left px-3 py-2 rounded hover:bg-gray-100" to="/curing">
          <Zap class="w-5 h-5 mr-2"/>
          가류
        </RouterLink>

        <RouterLink class="flex items-center w-full text-left px-3 py-2 rounded hover:bg-gray-100" to="/inspection">
          <CheckCircle class="w-5 h-5 mr-2"/>
          검사
        </RouterLink>

        <RouterLink class="flex items-center w-full text-left px-3 py-2 rounded hover:bg-gray-100" to="/delivery">
          <Truck class="w-5 h-5 mr-2"/>
          출하
        </RouterLink>

        <RouterLink class="flex items-center w-full text-left px-3 py-2 rounded hover:bg-gray-100" to="/quality">
          <Shield class="w-5 h-5 mr-2" />
          품질 관리
        </RouterLink>


        <RouterLink class="block w-full text-left px-3 py-2 rounded hover:bg-gray-100" to="/materials">
          자재 관리
        </RouterLink>

        <RouterLink class="block w-full text-left px-3 py-2 rounded hover:bg-gray-100" to="/lot-tracking">
          로트 추적
        </RouterLink>

        <button class="flex items-center w-full text-left px-3 py-2 rounded hover:bg-gray-100" disabled>
          보고서 관리
        </button>
      </nav>
      <div class="p-4 text-xs text-gray-400 border-t">v1.0.0 | 2024.12.16</div>
    </aside>

    <!-- Main -->
    <main class="flex-1 overflow-y-auto p-6 bg-gray-50">
      <RouterView />
    </main>
  </div>
</template>

<script setup>
import { 
  Factory, Package, BarChart3, Truck, CheckCircle, RotateCcw, Zap, Shield
} from 'lucide-vue-next'
</script>
