import { NativeModules } from 'react-native';

const { BuzzvilFeedAdModule } = NativeModules;

interface BuzzvilFeedAdModuleInterface {
  show(): Promise<void>;
}

export default BuzzvilFeedAdModule as BuzzvilFeedAdModuleInterface;
