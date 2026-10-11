import React, { useState, useEffect } from 'react';
import { TopHeader } from './TopHeader';
import { BottomNav } from './BottomNav';
import { Wifi, BatteryMedium, Signal } from 'lucide-react';

interface MobileShellProps {
  children: React.ReactNode;
}

export const MobileShell: React.FC<MobileShellProps> = ({ children }) => {
  const [time, setTime] = useState<string>('');

  useEffect(() => {
    const updateTime = () => {
      const now = new Date();
      setTime(
        now.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit', hour12: false })
      );
    };
    updateTime();
    const interval = setInterval(updateTime, 30000);
    return () => clearInterval(interval);
  }, []);

  return (
    <div className="min-h-screen bg-[#050806] flex items-center justify-center p-0 sm:p-4 md:p-6 text-slate-100 antialiased">
      {/* Background ambient lighting */}
      <div className="fixed inset-0 pointer-events-none overflow-hidden">
        <div className="absolute top-1/4 left-1/2 -translate-x-1/2 -translate-y-1/2 w-[500px] h-[500px] bg-emerald-900/15 rounded-full blur-[120px]" />
        <div className="absolute bottom-1/4 right-1/4 w-[400px] h-[400px] bg-teal-950/20 rounded-full blur-[100px]" />
      </div>

      {/* Mobile Device Canvas */}
      <div className="w-full max-w-md min-h-screen sm:min-h-[850px] sm:max-h-[920px] bg-[#090e0b] sm:rounded-[36px] shadow-[0_0_50px_rgba(0,0,0,0.8),0_0_20px_rgba(16,185,129,0.1)] border-0 sm:border sm:border-emerald-500/20 flex flex-col relative overflow-hidden">
        {/* Android Status Bar */}
        <div className="h-7 w-full px-5 pt-1.5 flex items-center justify-between text-[11px] text-slate-400 bg-black/40 select-none z-40 shrink-0">
          <span className="font-semibold text-slate-300 font-mono tracking-tight">{time || '19:35'}</span>

          {/* Camera punch hole / speaker notch */}
          <div className="w-3.5 h-3.5 rounded-full bg-black border border-white/10 mx-auto shadow-inner" />

          <div className="flex items-center gap-1.5 text-slate-400">
            <Signal className="w-3 h-3" />
            <Wifi className="w-3.5 h-3.5" />
            <div className="flex items-center gap-0.5">
              <span className="text-[10px] font-mono">87%</span>
              <BatteryMedium className="w-3.5 h-3.5 text-emerald-400" />
            </div>
          </div>
        </div>

        {/* Persistent Top Header (Individual / Grupal switch + group selector) */}
        <TopHeader />

        {/* Screen Content Scroll Area */}
        <main className="flex-1 overflow-y-auto pb-24 px-4 pt-3 relative">
          {children}
        </main>

        {/* Classic Android Bottom Navigation Bar */}
        <BottomNav />
      </div>
    </div>
  );
};
