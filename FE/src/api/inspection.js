// src/api/inspection.js
import axios from 'axios'

export function startInspectionOrders(workNos, runCount) {
  return axios.post('/api/inspection/start', { workNos, runCount })
}
