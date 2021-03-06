# BattleTech
Scripts for extracting information from BattleTech game assets.

## Usage Notes
On MacOS the files can be found in `~/Library/Application Support/Steam/steamapps/common/BATTLETECH/BattleTech.app`

The scripts require the mech chassis files (`chassisdef_NAME_TYPE.json`) and star system files 
(`starsystemdef_NAME.json`) be copied to their own directories, and that the files are "prepared" for use as defined below.

It is recommended that to overwrite any previous copies of the above files as details of mechs and systems may have 
changed between updates.

The location of each directory created above should be updated in each scripts main method

### Mechs
#### 1.9 Update
#### 1.8 Update
* BNC-3S - Banshee
* BJ-1DB - Blackjack
* CPLT-C4 - Catapult
* UM-R60L - UrbanMech
* UM-R90 - SuburbanMech
* VND1AA - Vindicator
#### 1.6 Update
* BJ-1DB - Blackjack
* UM-R60L - UrbanMech
* UM-R90 - SuburbanMech
* VND1AA - Vindicator

The above mechs are variants of existing mechs and need the following preparation: 
+ All need the `"tonnage"` line to be moved above `"variantName"`

### Systems
+ `"DifficultyList"` array needs to be flattened onto one line
+ `"ContractEmployers"` array needs to be flattened onto one line
+ `"Ùr Cruinne's"` json file is invalid, a `,` needs to be added to the end of `DefaultDifficulty`

## Unlockable Assets
It should be noted that the following Mechs are embedded in the runtime and can not be used with these scripts:
### Heavy Metal/1.9
* FLE-4 - Flea
* FLE-15 - Flea
* JVN-10A - Javelin
* RVN-3X - Raven
* ASN-21 - Assassin
* ASN-101 - Assassin
* VL-2T - Vulcan
* VL-5T - Vulcan
* PXH-1 - Phoenix Hawk
* PXH-1b - Phoenix Hawk
* PXH-1K - Phoenix Hawk
* CRB-27b - Crab
* RFL-3C - Rifleman
* RFL-3N - Rifleman
* RFL-4D - Rifleman
* RFL-RIP - Rifleman
* ARC-2R - Archer 
* ARC-2S - Archer 
* ARC-LS - Archer 
* ARC-XO - Archer
* CP-10-HQ - Cyclops 
### Urban Warfare
* RVN-1X - Raven
* JVN-10F - Javelin
* JVN-10N - Javelin
* HCT-3X - Hatchman
### Flashpoint
* HCT-3F - Hatchman
* CRB-27 - Crab
* BSC-27 - Big Steel Claw
* CP-10-Q - Cyclops
* CP-10-Z - Cyclops
