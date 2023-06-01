import React, { useEffect, useRef } from 'react';
import {
  PixelRatio,
  UIManager,
  findNodeHandle,
  requireNativeComponent,
} from 'react-native';

const createFragment = (viewId: number | null) => {
  console.log('[FeedViewManager] call createFragment:', viewId);
  UIManager.dispatchViewManagerCommand(viewId, 'create', [viewId]);
};

export const FeedAds: React.FC<{
  unitId: string;
  title?: string;
  width?: number;
  height?: number;
}> = ({ unitId, width = 0, height = 0, title = '적립 찬스!' }) => {
  const ref = useRef(null);

  useEffect(() => {
    const viewId = findNodeHandle(ref.current);
    console.log('[FeedViewManager] viewId:', viewId);
    createFragment(viewId);
  }, []);

  return (
    <FeedViewManager
      title={title}
      unitId={unitId}
      style={{
        width: PixelRatio.getPixelSizeForLayoutSize(width),
        height: PixelRatio.getPixelSizeForLayoutSize(height),
      }}
      ref={ref}
    />
  );
};

interface FeedViewManagerProps {
  title: string;
  unitId: string;
  style: {
    width: number;
    height: number;
  };
}

const FeedViewManager = requireNativeComponent<FeedViewManagerProps>(
  'BuzzvilFeedViewManager'
);
