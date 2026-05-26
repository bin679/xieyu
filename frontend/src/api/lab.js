import request from '@/utils/request'

export function getLabs(params) {
  return request({ url: '/labs', method: 'get', params })
}

export function getAllLabs() {
  return request({ url: '/labs/all', method: 'get' })
}

export function getLab(id) {
  return request({ url: `/labs/${id}`, method: 'get' })
}

export function addLab(data) {
  return request({ url: '/labs', method: 'post', data })
}

export function updateLab(id, data) {
  return request({ url: `/labs/${id}`, method: 'put', data })
}

export function deleteLab(id) {
  return request({ url: `/labs/${id}`, method: 'delete' })
}
