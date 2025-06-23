import {useTournamentQuery} from "../../api/hooks/tournament/queries/useTournamentQuery.ts";
import {useParams} from "react-router-dom";
import {toInteger} from "lodash";
import {PageStatus} from "../../common-components/PageStatus.tsx";
import Tournament from "./components/Tournament.tsx";


const TournamentPage = () => {
  const { id } = useParams()
  const { data, isLoading, error, refetch } = useTournamentQuery(toInteger(id) || 0)

  if (error || isLoading || !data) return <PageStatus isLoading={isLoading} isError={!!error} title="Verseny" />

  if (!data.tournament) return <PageStatus isError={true} title="Verseny nem elérhető" isLoading={false} />
  return <Tournament tournament={data.tournament} refetch={refetch} />
}

export default TournamentPage
