import axios from 'axios';

const apiClient = axios.create({
  baseURL: '/api',
  headers: {
    'Content-Type': 'application/json',
  },
});

export const searchEquipmentByProcess = (processName) => {
  return apiClient.get('/equipment/search', { params: { process: processName } });
};

export const getEquipmentDetails = (machineCode) => {
  return apiClient.get(`/equipment/${machineCode}`);
};