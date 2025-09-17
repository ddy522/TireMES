<template>
  <div class="space-y-4">
    <h2 class="text-lg font-semibold text-gray-900">비전 검사</h2>

    <div class="grid grid-cols-1 lg:grid-cols-2 gap-4">
      <CameraCard title="표면 검사 카메라" code="CAM-01" :accuracy="99.2" status="정상" />
      <CameraCard title="치수 검사 카메라" code="CAM-02" :accuracy="98.8" status="정상" />
      <CameraCard title="패턴 검사 카메라" code="CAM-03" :accuracy="99.5" status="정상" />
      <CameraCard title="측면 검사 카메라" code="CAM-04" :accuracy="98.9" status="정상" />
    </div>
  </div>
</template>

<script setup>
import CameraCard from './CameraCard.vue'
</script>
