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
    showLeaderboard(leaderboard: { leaderboard_id: string; }): Promise<{ login: boolean }>;
    showAllLeaderboard(): void;
    submitScore(leaderboard: { leaderboard_id: string; points: number; }): void;
}
