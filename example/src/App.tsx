import React, { useEffect, useRef } from 'react';

import {
  Button,
  Dimensions,
  ScrollView,
  StyleSheet,
  Text,
  View,
} from 'react-native';
import BuzzvilAdModule, { FeedAds } from 'react-native-buzzvil';

const windowWidth = Dimensions.get('window').width;
let pageCount = 0;

export default function App() {
  const scrollViewRef = useRef<ScrollView>(null);

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
        title="페이지 이동"
        onPress={() => {
          pageCount += 1;
          pageCount = pageCount > 2 ? 0 : pageCount;
          scrollViewRef.current?.scrollTo({
            x: pageCount * windowWidth,
            y: 0,
            animated: false,
          });
        }}
      />
      <ScrollView
        horizontal
        ref={scrollViewRef}
        scrollEnabled={false}
        showsHorizontalScrollIndicator={false}
      >
        <View style={[styles.container, { width: windowWidth }]}>
          <Text>page1</Text>
        </View>

        <View style={{ width: windowWidth }}>
          <FeedAds />
        </View>

        <View style={[styles.container, { width: windowWidth }]}>
          <Text>page2</Text>
        </View>
      </ScrollView>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
  },
  screenBlocker: {
    position: 'absolute',
    backgroundColor: 'pink',

    left: 0,
    right: 0,
    top: 0,
  },
});
