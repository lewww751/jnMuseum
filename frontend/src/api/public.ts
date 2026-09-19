import apiClient from './client'
import type {
  Exhibition,
  MuseumEvent,
  CollectionItem,
  GuideContent,
  DayAvailability,
  BookingRequest,
  BookingResponse,
  BookingView,
  CancelBookingRequest
} from '@/types'

// Public API - Exhibitions
export const exhibitionApi = {
  getList: (kind?: string) => apiClient.get<Exhibition[]>('/exhibitions', { params: { kind } }),
  getDetail: (id: number) => apiClient.get<Exhibition>(`/exhibitions/${id}`)
}

// Public API - Events
export const eventApi = {
  getList: () => apiClient.get<MuseumEvent[]>('/events')
}

// Public API - Collections
export const collectionApi = {
  getList: () => apiClient.get<CollectionItem[]>('/collections')
}

// Public API - Guide
export const guideApi = {
  getContent: (key: 'visit' | 'about') => apiClient.get<GuideContent>(`/guide/${key}`)
}

// Public API - Booking
export const bookingApi = {
  getAvailability: () => apiClient.get<DayAvailability[]>('/booking/availability'),
  create: (data: BookingRequest) => apiClient.post<BookingResponse>('/booking', data),
  lookupByCode: (code: string) => apiClient.get<BookingView[]>('/booking/lookup', { params: { code } }),
  lookupByPhoneAndIdCard: (phone: string, idCard: string) =>
    apiClient.get<BookingView[]>('/booking/lookup', { params: { phone, idCard } }),
  cancel: (data: CancelBookingRequest) => apiClient.post<BookingView>('/booking/cancel', data)
}