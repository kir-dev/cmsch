import {useTournamentQuery} from "../../api/hooks/tournament/useTournamentQuery.ts";
import {useParams} from "react-router-dom";
import {toInteger} from "lodash";
import {PageStatus} from "../../common-components/PageStatus.tsx";
import Tournament from "./components/Tournament.tsx";


const TournamentPage = () => {
  const { id } = useParams()
  const { data, isLoading, error, refetch } = useTournamentQuery(toInteger(id) || 0)

  if (error || isLoading || !data) return <PageStatus isLoading={isLoading} isError={!!error} title="Verseny" />

  return <Tournament tournament={data} refetch={refetch} />
}

export default TournamentPage
