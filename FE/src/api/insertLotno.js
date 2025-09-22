import axios from "axios"
import { API_BASE } from "./index.js"

export async function insertLotno(lotNo, proc) {
  try {
    console.log("lotNo:", lotNo, "proc:", proc);
    const response = await axios.post(`${API_BASE}/worksheet/insertLot`, {
      lotNo: lotNo,
      proc: proc
    })
    return response.data
  } catch (error) {
    console.error("insertLotno API 실패:", error)
    throw error
  }
}
