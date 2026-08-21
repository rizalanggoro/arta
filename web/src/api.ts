import { createClient, setConfig } from '@/client/client.gen';

export const apiClient = setConfig({
  baseUrl: import.meta.env.VITE_API_BASE_URL as string,
});

export { createClient };
