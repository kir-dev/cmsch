# Tournament life cycle
1. Previously: we have booked the fields for football, volleyball (and streetball?)
2. Tournament creation
   1. Football, volleyball, streetball; chess, beer pong, video games
   2. Name, max participants, type of tournament (ie. group stage?, knockout phase?) etc.
3. Applications come in for every event
4. Application ends, we need to generate the brackets/groups
   1. Set the seedings for the events
   2. Football, Volleyball, Streetball: group phases for all, others: knockout brackets
      - group sizes should be dependent on the count of the playing fields (how many matches can we play overall and concurrently)
   - we might want to avoid duplicate pairings over different sports to maintain diversity
5. set the times and places for matches
   - we should avoid teams being represented in different sports at the same time (at least in football, volleyball and streetball)
6. group stages for football, volleyball and streetball go, finish after a couple of days
7. get advancing teams, generate knockout brackets for them
   - once again, avoid teams being represented at the same time, also try to make the finals be at a different time

- register game results (possibly even have live scores updated)

## Views/Pages

### Registration
- That should be a form (don't know if it can be used as easily)

### Tournaments
- Lists the different tournaments
  - Outside sports might be on the same page, the online tourneys as well

### Tournament (group) view page
- Tab type 1: Table/bracket for a tournament
  - different tournament, different tab for bracket/group standings
- Tab type 2: Game schedules
  - ? every tournament's schedule in a group on the same page ?
  - Should we even group tournaments? If so, how?


## Schema

### Tournament Group (?)
- Id
- Name
- url

### Tournament
- Id
- Name
- groupId (optional) (?)
- url

### TournamentStage
- Id
- tournamentId

### GroupStage: TournamentStage
- teamsToAdvance

### TournamentRegistration
- Id
- groupId
- tournamentId
- seed
####
//- opponents: Opponents

