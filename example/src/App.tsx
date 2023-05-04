import React, { useEffect } from 'react';

import { StyleSheet, View } from 'react-native';
import BuzzvilAdModule from 'react-native-buzzvil';

export default function App() {
  useEffect(() => {
    BuzzvilAdModule.initialize();
    BuzzvilAdModule.setUserInfo({
      userId: '1',
      gender: 'MALE',
      birthYear: 2022,
    });
  }, []);

  return <View style={styles.container} />;
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
  },
  box: {
    width: 60,
    height: 60,
    marginVertical: 20,
  },
});
