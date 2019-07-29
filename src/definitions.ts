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
  login: string;
}

export interface PlayGamesPlugin {
  signInSilently(): Promise<IGoogleSignIn>;
  signOut(): Promise<{login: string}>;
}
