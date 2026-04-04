import axios from 'axios';
import { auth } from './auth';
import { patientApi } from '../api/patient';

const service = axios.create({
  baseURL: '/api',
  timeout: 10000,
});

let isRefreshing = false;
let refreshSubscribers: ((token: string) => void)[] = [];

function subscribeTokenRefresh(callback: (token: string) => void) {
  refreshSubscribers.push(callback);
}

function onTokenRefreshed(token: string) {
  refreshSubscribers.forEach(callback => callback(token));
  refreshSubscribers = [];
}

service.interceptors.request.use(
  (config) => {
    const token = auth.getAccessToken();
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

service.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;

    if (error.response?.status === 401 && !originalRequest._retry) {
      if (isRefreshing) {
        return new Promise((resolve) => {
          subscribeTokenRefresh((token: string) => {
            originalRequest.headers.Authorization = `Bearer ${token}`;
            resolve(service(originalRequest));
          });
        });
      }

      originalRequest._retry = true;
      isRefreshing = true;

      const refreshToken = auth.getRefreshToken();
      if (!refreshToken) {
        auth.clearTokens();
        window.location.href = '/patient/login';
        return Promise.reject(error);
      }

      try {
        const res = await patientApi.refreshToken(refreshToken);
        auth.setTokens({
          accessToken: res.accessToken,
          refreshToken: res.refreshToken,
          expiresIn: res.expiresIn,
        });
        onTokenRefreshed(res.accessToken);
        originalRequest.headers.Authorization = `Bearer ${res.accessToken}`;
        isRefreshing = false;
        return service(originalRequest);
      } catch (refreshError) {
        auth.clearTokens();
        isRefreshing = false;
        window.location.href = '/patient/login';
        return Promise.reject(refreshError);
      }
    }

    return Promise.reject(error);
  }
);

export default service;
