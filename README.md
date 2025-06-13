# BluePlaylists

Part of BlueBeats.
It contains all the logic to use Dynamic Playlists.

## Usage

In order to use this library, you need to implement some interfaces and populate the 
implementation in *Slots*.
The following interfaces need to be implemented (all of them also contain their respective slots):

- `apps.chocolatecakecodes.bluebeats.blueplaylists.interfaces.log.Logger`
- `apps.chocolatecakecodes.bluebeats.blueplaylists.interfaces.media.MediaLibrary`
- `apps.chocolatecakecodes.bluebeats.blueplaylists.interfaces.media.TimeSpanItemPlayerController`
- `apps.chocolatecakecodes.bluebeats.blueplaylists.interfaces.storage.RuleStorage`\
  (this needs to be implemented only if rules are going to be copied (e.g. via `applyFrom()`))

Also all abstract classes in `apps.chocolatecakecodes.bluebeats.blueplaylists.interfaces.media` 
need to be implemented.
