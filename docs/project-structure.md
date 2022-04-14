Project Structure
===

## Components

- app: base application, UI config
- UI components:
  - `new`
  - `event`
  - `riddle`
  - `achievement/task`
  - `token`
  - `profile`: aggregates `riddle`, `achievements`, `tokens`, profile data, `debts`, `location`
  - `extraPage`
  - `impressum`
- system components:
  - `login`: JWT and authsch
  - `menu`: required for `app` component
  - `leaderBoard`: aggregates `riddle`, `achievement`, `token`, TODO:`correctionScore`
- admin components:
  - `debts`
  - `location`
