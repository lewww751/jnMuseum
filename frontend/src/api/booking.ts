import api from './index';

export interface BookingGuest {
  id: number;
  bookingId: number;
  guestType: string;
  name: string;
  idCard: string;
}

export interface Booking {
  id: number;
  code: string;
  visitDate: string;
  slot: string;
  phone: string;
  status: string;
  createdAt: string;
  cancelledAt: string;
  checkedInAt: string;
  guests: BookingGuest[];
}

export interface CreateBookingParams {
  phone: string;
  visitDate: string;
  slot: string;
  guests: {
    name: string;
    idCard: string;
  }[];
}

export const createBooking = (params: CreateBookingParams) => {
  return api.post<Booking>('/booking', params);
};

export const cancelBooking = (code: string, phone: string) => {
  return api.post('/booking/cancel', { code, phone });
};

export const checkIn = (code: string) => {
  return api.post('/booking/check-in', { code });
};

export const getBookingByCode = (code: string) => {
  return api.get<Booking>(`/booking/lookup?code=${code}`);
};

export const getBookingsByPhone = (phone: string, idCard?: string) => {
  const params: any = { phone };
  if (idCard) params.idCard = idCard;
  return api.get<Booking[]>(`/booking/lookup`, { params });
};