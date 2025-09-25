export const EquipmentMockRepository = {
  async list() {
    return [
      {
        id: 'eq-mix-01',
        proc: '믹싱',
        name: '믹싱기 A',
        status: '가동',
        state: '정상',
        metrics: {
          temp: 70, humid: 42, power: 11.2, dust: 0.05, vocs: 0.01,
          torque: 120, thickness: null, length: null, tension: null, pressure: null
        }
      },
      {
        id: 'eq-ext-01',
        proc: '압출',
        name: '압출기 A',
        status: '가동',
        state: '정상',
        metrics: {
          temp: 65, humid: 40, power: 9.3, dust: 0.03, vocs: 0.02,
          torque: null, thickness: 2.5, length: 120, tension: null, pressure: null
        }
      },
      {
        id: 'eq-form-01',
        proc: '성형',
        name: '성형기 A',
        status: '가동',
        state: '정상',
        metrics: {
          temp: 55, humid: 38, power: 10.1, dust: 0.04, vocs: 0.01,
          torque: null, thickness: null, length: null, tension: 15, pressure: null
        }
      },
      {
        id: 'eq-vul-01',
        proc: '가류',
        name: '가류기 A',
        status: '가동',
        state: '정상',
        metrics: {
          temp: 180, humid: 35, power: 12.4, dust: 0.02, vocs: 0.01,
          torque: null, thickness: null, length: null, tension: null, pressure: 5.2
        }
      }
    ]
  },

  async getMaintSummary() {
    return { last: '2025-09-10', owner: '박민수', type: '정기점검' }
  }
}
// DB 연결시
// import axios from 'axios'

// const api = axios.create({
//   baseURL: import.meta.env.VITE_API_BASE_URL
// })

// export const EquipmentApiRepository = {
//   async list() {
//     const { data } = await api.get('/api/equipment/list')
//     return data
//   },

//   async getMaintSummary() {
//     const { data } = await api.get('/api/equipment/maint/summary')
//     return data
//   }
// }
