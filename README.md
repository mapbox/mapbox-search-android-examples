# Mapbox Search SDK for Android example app.

Example application showing usage of [Mapbox Search SDK for Android](https://docs.mapbox.com/android/search/guides/).


## Configure credentials

Before installing the Demo app, you will need to gather two pieces of sensitive information from your Mapbox account. If you don't have a Mapbox account: [sign up](https://account.mapbox.com/auth/signup/) and navigate to your [Account page](https://account.mapbox.com/). You'll need:
1. A public access token: From your [account's tokens page](https://account.mapbox.com/access-tokens/), you can either copy your default public token or click the Create a token button to create a new public token.
2. A secret access token with the Downloads:Read scope.

Export your public token as an environment variable `MAPBOX_ACCESS_TOKEN` and your secret access token as an environment variable `SDK_REGISTRY_TOKEN`. Alternatively, you can provide those credentials as project properties.
