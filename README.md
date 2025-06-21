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

## Rules

This is a quick explanation of every rule and other important elements and their parameters.\
The output of each rule is (often) shuffled and truncated to the requested amount.

### Share
A share is used to specify how much items every rule should contribute to their containing RuleGroup.

There are different modes:
- absolute: the rule should contribute exactly *n* items
- relative: the rule should contribute *n * amount_for_parent* items
- even: if the requested amount of items of the parent is not filled by other rules, the missing number is divided between all even rules
- unlimited: the rule should output all items which match it

### iterationSize
This is the amount of items the root-RuleGroup should generate for every request.

### RuleGroup
A rule which contains multiple rules (recursive structures are possible) 
and combines their outputs.
It calculates the amount of items every subrule should generate according to their **share**
(the shares are calculated against the requested amount for this group).
If **combineWithAnd** is true, then only the intersection of all outputs of the subrules are returned,
else the union.\
If subrules are negated, then they are requested to output all files they can match.
The union of all negated outputs is removed from the output of the other rules
(rules will still try to fulfill the requested amount).

### IncludeRule
Used to directly specify files which should occur in the playlist.
It can also take directories, which might be resolved recursively, to output all files in them.\
If multiple files and/or directories are specified in one rule, the union of all resolved files is returned.

### ID3TagsRule
Will return files which have one of the specified values as the ID3 tag field.
Supported tag-fields are currently: title, artist and genre.

### UsertagsRule
Will return files which have at least one or all (depending on **combineWithAnd**) Usertag.

### RegexRule
Will return files where the specified RegularExpression matches the value of the specified attribute.
Supported attributes are currently: filename, filepath, title, artist, Usertags (will match if the regex matches any of the Usertags).

### TimeSpanRule
This rule has one specific file and will return a playlist-item which plays the file,
but only the specified time interval.
It also has a **description** which is displayed in the playlist-overview in the player so that the user can note which
section the timespan is about (e.g. a chapter).
