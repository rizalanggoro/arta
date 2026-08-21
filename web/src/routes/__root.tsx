import { createRootRouteWithContext, redirect } from '@tanstack/react-router';
import { authStore } from '@/stores/auth';
import type { ReactNode } from 'react';

interface RouterContext {
  auth: {
    token: string | null;
    user: { name: string; email: string } | null;
    wallets: Array<{ id: number; name: string; type: string }>;
    selectedWalletId: number | null;
  };
}

export const Route = createRootRouteWithContext<RouterContext>()({
  component: RootComponent,
  beforeLoad: () => {
    const state = authStore.state;
    return {
      auth: {
        token: state.token,
        user: state.user ? { name: state.user.name, email: state.user.email } : null,
        wallets: state.wallets.map(w => ({ id: w.id, name: w.name, type: w.type })),
        selectedWalletId: state.selectedWalletId,
      },
    };
  },
});

function RootComponent({ children }: { children: ReactNode }) {
  return (
    <div className="min-h-screen bg-background">
      {children}
    </div>
  );
}
