const ACCESS_TOKEN_KEY = 'patient_access_token';
const REFRESH_TOKEN_KEY = 'patient_refresh_token';
const TOKEN_EXPIRE_TIME_KEY = 'patient_token_expire_time';

export interface TokenData {
  accessToken: string;
  refreshToken: string;
  expiresIn: number;
}

export const auth = {
  setTokens(data: TokenData) {
    localStorage.setItem(ACCESS_TOKEN_KEY, data.accessToken);
    localStorage.setItem(REFRESH_TOKEN_KEY, data.refreshToken);
    const expireTime = Date.now() + data.expiresIn * 1000;
    localStorage.setItem(TOKEN_EXPIRE_TIME_KEY, expireTime.toString());
  },

  getAccessToken(): string | null {
    return localStorage.getItem(ACCESS_TOKEN_KEY);
  },

  getRefreshToken(): string | null {
    return localStorage.getItem(REFRESH_TOKEN_KEY);
  },

  getTokenExpireTime(): number | null {
    const expireTime = localStorage.getItem(TOKEN_EXPIRE_TIME_KEY);
    return expireTime ? parseInt(expireTime, 10) : null;
  },

  clearTokens() {
    localStorage.removeItem(ACCESS_TOKEN_KEY);
    localStorage.removeItem(REFRESH_TOKEN_KEY);
    localStorage.removeItem(TOKEN_EXPIRE_TIME_KEY);
  },

  isLoggedIn(): boolean {
    const token = this.getAccessToken();
    const expireTime = this.getTokenExpireTime();
    if (!token || !expireTime) {
      return false;
    }
    return Date.now() < expireTime;
  },

  isTokenExpiringSoon(thresholdMs: number = 60000): boolean {
    const expireTime = this.getTokenExpireTime();
    if (!expireTime) {
      return true;
    }
    return Date.now() > expireTime - thresholdMs;
  },

  getAuthHeader(): string | null {
    const token = this.getAccessToken();
    return token ? `Bearer ${token}` : null;
  },
};

export default auth;
