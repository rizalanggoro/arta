import { createFileRoute, redirect, Outlet } from '@tanstack/react-router';
import { authStore } from '@/stores/auth';

export const Route = createFileRoute('/_auth')({
  beforeLoad: () => {
    const { token } = authStore.state;
    if (token) {
      throw redirect({ to: '/' });
    }
  },
  component: AuthLayout,
});

function AuthLayout() {
  return (
    <div className="flex min-h-screen items-center justify-center bg-muted/30">
      <Outlet />
    </div>
  );
}
