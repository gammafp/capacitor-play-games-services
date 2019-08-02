declare global {
    interface PluginRegistry {
        PlayGames: PlayGamesPlugin;
    }
}

export interface IGoogleSignIn {
    id: string;
    display_name: string;
    icon: string;
    title: string;
    login: boolean;
}

export interface PlayGamesPlugin {
    signInSilently(): Promise<IGoogleSignIn>;
    signOut(): Promise<{ login: boolean }>;
    signStatus(): Promise<{ login: boolean }>;
    showLeaderboard(leaderboard: { id: string; }): Promise<{ login: boolean }>;
    showAllLeaderboard(): void;
    submitScore(leaderboard: { id: string; points: number; }): void;
    showAchievements(): void;
    unlockAchievement(unlockAchievement: { id: string }): void;
    incrementAchievement(incrementAchievement: { id: string, step: number }): void;
    showSavedGamesUI(): void;
}
