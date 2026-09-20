import apiClient from './client'
import type {
  AdminLoginRequest,
  AdminLoginResponse,
  DashboardStats,
  Exhibition,
  MuseumEvent,
  CollectionItem,
  CalendarDay,
  DaySettingRequest,
  SlotCapacityRequest,
  BookingQueryParams,
  BookingPageResult,
  BookingView,
  UploadResponse
} from '@/types'

// Admin API - Authentication
export const adminAuthApi = {
  login: (data: AdminLoginRequest) => apiClient.post<AdminLoginResponse>('/admin/login', data)
}

// Admin API - Dashboard
export const adminDashboardApi = {
  getStats: () => apiClient.get<DashboardStats>('/admin/dashboard')
}

// Admin API - Exhibitions
export const adminExhibitionApi = {
  getList: () => apiClient.get<Exhibition[]>('/admin/exhibitions'),
  getById: (id: number) => apiClient.get<Exhibition>(`/admin/exhibitions/${id}`),
  create: (data: Exhibition) => apiClient.post<Exhibition>('/admin/exhibitions', data),
  update: (id: number, data: Exhibition) => apiClient.put<Exhibition>(`/admin/exhibitions/${id}`, data),
  delete: (id: number) => apiClient.delete(`/admin/exhibitions/${id}`)
}

// Admin API - Events
export const adminEventApi = {
  getList: () => apiClient.get<MuseumEvent[]>('/admin/events'),
  getById: (id: number) => apiClient.get<MuseumEvent>(`/admin/events/${id}`),
  create: (data: MuseumEvent) => apiClient.post<MuseumEvent>('/admin/events', data),
  update: (id: number, data: MuseumEvent) => apiClient.put<MuseumEvent>(`/admin/events/${id}`, data),
  delete: (id: number) => apiClient.delete(`/admin/events/${id}`)
}

// Admin API - Collections
export const adminCollectionApi = {
  getList: () => apiClient.get<CollectionItem[]>('/admin/collections'),
  getById: (id: number) => apiClient.get<CollectionItem>(`/admin/collections/${id}`),
  create: (data: CollectionItem) => apiClient.post<CollectionItem>('/admin/collections', data),
  update: (id: number, data: CollectionItem) => apiClient.put<CollectionItem>(`/admin/collections/${id}`, data),
  delete: (id: number) => apiClient.delete(`/admin/collections/${id}`)
}

// Admin API - Guide
export const adminGuideApi = {
  update: (key: 'visit' | 'about', content: string) =>
    apiClient.put(`/admin/guide/${key}`, { content })
}

// Admin API - Calendar
export const adminCalendarApi = {
  getMonth: async (month: string): Promise<CalendarDay[]> => {
    const data = await apiClient.get<{ month: string; days: CalendarDay[] }>('/admin/calendar', { params: { month } })
    return data.days
  },
  setDaySetting: (data: DaySettingRequest) => apiClient.put('/admin/day-setting', data),
  removeDaySetting: (date: string) => apiClient.delete('/admin/day-setting', { params: { date } }),
  setSlotCapacity: (data: SlotCapacityRequest) => apiClient.put('/admin/slot-capacity', data)
}

// Admin API - Bookings
export const adminBookingApi = {
  getList: (params: BookingQueryParams) => apiClient.get<BookingPageResult>('/admin/bookings', { params }),
  checkIn: (code: string) => apiClient.post<BookingView>(`/admin/bookings/${code}/check-in`)
}

// Admin API - Upload
export const adminUploadApi = {
  upload: (file: File) => {
    const formData = new FormData()
    formData.append('file', file)
    return apiClient.post<UploadResponse>('/admin/upload', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
  }
}