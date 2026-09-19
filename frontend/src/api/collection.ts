import api from './index';

export interface CollectionItem {
  id: number;
  name: string;
  era: string;
  category: string;
  description: string;
  image: string;
  published: boolean;
  sortOrder: number;
}

export const getCollectionItems = () => {
  return api.get<CollectionItem[]>('/collections');
};

export const getCollectionItemById = (id: number) => {
  return api.get<CollectionItem>(`/collections/${id}`);
};