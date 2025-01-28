import { useTournamentsQuery } from '../../api/hooks/tournament/useTournamentsQuery.ts'
import { useConfigContext } from '../../api/contexts/config/ConfigContext.tsx'
import { Box, Heading, useBreakpoint, useBreakpointValue, useDisclosure, VStack } from '@chakra-ui/react'
import { createRef, useState } from 'react'
import { TournamentView } from '../../util/views/tournament.view.ts'
import { ComponentUnavailable } from '../../common-components/ComponentUnavailable.tsx'
import { PageStatus } from '../../common-components/PageStatus.tsx'
import { CmschPage } from '../../common-components/layout/CmschPage.tsx'
import { Helmet } from 'react-helmet-async'


const TournamentListPage = () => {
  const { isLoading, isError, data } = useTournamentsQuery()
  const component = useConfigContext()?.components?.tournament
  const { isOpen, onToggle } = useDisclosure()
  const tabsSize = useBreakpointValue({ base: 'sm', md: 'md' })
  const breakpoint = useBreakpoint()
  const inputRef = createRef<HTMLInputElement>()

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
          data.map((tournament: TournamentView) => (
          <Box key={tournament.id}>
            <Heading as="h2" size="lg">
              {tournament.title}
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
