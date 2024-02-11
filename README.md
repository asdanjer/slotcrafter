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
- **Permission Message:** "You do not have permission to use this command."
- **Tab-Completion:** Yes

### `/yeetme`

- **Description:** Toggles the automatic kick feature for the user if the server's Mean Tick Time (MSPT) gets too high.
- **Permission:** `slotcrafter.yeetme`

### `/yeetthem`

- **Description:** Kicks all players who have enabled the automatic kick feature.
- **Permission:** `slotcrafter.yeetthem`

## Configuration (`config.yml`)

The `config.yml` file contains settings that control the behavior of the SlotCrafter plugin. Below are the available configuration options along with their descriptions:

- `minSlots`: The minimum number of slots the plugin will set. The plugin will not decrease the server slots below this number. (Default: `10`)
- `maxSlots`: The maximum number of slots the plugin will set. The plugin will not increase the server slots above this number. (Default: `100`)
- `lowerMSPTThreshold`: The Mean Server Tick Time (MSPT) threshold below which the plugin will increase the server slots, aiming to optimize performance without overloading the server. (Default: `50.0`)
- `upperMSPTThreshold`: The MSPT threshold above which the plugin will decrease the server slots to prevent server overload and maintain performance. (Default: `60.0`)
- `updateInterval`: The time interval in seconds between each automatic update/check by the plugin. (Default: `60`)
- `averageMSPTInterval`: The time frame in seconds over which the rolling average MSPT is calculated. Set to `0` to use Spark's 1-minute average only. Note that the number of measurements may vary as the plugin updates based on command issuance, player joinin gor leaveing and at the configured update interval. (Default: `600`)
- `autoMode`: Determines whether the plugin starts in automatic mode, adjusting slots based on performance metrics. (Default: `true`)
- `kickmspt`: The MSPT value at which players who have opted in will be kicked. Set to `0` or a negative number to disable this feature. (Default: `70.0`)
