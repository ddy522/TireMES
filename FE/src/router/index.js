import { createRouter, createWebHistory } from 'vue-router'
import MixingList from '../pages/MixingList.vue'
import MixingDetail from '../pages/MixingDetail.vue'
import ExtrusionList from '../pages/ExtrusionList.vue'      
import ExtrusionDetail from '../pages/ExtrusionDetail.vue'  
import CuttingList from '../pages/CuttingList.vue'
import CuttingDetail from '../pages/CuttingDetail.vue'
import MoldingList from '../pages/CuttingList.vue'
import InspectionList from '../pages/InspectionList.vue'
import InspectionDetail from '../pages/InspectionDetail.vue'
import Materials from '../pages/Materials.vue'
import LotTracking from '../pages/LotTracking.vue'
import CuringList from '../pages/CuringList.vue'

import QualityLayout from '../pages/quality/QualityLayout.vue'
import QualityOverview from '../pages/quality/QualityOverview.vue'
import VisionInspection from '../pages/quality/VisionInspection.vue'
import InspectionAdmin from '../pages/quality/InspectionAdmin.vue'
import DefectAnalysis from '../pages/quality/DefectAnalysis.vue'
import QualityReports from '../pages/quality/QualityReports.vue'
import QualityStandards from '../pages/quality/QualityStandards.vue'

const routes = [
  { path: '/', redirect: '/mixing' },
  { path: '/mixing', name: 'MixingList', component: MixingList },
  { path: '/mixing/:id', name: 'MixingDetail', component: MixingDetail, props: true, meta: { process: 'mixing' } },
  { path: '/extrusion', name: 'ExtrusionList', component: ExtrusionList },     
  { path: '/extrusion/:id', name: 'ExtrusionDetail', component: ExtrusionDetail, props: true }, 
  { path: '/cutting', name: 'CuttingList', component: CuttingList },
  { path: '/cutting/:id', name: 'CuttingDetail', component: CuttingDetail, props: true },
  { path: '/curing', name: 'CuringList', component: CuringList },
  // { path: '/curing/:id', name: 'CuringDetail', component: CuringDetail, props: true },
  { path: '/molding', name: 'MoldingList', component: MoldingList },
  { path: '/inspection', name: 'InspectionList', component: InspectionList },
  { path: '/inspection/:id', name: 'InspectionDetail', component: InspectionDetail, props: true },
  { path: '/materials', name: 'Materials', component: Materials },
  { path: '/lot-tracking', name: 'LotTracking', component: LotTracking },
    {
    path: '/quality',
    component: QualityLayout,
    children: [
      { path: '',            name: 'QualityOverview',    component: QualityOverview },
      { path: 'vision',      name: 'VisionInspection',   component: VisionInspection },
      { path: 'inspection',  name: 'InspectionAdmin',    component: InspectionAdmin },
      { path: 'defects',     name: 'DefectAnalysis',     component: DefectAnalysis },
      { path: 'reports',     name: 'QualityReports',     component: QualityReports },
      { path: 'standards',   name: 'QualityStandards',   component: QualityStandards },
    ],
  },
]

export default createRouter({
  history: createWebHistory(),
  routes,
})
