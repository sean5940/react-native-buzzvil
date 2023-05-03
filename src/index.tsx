import {
  requireNativeComponent,
  UIManager,
  Platform,
  ViewStyle,
} from 'react-native';

const LINKING_ERROR =
  `The package 'react-native-buzzvil' doesn't seem to be linked. Make sure: \n\n` +
  Platform.select({ ios: "- You have run 'pod install'\n", default: '' }) +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo Go\n';

type BuzzvilProps = {
  color: string;
  style: ViewStyle;
};

const ComponentName = 'BuzzvilView';

export const BuzzvilView =
  UIManager.getViewManagerConfig(ComponentName) != null
    ? requireNativeComponent<BuzzvilProps>(ComponentName)
    : () => {
        throw new Error(LINKING_ERROR);
      };
