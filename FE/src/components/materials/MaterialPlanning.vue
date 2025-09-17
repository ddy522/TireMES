<template>
  <div class="space-y-4">
    <!-- 계획 입력 -->
    <div class="card">
      <div class="grid grid-cols-1 md:grid-cols-4 gap-3">
        <select class="border rounded-md px-3 py-2 text-sm">
          <option>기간 선택</option>
          <option>1주</option>
          <option>1개월</option>
          <option>3개월</option>
        </select>
        <input class="border rounded-md px-3 py-2 text-sm" placeholder="타이어 생산량" />
        <select class="border rounded-md px-3 py-2 text-sm">
          <option>제품 선택</option>
          <option>타이어 A</option>
          <option>타이어 B</option>
        </select>
        <button class="btn-primary">소요량 계산</button>
      </div>
    </div>

    <!-- 예상 소요량 테이블 -->
    <div class="card overflow-x-auto">
      <table class="min-w-full text-sm">
        <thead>
          <tr class="bg-gray-100 text-gray-700">
            <th class="px-3 py-2 text-left">자재명</th>
            <th class="px-3 py-2 text-right">현재 재고</th>
            <th class="px-3 py-2 text-right">예상 소요량</th>
            <th class="px-3 py-2 text-right">부족량</th>
            <th class="px-3 py-2 text-left">발주 권장</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="r in rows" :key="r.name" class="border-t">
            <td class="px-3 py-2">{{ r.name }}</td>
            <td class="px-3 py-2 text-right">{{ r.stock }}톤</td>
            <td class="px-3 py-2 text-right">{{ r.need }}톤</td>
            <td class="px-3 py-2 text-right">
              <span :class="r.shortage>0?'text-red-600 font-semibold':''">{{ shortageText(r.shortage) }}</span>
            </td>
            <td class="px-3 py-2">
              <button class="btn bg-white border" :disabled="r.shortage<=0">
                {{ r.shortage>0 ? '발주 필요' : '불필요' }}
              </button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup>
const rows = [
  { name: '천연고무', stock: 850, need: 400, shortage: 0 },
  { name: '카본블랙', stock: 120, need: 180, shortage: 60 },
  { name: '스틸코드', stock: 850, need: 200, shortage: 0 },
]
function shortageText(n){ return n>0 ? `${n}톤` : '-' }
</script>
