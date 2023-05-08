# react-native-buzzvil

react native library for buzzvil android sdk

## Installation

```sh
yarn add react-native-buzzvil
```

## Usage
react-native-google-mobile-ads과 같이 app.json에 앱키를 등록해야 함.
```json
{
   "react-native-buzzvil-ads": {
        "android_app_id": "app-pub-{app id}"
    }
}
```

```js
import React, { useEffect, useState } from 'react';

import { Button, StyleSheet, View } from 'react-native';
import BuzzvilAdModule, { FeedAds } from 'react-native-buzzvil';

export default function App() {
  const [isShowFeed, setShowFeed] = useState<boolean>(false);

  useEffect(() => {
    BuzzvilAdModule.initialize({ feedId: '${feed id' });
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

```

## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

## License

MIT

---
