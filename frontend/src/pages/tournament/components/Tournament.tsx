import {
  TournamentDetailsView,
  TournamentResponseMessages,
  TournamentResponses
} from '../../../util/views/tournament.view.ts'
import {
  Box,
  Button,
  Flex,
  Heading,
  Tab,
  TabList,
  TabPanel,
  TabPanels,
  Tabs,
  Text,
  useToast
} from '@chakra-ui/react'
import KnockoutStage from "./KnockoutStage.tsx";
import {useState} from "react";
import { useConfigContext } from '../../../api/contexts/config/ConfigContext.tsx'
import { ComponentUnavailable } from '../../../common-components/ComponentUnavailable.tsx'
import { Helmet } from 'react-helmet-async'
import { CmschPage } from '../../../common-components/layout/CmschPage.tsx'
import { useTournamentJoinMutation } from '../../../api/hooks/tournament/actions/useTournamentJoinMutation.ts'
import { FaSignInAlt } from 'react-icons/fa'


interface TournamentProps {
  tournament: TournamentDetailsView,
  refetch?: () => void
}

const Tournament = ({tournament, refetch = () => {}}: TournamentProps) => {
  const toast = useToast()
  const { components } = useConfigContext()
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

  const joinMutation = useTournamentJoinMutation()

  const joinTournament = () => {
    if (tournament.tournament.joinEnabled){
      joinMutation.mutate(tournament.tournament.id, {
        onSuccess: (response: TournamentResponses) => {
          actionResponseCallback(response)
          if (response === TournamentResponses.OK) {
            refetch()
          }
        },
        onError: () => {
          toast({ status: 'error', title: 'Hiba történt a versenyre való jelentkezés során.' })
        }
      })
    }
  }

  const [tabIndex, setTabIndex] = useState(0)

  const onTabSelected = (i: number) => {
    setTabIndex(i)
  }

  return (
    <CmschPage>
      <Helmet title={tournament.tournament.title} />
      <Flex direction="column" gap={4} p={4} maxWidth="100%" mx="auto">
        <Heading>{tournament.tournament.title}</Heading>
        <Text>{tournament.tournament.description}</Text>
        <Text>{tournament.tournament.location}</Text>
        <Flex>
          {tournament.tournament.joinEnabled && (
            <Button
              leftIcon={<FaSignInAlt />}
              colorScheme="brand"
              onClick={joinTournament}
              isDisabled={tournament.tournament.isJoined}
            >
              Jelentkezés a versenyre
            </Button>
          )}
          {tournament.tournament.isJoined && (
            <Button
              colorScheme="brand"
              isDisabled={true}
            >
              Jelentkezve
            </Button>
          )}
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
                  <Box>
                    <Heading as="h3" size="md" marginY={0.5} maxWidth="100%">
                      {participant.teamName}
                    </Heading>
                  </Box>
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
