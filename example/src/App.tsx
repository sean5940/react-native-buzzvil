import React, { useEffect, useRef, useState } from 'react';

import {
  Button,
  Dimensions,
  ScrollView,
  StyleSheet,
  Text,
  View,
} from 'react-native';
import BuzzvilAdModule, { FeedAds, NativeAd } from 'react-native-buzzvil';

const windowWidth = Dimensions.get('window').width;
let pageCount = 0;

export default function App() {
  const scrollViewRef = useRef<ScrollView>(null);
  const [showNativeAd, setShowNativeAd] = useState<boolean>(false);

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
        <View style={[styles.defaultContain, { width: windowWidth }]}>
          <View style={{ alignItems: 'center', marginTop: 20 }}>
            <Button
              onPress={() => {
                setShowNativeAd((pre) => !pre);
              }}
              title={showNativeAd ? '네이티브 광고 숨김' : '네이티브 광고 노출'}
            />
          </View>
          <View style={styles.nativeAdContainer}>
            <NativeAd
              unitId="252290577659165"
              width={240}
              height={235}
              bgColor="#F1EDED"
              onParticipated={() => {
                console.log('onParticipated');
              }}
              onRewarded={() => {
                console.log('onRewarded');
              }}
              onError={(errorType) => {
                console.log('onError:', errorType);
              }}
            />
          </View>
          {!showNativeAd && (
            <View
              style={{
                left: 75,
                right: 75,
                top: 240,
                bottom: 320,
                position: 'absolute',
                backgroundColor: 'pink',
              }}
            />
          )}
        </View>

        <View style={[styles.container, { width: windowWidth }]}>
          <FeedAds unitId="293169052486685" rewardText="적립 가능 캐시 ⓒ " />
        </View>

        <View style={[styles.container, { width: windowWidth }]}>
          <Text>page2</Text>
        </View>
      </ScrollView>
    </View>
  );
}

const styles = StyleSheet.create({
  defaultContain: {
    flex: 1,
  },
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
  },
  nativeAdContainer: {
    left: 88,
    right: 88,
    top: 250,
    bottom: 330,
    position: 'absolute',
  },
  screenBlocker: {
    position: 'absolute',
    backgroundColor: 'pink',
    left: 0,
    right: 0,
    top: 0,
  },
});
