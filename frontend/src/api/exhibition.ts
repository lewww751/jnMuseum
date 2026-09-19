import api from './index';

export interface Exhibition {
  id: number;
  title: string;
  kind: string;
  hall: string;
  summary: string;
  content: string;
  coverImage: string;
  startDate: string;
  endDate: string;
  published: boolean;
  sortOrder: number;
}

export const getExhibitions = (kind?: string) => {
  return api.get<Exhibition[]>('/exhibitions', { params: { kind } });
};

export const getExhibitionById = (id: number) => {
  return api.get<Exhibition>(`/exhibitions/${id}`);
};