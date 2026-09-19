// Common types
export type Result<T = any> = {
  code: number
  message: string
  data: T
}

// Exhibition types
export type ExhibitionKind = 'PERMANENT' | 'TEMPORARY'
export type DisplayStatus = 'ONGOING' | 'UPCOMING' | 'ENDED'

export interface Exhibition {
  id: number
  title: string
  kind: ExhibitionKind
  hall: string
  summary: string
  content?: string
  coverImage: string
  startDate?: string
  endDate?: string
  displayStatus: DisplayStatus
  sortOrder?: number
  published: boolean
}

// Event types
export type EventStatus = 'UPCOMING' | 'ONGOING' | 'ENDED'

export interface MuseumEvent {
  id: number
  title: string
  description: string
  category: string
  startTime: string
  endTime?: string
  eventStatus: EventStatus
  published: boolean
}

// Collection types
export interface CollectionItem {
  id: number
  name: string
  description: string
  category: string
  period: string
  imageUrl: string
  isHighlight: boolean
  sortOrder: number
  published: boolean
}

// Guide types
export interface GuideContent {
  content: string
}

// Booking types
export type Slot = 'AM' | 'PM'
export type BookingStatus = 'ACTIVE' | 'CANCELLED' | 'CHECKED_IN'
export type GuestType = 'PRIMARY' | 'COMPANION'

export interface DayAvailability {
  date: string
  weekday: string
  isOpen: boolean
  closeReason?: string
  slots: SlotAvailability[]
}

export interface SlotAvailability {
  slot: Slot
  label: string
  capacity: number
  booked: number
  remaining: number
  bookable: boolean
  reason?: string
}

export interface BookingGuest {
  type: GuestType
  name: string
  idCard: string
}

export interface BookingRequest {
  visitDate: string
  slot: Slot
  phone: string
  guests: BookingGuest[]
}

export interface BookingResponse {
  code: string
  visitDate: string
  slotLabel: string
  guests: BookingGuestView[]
  createdAt: string
}

export interface BookingGuestView {
  name: string
  idCardMasked: string
  type: GuestType
}

export interface BookingView {
  code: string
  visitDate: string
  weekday: string
  slot: Slot
  slotLabel: string
  status: BookingStatus
  statusLabel: string
  cancellable: boolean
  cancelDeadline: string
  createdAt: string
  guests: BookingGuestView[]
}

export interface CancelBookingRequest {
  code: string
  phone: string
}

// Admin types
export interface AdminLoginRequest {
  username: string
  password: string
}

export interface AdminLoginResponse {
  token: string
}

export interface DashboardStats {
  today: SlotCapacityView[]
  tomorrow: SlotCapacityView[]
  last7Days: DailyBookingView[]
  stats: {
    activeBookings: number
    todayCheckIns: number
    exhibitionCount: number
    eventCount: number
    collectionCount: number
  }
}

export interface SlotCapacityView {
  slot: Slot
  label: string
  capacity: number
  booked: number
}

export interface DailyBookingView {
  date: string
  booked: number
}

// Calendar types
export interface CalendarDay {
  date: string
  weekday: string
  defaultOpen: boolean
  isOpen: boolean
  override: {
    isOpen: boolean
    reason?: string
  } | null
  slots: {
    AM: SlotCapacityView
    PM: SlotCapacityView
  }
}

export interface DaySettingRequest {
  date: string
  isOpen: boolean
  reason?: string
}

export interface SlotCapacityRequest {
  date: string
  slot: Slot
  capacity: number
}

// Booking management types
export interface BookingQueryParams {
  visitDate?: string
  slot?: Slot
  status?: BookingStatus
  keyword?: string
  page: number
  size: number
}

export interface BookingPageResult {
  records: BookingView[]
  total: number
}

// Upload types
export interface UploadResponse {
  url: string
}