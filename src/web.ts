import { WebPlugin } from '@capacitor/core';
import { PlayGamesPlugin } from './definitions';

export class PlayGamesWeb extends WebPlugin implements PlayGamesPlugin {
  constructor() {
    super({
      name: 'PlayGames',
      platforms: ['web']
    });
  }

  async echo(options: { value: string }): Promise<{value: string}> {
    console.log('ECHO', options);
    return options;
  }
}

const PlayGames = new PlayGamesWeb();

export { PlayGames };

import { registerWebPlugin } from '@capacitor/core';
registerWebPlugin(PlayGames);
