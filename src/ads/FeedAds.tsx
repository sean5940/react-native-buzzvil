import React, { useEffect, useRef } from 'react';
import {
  PixelRatio,
  UIManager,
  findNodeHandle,
  requireNativeComponent,
} from 'react-native';

const createFragment = (viewId: number | null) => {
  console.log('[FeedViewManager] call createFragment:', viewId);
  try {
    UIManager.dispatchViewManagerCommand(viewId, 'create', [viewId]);
  } catch (e) {
    console.log('error', e);
  }
};

export const FeedAds: React.FC<{ width?: number; height?: number }> = ({
  width = 0,
  height = 0,
}) => {
  const ref = useRef(null);

  useEffect(() => {
    const viewId = findNodeHandle(ref.current);
    console.log('[FeedViewManager] viewId:', viewId);
    createFragment(viewId);
  }, []);

  return (
    <FeedViewManager
      style={{
        width: PixelRatio.getPixelSizeForLayoutSize(width),
        height: PixelRatio.getPixelSizeForLayoutSize(height),
      }}
      ref={ref}
    />
  );
};

interface FeedViewManagerProps {
  style: {
    width: number;
    height: number;
  };
}

const FeedViewManager = requireNativeComponent<FeedViewManagerProps>(
  'BuzzvilFeedViewManager'
);
