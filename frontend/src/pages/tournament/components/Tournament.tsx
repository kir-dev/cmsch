import {TournamentDetailsView} from "../../../util/views/tournament.view.ts";


interface TournamentProps {
  tournament: TournamentDetailsView
}

const Tournament = ({tournament}: TournamentProps) => {
  return (
    <div>
      <h1>{tournament.tournament.title}</h1>
      <p>{tournament.tournament.description}</p>
      <p>{tournament.tournament.location}</p>
      
    </div>
  )
}

export default Tournament
