import { createRouter, createWebHistory } from 'vue-router'
import MixingList from '../pages/MixingList.vue'
import MixingDetail from '../pages/MixingDetail.vue'
import ExtrusionList from '../pages/ExtrusionList.vue'      
import ExtrusionDetail from '../pages/ExtrusionDetail.vue'  
import CuttingList from '../pages/CuttingList.vue'
import CuttingDetail from '../pages/CuttingDetail.vue'
import InspectionList from '../pages/InspectionList.vue'
import InspectionDetail from '../pages/InspectionDetail.vue'
import Materials from '../pages/Materials.vue'
import LotTracking from '../pages/LotTracking.vue'
import EquipmentDashboard from '../pages/EquipmentDashboard.vue'


const routes = [
  { path: '/', redirect: '/mixing' },
  { path: '/mixing', name: 'MixingList', component: MixingList },
  { path: '/mixing/:id', name: 'MixingDetail', component: MixingDetail, props: true },
  { path: '/extrusion', name: 'ExtrusionList', component: ExtrusionList },         // ✅ 등록
  { path: '/extrusion/:id', name: 'ExtrusionDetail', component: ExtrusionDetail, props: true }, // ✅ 등록
  { path: '/cutting', name: 'CuttingList', component: CuttingList },
  { path: '/cutting/:id', name: 'CuttingDetail', component: CuttingDetail, props: true },
  { path: '/inspection', name: 'InspectionList', component: InspectionList },
  { path: '/inspection/:id', name: 'InspectionDetail', component: InspectionDetail, props: true },
  { path: '/materials', name: 'Materials', component: Materials },
  { path: '/lot-tracking', name: 'LotTracking', component: LotTracking },
  { path: '/equipment', name: 'EquipmentDashboard', component: EquipmentDashboard },
]

export default createRouter({
  history: createWebHistory(),
  routes,
})
