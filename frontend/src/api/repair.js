import request from '@/utils/request'

export function getRepairs(params) {
  return request({ url: '/repairs', method: 'get', params })
}

export function getRepairDetails() {
  return request({ url: '/repairs/details', method: 'get' })
}

export function addRepair(data) {
  return request({ url: '/repairs', method: 'post', data })
}

export function updateRepair(id, data) {
  return request({ url: `/repairs/${id}`, method: 'put', data })
}

export function deleteRepair(id) {
  return request({ url: `/repairs/${id}`, method: 'delete' })
}
