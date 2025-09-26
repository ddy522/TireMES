<template>
  <div class="space-y-4">
    <div class="flex items-center justify-between mb-2">
      <h4 class="section-title text-xl font-bold">LOT 목록</h4>
      <div class="text-sm text-gray-500">{{ lots.length }}개 LOT</div>
    </div>

    <div v-for="lot in lots" :key="lot.lotno" class="border rounded-xl p-4 shadow-md hover:shadow-lg transition bg-white">
      <div class="flex justify-between items-center mb-3">
        <div class="space-y-1">
          <div class="text-lg font-semibold text-gray-900">LOTNO : {{ lot.lotno }}</div>
          <div class="text-sm text-gray-500">{{ lot.lotDate }} · 생성: {{ lot.createdAt }}</div>
        </div>
      </div>

      <div class="grid grid-cols-2 gap-3 mb-3">
        <div class="border rounded-lg p-3 bg-gray-50">
          <div class="text-gray-500 text-sm">품번</div>
          <div class="font-semibold text-gray-800">{{ lot.partCode }}</div>
        </div>
        <div class="border rounded-lg p-3 bg-gray-50">
          <div class="text-gray-500 text-sm">품명</div>
          <div class="font-semibold text-gray-800">{{ lot.partName }}</div>
        </div>
        <div class="border rounded-lg p-3 bg-gray-50">
          <div class="text-gray-500 text-sm">수량</div>
          <div class="font-semibold text-gray-800">{{ lot.qty }}개</div>
        </div>
        <div class="border rounded-lg p-3 bg-gray-50">
          <div class="text-gray-500 text-sm">공정번호</div>
          <div class="font-semibold text-gray-800">{{ lot.processNo }}</div>
        </div>
      </div>

      <div class="flex justify-end gap-2">
        <button class="btn bg-white border text-sm px-3 py-1 hover:bg-gray-100 transition" @click="openModal(lot)">🔍 추적</button>
        <button class="btn bg-blue-500 text-white border text-sm px-3 py-1 hover:bg-blue-600 transition" @click="openQRModal(lot)">🏷️ QR 발행</button>
        <button class="btn bg-red-500 text-white border text-sm px-3 py-1 hover:bg-red-600 transition" @click="deleteLot(lot.lotno)">LOT 삭제</button>
      </div>
    </div>

    <div v-if="showModal" class="fixed inset-0 bg-black/50 flex justify-center items-start pt-20 z-50">
      <div class="bg-white rounded-lg shadow-lg w-[600px] max-h-[80vh] overflow-y-auto p-4">
        <div class="flex justify-between items-center mb-3">
          <h5 class="font-bold text-lg">{{ selectedLot.lotno }} 세부 추적</h5>
          <button @click="closeModal" class="text-gray-500 hover:text-gray-800">&times;</button>
        </div>
        <table class="w-full text-sm border border-gray-200">
          <thead class="bg-gray-100">
            <tr>
              <th class="border px-2 py-1 text-left">LOT 번호</th>
              <th class="border px-2 py-1 text-left">공정번호</th>
              <th class="border px-2 py-1 text-left">작업일자</th>
              <th class="border px-2 py-1 text-left">수량</th>
              <th class="border px-2 py-1 text-left">품번</th>
              <th class="border px-2 py-1 text-left">품명</th>
            </tr>
          </thead>
          <tbody>
            <tr>
              <td class="border px-2 py-1">{{ selectedLot.lotno }}</td>
              <td class="border px-2 py-1">{{ selectedLot.processNo }}</td>
              <td class="border px-2 py-1">{{ selectedLot.createdAt }}</td>
              <td class="border px-2 py-1">{{ selectedLot.qty }}</td>
              <td class="border px-2 py-1">{{ selectedLot.partCode }}</td>
              <td class="border px-2 py-1">{{ selectedLot.partName }}</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <div v-if="showQRModal" class="fixed inset-0 bg-black/50 flex items-center justify-center z-50" @click.self="closeQRModal">
        <div class="bg-white rounded-lg p-6 max-w-sm w-full mx-4 shadow-2xl">
            <div class="flex justify-between items-center mb-4">
                <h4 class="text-xl font-bold text-gray-800">LOT QR 코드 발행</h4>
                <button @click="closeQRModal" class="text-gray-500 hover:text-gray-800">&times;</button>
            </div>
            
            <div class="text-center mb-4">
                <p class="text-lg font-mono text-gray-800 mb-3">{{ qrLotNo }}</p>
                <canvas ref="qrCanvas" class="mx-auto border p-2"></canvas>
            </div>

            <div class="flex justify-end gap-2">
                <button @click="printQR" class="btn bg-white border text-sm px-3 py-2 hover:bg-gray-100 transition">인쇄</button>
                <button @click="closeQRModal" class="btn bg-blue-600 text-white text-sm px-3 py-2 hover:bg-blue-700 transition">닫기</button>
            </div>
        </div>
    </div>

  </div>
