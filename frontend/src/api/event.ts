import api from './index';

export interface MuseumEvent {
  id: number;
  title: string;
  category: string;
  location: string;
  startTime: string;
  endTime: string;
  summary: string;
  coverImage: string;
  published: boolean;
}

export const getEvents = (category?: string) => {
  return api.get<MuseumEvent[]>('/events', { params: { category } });
};

export const getEventById = (id: number) => {
  return api.get<MuseumEvent>(`/events/${id}`);
};