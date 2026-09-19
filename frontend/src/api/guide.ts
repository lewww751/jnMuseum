import api from './index';

export interface GuideContent {
  id: number;
  pageKey: string;
  content: string;
  updatedAt: string;
}

export const getGuideContent = (key: string) => {
  return api.get<GuideContent>(`/guide/${key}`);
};