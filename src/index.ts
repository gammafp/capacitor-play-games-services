import { registerPlugin } from '@capacitor/core';

import type { PlayGamesPlugin } from './definitions';

const PlayGames = registerPlugin<PlayGamesPlugin>('PlayGamesPlugin');

export * from './definitions';
export { PlayGames };
