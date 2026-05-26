import request from '@/utils/request'

export function getDashboard() {
  return request({ url: '/statistics/dashboard', method: 'get' })
}
