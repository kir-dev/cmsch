import { useTournamentListQuery } from '../../api/hooks/tournament/queries/useTournamentListQuery.ts'
import { useConfigContext } from '../../api/contexts/config/ConfigContext.tsx'
import {Box, Heading, LinkOverlay, VStack} from '@chakra-ui/react'
import { TournamentPreview } from '../../util/views/tournament.view.ts'
import { ComponentUnavailable } from '../../common-components/ComponentUnavailable.tsx'
import { PageStatus } from '../../common-components/PageStatus.tsx'
import { CmschPage } from '../../common-components/layout/CmschPage.tsx'
import { Helmet } from 'react-helmet-async'
import {Link} from "react-router-dom";
import {AbsolutePaths} from "../../util/paths.ts";


const TournamentListPage = () => {
  const { isLoading, isError, data } = useTournamentListQuery()
  const component = useConfigContext()?.components?.tournament

  if (!component) return <ComponentUnavailable />

  if (isError || isLoading || !data) return <PageStatus isLoading={isLoading} isError={isError} title={component.title} />
  return (
    <CmschPage>
      <Helmet title={component.title ?? "Versenyek"} />
      <Box mb={5}>
        <Heading as="h1" variant="main-title" mb={5}>
          {component.title}
        </Heading>
        <Heading as="h2" size="md" mb={5}>
          {data.length} verseny található.
        </Heading>
      </Box>
      <VStack spacing={4} mt={5} align="stretch">
        {(data ?? []).length > 0 ? (
          data.map((tournament: TournamentPreview) => (
          <Box key={tournament.id}>
            <Heading as="h2" size="lg">
              <LinkOverlay as={Link} to={`${AbsolutePaths.TOURNAMENTS}/${tournament.id}`}>
                {tournament.title}
              </LinkOverlay>
            </Heading>
            <Box>
              {tournament.description}
            </Box>
          </Box>
        ))
        ) : (
          <Box>Nincs egyetlen verseny sem.</Box>
        )}
      </VStack>
    </CmschPage>
  )
}

export default TournamentListPage
