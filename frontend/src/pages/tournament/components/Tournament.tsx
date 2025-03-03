import {TournamentDetailsView} from "../../../util/views/tournament.view.ts";
import {Tab, TabList, TabPanel, TabPanels, Tabs} from "@chakra-ui/react";
import KnockoutStage from "./KnockoutStage.tsx";
import {useState} from "react";


interface TournamentProps {
  tournament: TournamentDetailsView
}

const Tournament = ({tournament}: TournamentProps) => {


  const [tabIndex, setTabIndex] = useState(0)

  const onTabSelected = (i: number) => {
    setTabIndex(i)
  }

  return (
    <div>
      <h1>{tournament.tournament.title}</h1>
      <p>{tournament.tournament.description}</p>
      <p>{tournament.tournament.location}</p>
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
                <div key={participant.teamId}>
                  <h2>{participant.teamName}</h2>
                </div>
              ))
            }
          </TabPanel>
            {
              tournament.stages.map((stage) => (
                <TabPanel px={0}>
                  <KnockoutStage key={stage.id} stage={stage}/>
                </TabPanel>
              ))
            }
        </TabPanels>
      </Tabs>
    </div>
  )
}

export default Tournament
