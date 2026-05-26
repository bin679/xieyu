import request from '@/utils/request'

export function getEquipments(params) {
  return request({ url: '/equipment', method: 'get', params })
}

export function getAllEquipments() {
  return request({ url: '/equipment/all', method: 'get' })
}

export function getEquipment(id) {
  return request({ url: `/equipment/${id}`, method: 'get' })
}

export function addEquipment(data) {
  return request({ url: '/equipment', method: 'post', data })
}

export function updateEquipment(id, data) {
  return request({ url: `/equipment/${id}`, method: 'put', data })
}

export function deleteEquipment(id) {
  return request({ url: `/equipment/${id}`, method: 'delete' })
}
