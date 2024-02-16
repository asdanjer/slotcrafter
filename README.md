# SlotCrafter Plugin Documentation

Welcome to the SlotCrafter plugin documentation! This guide provides detailed information on the commands, permissions, and configuration options available in SlotCrafter.


- **API Version Required:** `1.20`
- **Dependencies:** Requires the `spark` plugin.

## Commands

SlotCrafter introduces several commands to manage slot limits, update configurations, and manage automatic player kicking based on server performance.

### `/setslots <new limit/auto>`

- **Description:** Sets the slot limit for the server. You can specify a number or set it to 'auto' for automatic adjustments.
- **Permission:** `slotcrafter.setslotlimit`
- **Tab-Completion:** Yes

### `/slotcrafter update <config setting> <value>`

- **Description:** Updates a specific SlotCrafter configuration setting to a new value.
- **Permission:** `slotcrafter.admin`
- **Tab-Completion:** Yes

### `/slotcrafter info `

- **Description:** Gives you some info from the Plugin.
- **Permission:** `slotcrafter.admin`
- **Tab-Completion:** Yes

### `/yeetme`

- **Description:** Toggles the automatic kick feature for the user if the server's Mean Tick Time (MSPT) gets too high.
- **Permission:** `slotcrafter.yeetme`

### `/yeetthem`

- **Description:** Kicks all players who have enabled the automatic kick feature.
- **Permission:** `slotcrafter.yeetthem`

## Configuration (`config.yml`)

The `config.yml` file contains settings that control the behavior of the SlotCrafter plugin. Below are the available configuration options along with their descriptions:
MSPT = milliseconds per tick
- `minSlots`: The minimum number of slots the plugin will set. The plugin will not decrease the server slots below this number. (Default: `10`)
- `maxSlots`: The maximum number of slots the plugin will set. The plugin will not increase the server slots above this number. (Default: `100`)
- `lowerMSPTThreshold`: MSPT threshold below which the plugin will increase the server slots up to `maxSlots`. Above this Threshold the Playercap will stay the same till it hits `upperMSPTThreshold` (Default: `50.0`)
- `upperMSPTThreshold`: The MSPT threshold above which the plugin will start to reduce the playercap to the players online preventing joining down to `minSlots` (this does not invole kicking just preventing new joins)(Default: `60.0`)
- `updateInterval`: The time interval in seconds between each automatic update/check by the plugin. (Default: `60`)
- `averageMSPTInterval`: The time frame in seconds over which the rolling average MSPT is calculated. Set to `0` to use Spark's 1-minute average only. Note that the number of measurements may vary. (Default: `600`)
- `autoMode`: Determines whether the plugin starts in automatic mode, adjusting slots based on MSPT. (Default: `true`)
- `kickmspt`: The MSPT value at which players who have opted in will be kicked. Set to `0` or a negative number to disable this feature. (Default: `70`)
- `rejoinDelay`: The time people can still rejoin ignoring Player cap after discoenting/losing conection. (Default: `120`)

### `slotcrafter.ignorecap`

- **Description:** Permission ignores the Slot Cap.
  slotcrafter.ignorecap:
    description: Allows people to ignore the slot cap.
    default: op