</template>

<script setup>
import { ref, watch, onMounted, defineExpose, nextTick } from 'vue'
import axios from 'axios'

const props = defineProps({
  worksheetSkey: String
})

const lots = ref([])
const loading = ref(false)

// 🌟 LOT 추적 모달 상태 (기존)
const showModal = ref(false)
const selectedLot = ref({})

// 🌟 QR 발행 모달 상태 (추가)
const showQRModal = ref(false)
const qrLotNo = ref('') // QR 코드로 발행할 LOT 번호
const qrCanvas = ref(null) // 캔버스 요소를 참조할 ref

// --- LOT 조회 및 삭제 (기존 로직 유지) ---

async function fetchLots(skey) {
  if (!skey) return
  try {
    loading.value = true
    const res = await axios.get(`http://localhost:8080/api/lot/lotList/${skey}`)
    lots.value = res.data
  } catch (e) {
    console.error('LOT 불러오기 실패:', e)
    lots.value = []
  } finally {
    loading.value = false
  }
}

watch(() => props.worksheetSkey, (newSkey) => {
  if (newSkey) fetchLots(newSkey)
})

onMounted(() => {
  if (props.worksheetSkey) fetchLots(props.worksheetSkey)
})

defineExpose({ fetchLots })

async function deleteLot(lotno) {
  if (!props.worksheetSkey) return
  if (!confirm(`LOT 번호 ${lotno}를 정말로 삭제하시겠습니까?`)) return;
  try {
    await axios.delete(`http://localhost:8080/api/lot/deleteLot`, {
      params: { lotNo: lotno, worksheetSkey: props.worksheetSkey }
    })
    lots.value = lots.value.filter(l => l.lotno !== lotno)
    alert('LOT가 성공적으로 삭제되었습니다.')
  } catch (e) {
    console.error('LOT 삭제 실패:', e)
    alert('LOT 삭제에 실패했습니다. 관리자에게 문의하세요.')
  }
}

// --- LOT 추적 모달 로직 (기존 유지) ---

function openModal(lot) {
  selectedLot.value = lot
  showModal.value = true
}
function closeModal() {
  showModal.value = false
}

// --- 🌟 QR 발행 로직 (추가) 🌟 ---

// QR 코드 생성 함수 (window.QRCode 객체 필요)
async function generateQRCode(lotNo) {
    if (!window.QRCode || !qrCanvas.value) {
        console.error("QRCode 라이브러리(window.QRCode)가 로드되지 않았거나 캔버스 요소가 없습니다.");
        return;
    }

    const ctx = qrCanvas.value.getContext('2d');
    ctx.clearRect(0, 0, qrCanvas.value.width, qrCanvas.value.height);

    // qrcode.js의 toCanvas 함수 사용
    window.QRCode.toCanvas(qrCanvas.value, lotNo, {
        errorCorrectionLevel: 'H',
        margin: 1,
        width: 250 // QR 코드 크기
    }, function (error) {
        if (error) console.error("QR 코드 생성 중 오류:", error);
    });
}

// QR 발행 모달 열기
async function openQRModal(lot) {
    qrLotNo.value = lot.lotno
    showQRModal.value = true
    
    // DOM이 업데이트되어 모달과 캔버스가 표시된 후 QR 코드 생성 시작
    await nextTick()
    generateQRCode(lot.lotno)
}

// QR 발행 모달 닫기
function closeQRModal() {
    showQRModal.value = false
    qrLotNo.value = ''
}

// 인쇄 기능
function printQR() {
    // 캔버스 내용을 새 창에 띄워 인쇄하는 방법이 가장 깔끔합니다.
    const canvas = qrCanvas.value;
    if (!canvas) return;

    const printWindow = window.open('', '_blank', 'width=300,height=400');
    
    // 인쇄용 HTML 구성
    printWindow.document.write(`
        <html>
            <head>
                <title>LOT QR 코드 인쇄</title>
                <style>
                    body { font-family: sans-serif; text-align: center; margin: 0; padding: 20px; }
                    .lot-info { font-size: 16px; margin-bottom: 10px; font-weight: bold; }
                    img { max-width: 100%; height: auto; display: block; margin: 0 auto; }
                </style>
            </head>
            <body>
                <div class="lot-info">${qrLotNo.value}</div>
                <img src="${canvas.toDataURL('image/png')}">
            </body>
        </html>
    `);
    
    printWindow.document.close();
    printWindow.focus();
    // 이미지 로드가 완료된 후 인쇄 실행
    printWindow.onload = function() {
        printWindow.print();
        printWindow.close();
    };
}
</script>

<style scoped>
/* Tailwind CSS 사용 가정 */
.section-title {
  @apply text-gray-800;
}
.btn {
  /* 버튼 기본 스타일 */
  @apply rounded-md font-medium;
}
/* 추가적인 스타일이 필요하면 여기에 정의하세요 */
</style>