import React from 'react';
import { useApp } from '../../context/AppContext';
import { PantallaActiva } from '../../types/database';
import {
  Wallet,
  ClipboardList,
  BadgePercent,
  ReceiptText,
  CircleUserRound,
} from 'lucide-react';

interface NavItem {
  id: PantallaActiva;
  label: string;
  icon: React.ComponentType<{ className?: string }>;
}

const NAV_ITEMS: NavItem[] = [
  { id: 'resumen', label: 'Resumen', icon: Wallet },
  { id: 'listas', label: 'Listas', icon: ClipboardList },
  { id: 'ofertas', label: 'Ofertas', icon: BadgePercent },
  { id: 'historial', label: 'Movimientos', icon: ReceiptText },
  { id: 'perfil', label: 'Perfil', icon: CircleUserRound },
];

export const BottomNav: React.FC = () => {
  const { pantallaActiva, setPantallaActiva } = useApp();

  return (
    <nav className="fixed bottom-0 left-0 right-0 z-40 max-w-md mx-auto glass-nav border-t border-white/[0.08] shadow-2xl">
      <div className="grid grid-cols-5 items-center h-16 px-1.5">
        {NAV_ITEMS.map((item) => {
          const Icon = item.icon;
          const isActive = pantallaActiva === item.id;

          return (
            <button
              key={item.id}
              onClick={() => setPantallaActiva(item.id)}
              className={`flex flex-col items-center justify-center py-1.5 px-0.5 relative transition-all group ${
                isActive ? 'text-emerald-300' : 'text-slate-400 hover:text-slate-200'
              }`}
            >
              {/* Refined active top indicator */}
              {isActive && (
                <span className="absolute -top-[1px] w-5 h-0.5 rounded-full bg-emerald-400 shadow-[0_0_6px_rgba(52,211,153,0.5)]" />
              )}

              <div
                className={`p-1.5 rounded-xl transition-all ${
                  isActive
                    ? 'bg-emerald-500/10 text-emerald-300 border border-emerald-500/20'
                    : 'text-slate-400 group-hover:bg-white/[0.04]'
                }`}
              >
                <Icon className="w-4 h-4 sm:w-5 sm:h-5 stroke-[2.1]" />
              </div>

              <span
                className={`text-[10px] font-medium tracking-tight mt-0.5 transition-colors truncate max-w-[64px] ${
                  isActive ? 'text-emerald-300 font-bold' : 'text-slate-400'
                }`}
              >
                {item.label}
              </span>
            </button>
          );
        })}
      </div>

      {/* Android system navigation gesture pill */}
      <div className="w-full flex justify-center pb-2 pt-0.5 bg-black/40">
        <div className="w-28 h-1 bg-slate-700/50 rounded-full" />
      </div>
    </nav>
  );
};

