import { create } from '@tanstack/react-store';
import type { DomainUser, DomainWallet } from '@/client/types.gen';

interface AuthState {
  token: string | null;
  user: DomainUser | null;
  wallets: DomainWallet[];
  selectedWalletId: number | null;
}

const stored = typeof window !== 'undefined' ? localStorage.getItem('auth') : null;
const initial: AuthState = stored ? JSON.parse(stored) : { token: null, user: null, wallets: [], selectedWalletId: null };

function persist(state: AuthState) {
  localStorage.setItem('auth', JSON.stringify(state));
}

export const authStore = create<AuthState>({
  initialState: initial,
  mutations: {
    login(state, payload: { token: string; user: DomainUser; wallets: DomainWallet[] }) {
      state.token = payload.token;
      state.user = payload.user;
      state.wallets = payload.wallets;
      state.selectedWalletId = payload.wallets[0]?.id ?? null;
      persist(state);
    },
    logout(state) {
      state.token = null;
      state.user = null;
      state.wallets = [];
      state.selectedWalletId = null;
      persist(state);
    },
    selectWallet(state, walletId: number) {
      state.selectedWalletId = walletId;
      persist(state);
    },
    setWallets(state, wallets: DomainWallet[]) {
      state.wallets = wallets;
      if (!state.selectedWalletId && wallets.length > 0) {
        state.selectedWalletId = wallets[0].id;
      }
      persist(state);
    },
  },
});
