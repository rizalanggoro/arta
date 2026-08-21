import { createFileRoute, Link, useNavigate } from '@tanstack/react-router';
import { useMutation } from '@tanstack/react-query';
import { useState } from 'react';
import { postApiAuthRegister, getApiAuthMe, listWallets } from '@/client/sdk.gen';
import { authStore } from '@/stores/auth';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';

export const Route = createFileRoute('/_auth/register')({
  component: RegisterPage,
});

function RegisterPage() {
  const navigate = useNavigate();
  const [name, setName] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');

  const registerMutation = useMutation({
    mutationFn: async () => {
      const res = await postApiAuthRegister({
        body: { name, email, password },
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
      setError(err.message ?? 'Registration failed');
    },
  });

  return (
    <Card className="w-full max-w-md">
      <CardHeader>
        <CardTitle>Register</CardTitle>
        <CardDescription>Create your ARTA account</CardDescription>
      </CardHeader>
      <CardContent>
        <form
          onSubmit={(e) => {
            e.preventDefault();
            setError('');
            registerMutation.mutate();
          }}
          className="space-y-4"
        >
          {error && (
            <p className="text-sm text-destructive">{error}</p>
          )}
          <div className="space-y-2">
            <Label htmlFor="name">Name</Label>
            <Input
              id="name"
              value={name}
              onChange={(e) => setName(e.target.value)}
              required
            />
          </div>
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
              minLength={8}
            />
          </div>
          <Button type="submit" className="w-full" disabled={registerMutation.isPending}>
            {registerMutation.isPending ? 'Creating account...' : 'Create Account'}
          </Button>
          <p className="text-center text-sm text-muted-foreground">
            Already have an account?{' '}
            <Link to="/login" className="text-primary underline">
              Sign in
            </Link>
          </p>
        </form>
      </CardContent>
    </Card>
  );
}
