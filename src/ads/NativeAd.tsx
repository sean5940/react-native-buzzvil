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
}> = ({ unitId, width = 0, height = 0, bgColor = '#F7F5F5' }) => {
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
}

const NativeViewManager = requireNativeComponent<NativeViewManagerProps>(
  'BuzzVilNativeViewManager'
);
