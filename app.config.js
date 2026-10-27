export default ({ config }) => ({
  ...config,
  name: 'Zyro Wallet',
  slug: 'zyro-wallet',
  version: '1.0.0',
  orientation: 'portrait',
  userInterfaceStyle: 'light',
  newArchEnabled: true,
  android: { ...config.android, package: 'com.zyro.wallet', versionCode: 1, permissions: ['INTERNET'] },
  extra: { ...config.extra, apiBaseUrl: process.env.EXPO_PUBLIC_API_BASE_URL || '' }
});
