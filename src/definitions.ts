declare global {
  interface PluginRegistry {
    PlayGames: PlayGamesPlugin;
  }
}

export interface IGoogleSignIn {
  id: string;
  display_name: string;
  family_name: string;
  given_name: string;
  email: string;
  id_token: string;
  photo_url: string;
  server_auth_code: string;
}

export interface PlayGamesPlugin {
  signInSilently(): Promise<IGoogleSignIn>;
}
