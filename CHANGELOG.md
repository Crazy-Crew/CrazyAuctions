### Additions:
- Added the shield and the mace to the weapons category.
- Added {page} placeholder to inventory titles that have the ability to have multiple pages
  - Previously, this was hard coded and could not be removed.

### Changes:
- Re-coded how all inventories are handled internally which fixed a large chunk of known issues.
- Improved performance with inventory handling by reducing a lot of unnecessary bloat.
  - We no longer check the display name, or inventory titles for information we need.
- Clear all caches on player quit.
- Register permissions on startup.
- Updated vital api, which uses a more performant version of the file manager.

### Fixed:
- Fixed an issue where you couldn't use hex colors with buttons.
- Fixed an issue where pagination let you keep creating new pages.
- Fixed a spelling mistake in the config.yml, Refesh -> Refresh
  - Old configurations will work, but please update in your `config.yml`
- Fixed an issue with blacklist materials.

### What's in the pipeline?
- Configuration overhauls.
- MiniMessage Support.
- Ability to auction custom items.
- Ability to use items as currency.
- Anything on GitHub that is a feature request