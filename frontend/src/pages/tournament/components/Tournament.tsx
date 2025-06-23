import {
  TournamentDetailsView,
  TournamentResponseMessages,
  TournamentResponses
} from '../../../util/views/tournament.view.ts'
import { Flex, Heading, HStack, Tab, TabList, TabPanel, TabPanels, Tabs, Text, useToast } from '@chakra-ui/react'
import KnockoutStage from "./KnockoutStage.tsx";
import {useState} from "react";
import { useConfigContext } from '../../../api/contexts/config/ConfigContext.tsx'
import { ComponentUnavailable } from '../../../common-components/ComponentUnavailable.tsx'
import { Helmet } from 'react-helmet-async'
import { CmschPage } from '../../../common-components/layout/CmschPage.tsx'


interface TournamentProps {
  tournament: TournamentDetailsView,
  refetch?: () => void
}

const Tournament = ({tournament, refetch = () => {}}: TournamentProps) => {
  const toast = useToast()
  const { components } = useConfigContext()
  const userRole = useConfigContext().role
  const tournamentComponent = components.tournament

  if (!tournamentComponent) return <ComponentUnavailable />

  const actionResponseCallback = (response: TournamentResponses) => {
    if (response == TournamentResponses.OK) {
      toast({ status: 'success', title: TournamentResponseMessages[response] })
      refetch()
    } else {
      toast({ status: 'error', title: TournamentResponseMessages[response] })
    }
  }

  const [tabIndex, setTabIndex] = useState(0)

  const onTabSelected = (i: number) => {
    setTabIndex(i)
  }

  return (
    <CmschPage>
      <Helmet title={tournament.tournament.title} />
      <Flex>
        <Heading>{tournament.tournament.title}</Heading>
        <Text>{tournament.tournament.description}</Text>
        <Text>{tournament.tournament.location}</Text>
        <Flex>

        </Flex>
        <Tabs isLazy isFitted colorScheme="brand" variant="enclosed" index={tabIndex} onChange={onTabSelected}>
          <TabList>
            <Tab>Résztvevők</Tab>
            {
              tournament.stages.map((stage) => (
                <Tab key={stage.id}>{stage.name}</Tab>
              ))
            }
          </TabList>
          <TabPanels>
            <TabPanel px={100}>
              {
                tournament.tournament.participants.map((participant) => (
                  <HStack spacing={3} key={participant.teamId}>
                    <Heading as="h2" maxWidth="100%">{participant.teamName}</Heading>
                  </HStack>
                ))
              }
            </TabPanel>
            {
              tournament.stages.map((stage) => (
                <TabPanel px={0} overflowX="auto" scrollBehavior="smooth">
                  <KnockoutStage key={stage.id} stage={stage}/>
                </TabPanel>
              ))
            }
          </TabPanels>
        </Tabs>
      </Flex>
    </CmschPage>
  )
}

export default Tournament
