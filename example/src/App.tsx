import React, { useEffect, useState } from 'react';

import { Button, StyleSheet, View } from 'react-native';
import BuzzvilAdModule, { FeedAds } from 'react-native-buzzvil';

export default function App() {
  const [isShowFeed, setShowFeed] = useState<boolean>(false);

  useEffect(() => {
    BuzzvilAdModule.initialize({ feedId: '293169052486685' });
    BuzzvilAdModule.setUserInfo({
      userId: '1',
      gender: 'MALE',
      birthYear: 1989,
    });
  }, []);

  return (
    <View style={styles.container}>
      <Button
        title={!isShowFeed ? '피드 보기' : '피드 제거'}
        onPress={() => {
          setShowFeed((pre) => !pre);
        }}
      />
      <View style={styles.container}>
        <FeedAds />
        <View style={[styles.screenBlocker, blockerHeight()]} />
      </View>
    </View>
  );

  function blockerHeight() {
    return { bottom: isShowFeed ? undefined : 0 };
  }
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
  },
  screenBlocker: {
    position: 'absolute',
    backgroundColor: 'pink',
    left: 0,
    right: 0,
    top: 0,
  },
});
