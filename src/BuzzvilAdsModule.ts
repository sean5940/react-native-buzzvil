import { NativeModules } from 'react-native';
import type { RequestUserInfo } from './types';

const { BuzzvilAdModule } = NativeModules;

interface BuzzvilAdModuleInterface {
  initialize(): Promise<void>;
  setUserInfo(requestUserInfo?: RequestUserInfo): Promise<void>;
}

export default BuzzvilAdModule as BuzzvilAdModuleInterface;
