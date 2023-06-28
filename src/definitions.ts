import type { PluginListenerHandle } from '@capacitor/core';

export interface IGoogleSignIn {
    id?: string;
    display_name?: string;
    icon?: string;
    title?: string;
    message?: string;
    isLogin: boolean;
}

export interface PlayGamesPlugin {
    // echo(): Promise<{ value: string }>;
    login(): Promise<IGoogleSignIn>;
    status(): Promise<{ isLogin: boolean }>;
    showLeaderboard(leaderboard: { id: string; }): void;
    showAllLeaderboard(): void;
    submitScore(leaderboard: { id: string; score: number; }): void;
  
    unLockAchievement(achievement: { id: string; }): void;
    showAchievements(): void;

    addListener(
        eventName: 'onSignInStatus',
        listenerFunc: (data: IGoogleSignIn) => void,
    ): PluginListenerHandle;

    removeListener(
        eventName: 'onSignInStatus',
        listenerFunc: (data: IGoogleSignIn) => void,
    ): void;

}