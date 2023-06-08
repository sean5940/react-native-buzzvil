import React, { useEffect, useRef } from 'react';
import {
  PixelRatio,
  UIManager,
  findNodeHandle,
  requireNativeComponent,
} from 'react-native';

const createNativeAd = (viewId: number | null) => {
  console.log('[BuzzVilNativeViewManager] call createNativeAd:', viewId);
  UIManager.dispatchViewManagerCommand(viewId, 'create', [viewId]);
};

export const NativeAd: React.FC<{
  unitId: string;
  width?: number;
  height?: number;
  bgColor?: string;
  onClicked?: () => void;
  onImpressed?: () => void;
  onParticipated?: () => void;
  onRewardRequested?: () => void;
  onRewarded?: () => void;
  onRequested?: () => void;
  onNext?: () => void;
  onComplete?: () => void;
  onError?: (errorType: string) => void;
}> = ({
  unitId,
  width = 0,
  height = 0,
  bgColor = '#F7F5F5',
  onClicked,
  onImpressed,
  onParticipated,
  onRewardRequested,
  onRewarded,
  onRequested,
  onNext,
  onComplete,
  onError,
}) => {
  const ref = useRef(null);

  useEffect(() => {
    const viewId = findNodeHandle(ref.current);
    console.log('[BuzzVilNativeViewManager] viewId:', viewId);
    createNativeAd(viewId);
  }, []);

  return (
    <NativeViewManager
      unitId={unitId}
      background={bgColor}
      style={{
        width: PixelRatio.getPixelSizeForLayoutSize(width),
        height: PixelRatio.getPixelSizeForLayoutSize(height),
      }}
      onClicked={onClicked}
      onImpressed={onImpressed}
      onParticipated={onParticipated}
      onRewardRequested={onRewardRequested}
      onRewarded={onRewarded}
      onRequested={onRequested}
      onNext={onNext}
      onComplete={onComplete}
      onError={(event) => {
        if (onError) {
          onError(`${event.nativeEvent.error}`);
        }
      }}
      ref={ref}
    />
  );
};

interface NativeViewManagerProps {
  unitId: string;
  background: string;
  style: {
    width: number;
    height: number;
  };
  onClicked?: () => void;
  onImpressed?: () => void;
  onParticipated?: () => void;
  onRewardRequested?: () => void;
  onRewarded?: () => void;
  onRequested?: () => void;
  onNext?: () => void;
  onComplete?: () => void;
  onError?: (event: any) => void;
}

const NativeViewManager = requireNativeComponent<NativeViewManagerProps>(
  'BuzzVilNativeViewManager'
);
