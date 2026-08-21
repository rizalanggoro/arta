import { Link, useLocation, useNavigate } from '@tanstack/react-router';
import { useQuery } from '@tanstack/react-query';
import { listWallets } from '@/client/sdk.gen';
import { authStore } from '@/stores/auth';
import { Button } from '@/components/ui/button';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select';
import { Separator } from '@/components/ui/separator';
import { Badge } from '@/components/ui/badge';
import {
  LayoutDashboard,
  ArrowLeftRight,
  Coins,
  Tag,
  Settings,
  LogOut,
  Wallet,
} from 'lucide-react';

const NAV_ITEMS = [
  { to: '/', label: 'Dashboard', icon: LayoutDashboard },
  { to: '/transactions', label: 'Transactions', icon: ArrowLeftRight },
  { to: '/golds', label: 'Gold', icon: Coins },
  { to: '/categories', label: 'Categories', icon: Tag },
  { to: '/wallets', label: 'Wallets', icon: Wallet },
  { to: '/settings', label: 'Settings', icon: Settings },
] as const;

export function Sidebar() {
  const location = useLocation();
  const navigate = useNavigate();
  const { token, wallets, selectedWalletId, user } = authStore.state;

  const selectedWallet = wallets.find(w => w.id === selectedWalletId);
  const isCash = selectedWallet?.type === 'cash_savings';

  const handleLogout = () => {
    authStore.state.logout();
    navigate({ to: '/login' });
  };

  const handleWalletChange = (value: string) => {
    const id = parseInt(value);
    authStore.state.selectWallet(id);
  };

  // Filter nav items based on wallet type
  const visibleNav = NAV_ITEMS.filter(item => {
    if (item.to === '/transactions' && !isCash) return false;
    if (item.to === '/golds' && isCash) return false;
    return true;
  });

  return (
    <aside className="flex h-screen w-64 flex-col border-r bg-sidebar text-sidebar-foreground">
      <div className="p-4">
        <h1 className="text-xl font-bold tracking-tight">ARTA</h1>
        <p className="text-xs text-muted-foreground mt-0.5">Personal Finance</p>
      </div>

      {wallets.length > 1 && (
        <div className="px-4 pb-2">
          <Select value={String(selectedWalletId)} onValueChange={handleWalletChange}>
            <SelectTrigger className="h-9 text-xs">
              <SelectValue placeholder="Select wallet" />
            </SelectTrigger>
            <SelectContent>
              {wallets.map(w => (
                <SelectItem key={w.id} value={String(w.id)}>
                  <div className="flex items-center gap-2">
                    <Badge variant={w.type === 'cash_savings' ? 'default' : 'secondary'} className="text-[10px] px-1.5 py-0">
                      {w.type === 'cash_savings' ? 'CASH' : 'GOLD'}
                    </Badge>
                    {w.name}
                  </div>
                </SelectItem>
              ))}
            </SelectContent>
          </Select>
        </div>
      )}

      <nav className="flex-1 space-y-1 px-3 py-2">
        {visibleNav.map(item => {
          const isActive = item.to === '/'
            ? location.pathname === '/'
            : location.pathname.startsWith(item.to);
          return (
            <Link
              key={item.to}
              to={item.to}
              className={`flex items-center gap-3 rounded-md px-3 py-2 text-sm transition-colors ${
                isActive
                  ? 'bg-sidebar-accent text-sidebar-accent-foreground font-medium'
                  : 'text-muted-foreground hover:bg-sidebar-accent/50'
              }`}
            >
              <item.icon className="h-4 w-4" />
              {item.label}
            </Link>
          );
        })}
      </nav>

      <div className="border-t p-4">
        <div className="mb-3">
          <p className="text-sm font-medium truncate">{user?.name}</p>
          <p className="text-xs text-muted-foreground truncate">{user?.email}</p>
        </div>
        <Button variant="ghost" size="sm" className="w-full justify-start gap-2" onClick={handleLogout}>
          <LogOut className="h-4 w-4" />
          Logout
        </Button>
      </div>
    </aside>
  );
}
