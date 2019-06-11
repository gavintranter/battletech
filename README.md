# BattleTech
Scripts for extracting information from BattleTech game assets.

## Usage Notes
On MacOS the files can be found in `Library/Application Support/Steam/steamapps/common/BATTLETECH/BattleTech.app`

The scripts require the mech chassis files (`chassisdef_NAME_TYPE.json`) and star system files 
(`starsystemdef_NAME.json`) be moved to their own directories, and that the files are "prepared" for use as defined below.

It is recommended that to overwrite any previous copies of the above files as details of mechs and systems may have 
changed between updates.

The location of each directory created above should be updated in each scripts main method

### Mechs
#### 1.6 Update
* UM-R60L - UrbanMech
* UM-R90 - SuburbanMech
* BJ-1DB - Blackjack
* VND1AA - Vindicator

The above mechs are variants of existing mechs and need the following preparation: 
+ All need the `"tonnage"` line to be moved above `"variantName"`

### Systems
+ `"DifficultyList"` array needs to be flattened onto one line
+ `"ContractEmployers"` array needs to be flattened onto one line
+ `"Ùr Cruinne's"` json file is invalid, a `,` needs to be added to the end of `DefaultDifficulty`

## Unlockable Assets
It should be noted that the following Mechs are embedded in the runtime and can not be used with these scripts:
### Urban Warfare
* RVN-1X = Raven
* Javelin
### Flashpoint
* HCT-3F - Hatchman
* HCT-3X - Hatchman
* CRB-27 - Crab
* BSC-27 - Big Steel Claw
* CP-10-Q - Cyclops
* CP-10-Z - Cyclops
