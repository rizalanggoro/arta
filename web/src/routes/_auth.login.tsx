import { createFileRoute, Link, useNavigate } from '@tanstack/react-router';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { useState } from 'react';
import { postApiAuthLogin } from '@/client/sdk.gen';
import { authStore } from '@/stores/auth';
import { getApiAuthMe, listWallets } from '@/client/sdk.gen';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';

export const Route = createFileRoute('/_auth/login')({
  component: LoginPage,
});

function LoginPage() {
  const navigate = useNavigate();
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');

  const loginMutation = useMutation({
    mutationFn: async () => {
      const res = await postApiAuthLogin({
        body: { email, password },
      });
      if (res.error) throw res.error;
      return res.data;
    },
    onSuccess: async (data) => {
      authStore.state.login({
        token: data.token,
        user: { id: 0, name: data.name, email: data.email, created_at: '', updated_at: '' },
        wallets: [],
      });
      // Fetch full profile + wallets
      const meRes = await getApiAuthMe({
        headers: { Authorization: `Bearer ${data.token}` },
      });
      if (meRes.data) {
        const walletRes = await listWallets({
          headers: { Authorization: `Bearer ${data.token}` },
        });
        const wallets = walletRes.data?.wallets?.map(w => w.data) ?? [];
        authStore.state.login({
          token: data.token,
          user: meRes.data.data,
          wallets,
        });
      }
      navigate({ to: '/' });
    },
    onError: (err: { message?: string }) => {
      setError(err.message ?? 'Login failed');
    },
  });

  return (
    <Card className="w-full max-w-md">
      <CardHeader>
        <CardTitle>Login</CardTitle>
        <CardDescription>Sign in to your ARTA account</CardDescription>
      </CardHeader>
      <CardContent>
        <form
          onSubmit={(e) => {
            e.preventDefault();
            setError('');
            loginMutation.mutate();
          }}
          className="space-y-4"
        >
          {error && (
            <p className="text-sm text-destructive">{error}</p>
          )}
          <div className="space-y-2">
            <Label htmlFor="email">Email</Label>
            <Input
              id="email"
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
            />
          </div>
          <div className="space-y-2">
            <Label htmlFor="password">Password</Label>
            <Input
              id="password"
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
            />
          </div>
          <Button type="submit" className="w-full" disabled={loginMutation.isPending}>
            {loginMutation.isPending ? 'Signing in...' : 'Sign In'}
          </Button>
          <p className="text-center text-sm text-muted-foreground">
            Don't have an account?{' '}
            <Link to="/register" className="text-primary underline">
              Register
            </Link>
          </p>
        </form>
      </CardContent>
    </Card>
  );
}
