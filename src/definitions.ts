declare global {
  interface PluginRegistry {
    PlayGames: PlayGamesPlugin;
  }
}

export interface IGoogleSignIn {
  id: string;
  display_name: string;
}

export interface PlayGamesPlugin {
  signInSilently(): Promise<IGoogleSignIn>;
  signOut(): Promise<{status: string}>;
}
