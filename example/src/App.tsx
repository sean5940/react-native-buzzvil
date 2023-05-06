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
      birthYear: 2022,
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
      <View style={{ flex: 1 }}>
        {isShowFeed ? (
          <FeedAds />
        ) : (
          <View style={{ flex: 1, backgroundColor: 'blue' }} />
        )}
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
  },
});
