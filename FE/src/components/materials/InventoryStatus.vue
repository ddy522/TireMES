<template>
  <div class="space-y-4">
    <!-- 상단 KPI -->
    <div class="grid grid-cols-1 md:grid-cols-4 gap-4">
      <KpiCard title="총 자재 가치" value="₩1.8B" sub="전월 대비 +3.2%" />
      <KpiCard title="부족 자재" value="3" sub="긴급 보충 필요" />
      <KpiCard title="자재 회전율" value="5.2" sub="전월 대비 +0.4" />
      <KpiCard title="재고 정확도" value="99.1%" sub="전월 대비 +0.2%" />
    </div>

    <!-- 검색/필터 -->
    <div class="flex items-center gap-2">
      <input class="border rounded-md px-3 py-2 text-sm flex-1" placeholder="자재 검색…" />
      <select class="border rounded-md px-3 py-2 text-sm">
        <option>카테고리</option>
        <option>고무원료</option>
        <option>충전재</option>
        <option>보강재</option>
      </select>
      <button class="btn bg-white border">필터</button>
      <button class="btn bg-white border">내보내기</button>
      <button class="btn-primary">+ 자재 등록</button>
    </div>

    <!-- 재고 테이블 -->
    <div class="card overflow-x-auto">
      <table class="min-w-full text-sm">
        <thead>
          <tr class="bg-gray-100 text-gray-700">
            <th class="px-3 py-2 text-left">자재명</th>
            <th class="px-3 py-2 text-left">카테고리</th>
            <th class="px-3 py-2 text-right">현재 재고</th>
            <th class="px-3 py-2 text-right">안전 재고</th>
            <th class="px-3 py-2 text-left">단위</th>
            <th class="px-3 py-2 text-left">상태</th>
            <th class="px-3 py-2 text-left">공급업체</th>
            <th class="px-3 py-2 text-left">작업</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="row in rows" :key="row.name" class="border-t">
            <td class="px-3 py-2">{{ row.name }}</td>
            <td class="px-3 py-2">{{ row.category }}</td>
            <td class="px-3 py-2 text-right">{{ row.stock }}</td>
            <td class="px-3 py-2 text-right">{{ row.safety }}</td>
            <td class="px-3 py-2">톤</td>
            <td class="px-3 py-2">
              <span :class="badge(row.status)" class="badge">{{ row.status }}</span>
            </td>
            <td class="px-3 py-2">{{ row.vendor }}</td>
            <td class="px-3 py-2">
              <div class="flex items-center gap-2">
                <button class="btn bg-white border">모니터링</button>
                <button class="btn bg-white border">상세</button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup>
const rows = [
  { name: '천연고무', category: '고무원료', stock: 850, safety: 500, status: '충분', vendor: '태국 고무' },
  { name: '합성고무', category: '고무원료', stock: 400, safety: 300, status: '충분', vendor: '한국석유화학' },
  { name: '스틸코드', category: '보강재',   stock: 850, safety: 1000, status: '주의', vendor: '고려제강' },
  { name: '카본블랙', category: '충전재',   stock: 120, safety: 300, status: '부족', vendor: 'OCI' },
  { name: '가황제',   category: '가황제',   stock: 45,  safety: 50,  status: '주의', vendor: '한국화학' },
  { name: '가황촉진제',category: '촉진제',  stock: 25,  safety: 20,  status: '충분', vendor: '동양화학' },
]

function badge(s) {
  if (s === '충분') return 'badge-green'
  if (s === '주의') return 'badge-yellow'
  if (s === '부족') return 'badge-red'
  return 'badge-gray'
}
</script>
