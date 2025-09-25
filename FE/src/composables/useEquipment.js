import { ref, computed } from 'vue'
import { EquipmentMockRepository } from '../api/equipment.mock'
import { EquipmentApiRepository } from '../api/equipment.api'


// const repo = import.meta.env.VITE_USE_MOCK === 'true'
//   ? EquipmentMockRepository
//   : EquipmentApiRepository
const repo = EquipmentMockRepository
export function useEquipment() {
  const list = ref([])
  const selectedId = ref('')
  const selected = computed(() =>
    list.value.find(e => e.id === selectedId.value) || null
  )

  async function load() {
    list.value = await repo.list()
    if (list.value.length && !selectedId.value) {
      selectedId.value = list.value[0].id
    }
  }

  async function loadMaintSummary() {
    return repo.getMaintSummary()
  }

  return { list, selectedId, selected, load, loadMaintSummary }
 }