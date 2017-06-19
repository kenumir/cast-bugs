# Chromecast Stop/Play Bugs
## Scenario 1:
- connect to cast device
- press play
- when sound starts press stop

After this playing is not possible. Screen display only black background.

## Scenario 2:
- connect to cast device
- press `Start Play` multiple (7-8) times (~1s delay)

On logcat show `onFailure` message `statusCode=unknown status code: 2103`

Only reconnect to cast can help. It looks like receiver hangs on something.

### Tested on:
- Chromecast 1
- Chrome Audio Cast
- Shield TV

All devices has `1.24.88047` software version