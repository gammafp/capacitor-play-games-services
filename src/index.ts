import { registerPlugin } from '@capacitor/core';

import type { PlayGamesPlugin } from './definitions';

const PlayGames = registerPlugin<PlayGamesPlugin>('PlayGames');

export * from './definitions';
export { PlayGames };
