import {useTournamentQuery} from "../../api/hooks/tournament/useTournamentQuery.ts";
import {useParams} from "react-router-dom";
import {toInteger} from "lodash";
import {PageStatus} from "../../common-components/PageStatus.tsx";
import {CmschPage} from "../../common-components/layout/CmschPage.tsx";
import Tournament from "./components/Tournament.tsx";
import {Helmet} from "react-helmet-async";


const TournamentPage = () => {
  const { id } = useParams()
  const { isLoading, isError, data } = useTournamentQuery(toInteger(id) || 0)

  if (isError || isLoading || !data) return <PageStatus isLoading={isLoading} isError={isError} title="Verseny" />

  return (
    <CmschPage>
      <Helmet title={data.tournament.title} />
      <Tournament tournament={data} />
    </CmschPage>
  )
}

export default TournamentPage
