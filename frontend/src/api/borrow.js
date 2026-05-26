import request from '@/utils/request'

export function getBorrows(params) {
  return request({ url: '/borrows', method: 'get', params })
}

export function getBorrowDetails() {
  return request({ url: '/borrows/details', method: 'get' })
}

export function getOverdueBorrows() {
  return request({ url: '/statistics/overdue', method: 'get' })
}

export function addBorrow(data) {
  return request({ url: '/borrows', method: 'post', data })
}

export function updateBorrow(id, data) {
  return request({ url: `/borrows/${id}`, method: 'put', data })
}

export function returnBorrow(id) {
  return request({ url: `/borrows/${id}/return`, method: 'put' })
}

export function deleteBorrow(id) {
  return request({ url: `/borrows/${id}`, method: 'delete' })
}
