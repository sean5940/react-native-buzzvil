import { NativeModules } from 'react-native';
import type { RequestFeedInfo, RequestUserInfo } from './types';

const { BuzzvilAdModule } = NativeModules;

interface BuzzvilAdModuleInterface {
  initialize(requestFeedInfo: RequestFeedInfo): Promise<void>;
  setUserInfo(requestUserInfo?: RequestUserInfo): Promise<void>;
}

export default BuzzvilAdModule as BuzzvilAdModuleInterface;
