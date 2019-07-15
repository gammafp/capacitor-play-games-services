declare module "@capacitor/core" {
  interface PluginRegistry {
    PlayGames: PlayGamesPlugin;
  }
}

export interface PlayGamesPlugin {
  echo(options: { value: string }): Promise<{value: string}>;
}
