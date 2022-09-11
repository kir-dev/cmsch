import { Button, Divider, Flex, Heading, VStack } from '@chakra-ui/react'
import { Helmet } from 'react-helmet-async'
import { Navigate, useParams } from 'react-router-dom'

import { CustomBreadcrumb } from '../../common-components/CustomBreadcrumb'
import { CmschPage } from '../../common-components/layout/CmschPage'
import { AbsolutePaths } from '../../util/paths'
import { BoardStat } from '../../common-components/BoardStat'
import { MemberRow } from './components/MemberRow'
import { useTeamDetails } from '../../api/hooks/team/useTeamDetails'
import { useMyTeam } from '../../api/hooks/team/useMyTeam'
import { Loading } from '../../common-components/Loading'
import { l } from '../../util/language'
import { useServiceContext } from '../../api/contexts/service/ServiceContext'

export default function TeamDetailsPage() {
  const { id } = useParams()
  const { data: team, isLoading, error, isError } = useTeamDetails(id)
  const { data: myTeam, isLoading: isMyTeamLoading } = useMyTeam()
  const { sendMessage } = useServiceContext()

  if (isLoading || isMyTeamLoading) {
    return <Loading />
  }

  if (isError) {
    sendMessage(l('team-load-failed') + error.message)
    return <Navigate replace to={AbsolutePaths.ERROR} />
  }

  if (typeof team === 'undefined') {
    sendMessage(l('team-load-failed-contact-developers'))
    return <Navigate replace to={AbsolutePaths.ERROR} />
  }

  const breadcrumbItems = [
    {
      title: 'Csapatok',
      to: AbsolutePaths.TEAMS
    },
    {
      title: team.name
    }
  ]
  return (
    <CmschPage>
      <Helmet title={team.name} />
      <CustomBreadcrumb items={breadcrumbItems} mt={5} />
      <Flex justify="space-between" wrap="wrap">
        <VStack>
          <Heading>{team.name}</Heading>
          <BoardStat label="Csapat pont" value={team.points} />
        </VStack>
        <VStack mt={10}>
          <Button colorScheme="brand">Jelentkezés a csapatba</Button>
        </VStack>
      </Flex>
      {team.applicants.length > 0 && (
        <>
          <Divider mt={10} borderWidth={2} />
          <Heading fontSize="lg">Jelentkezők</Heading>
          {team.applicants.map((m) => (
            <MemberRow member={m} onAccept={() => {}} onDelete={() => {}} />
          ))}
        </>
      )}
      <Divider mt={10} borderWidth={2} />
      <Heading fontSize="lg">Csapattagok</Heading>
      {team.members.map((m) => (
        <MemberRow member={m} onDelete={() => {}} onRoleChange={() => {}} />
      ))}
    </CmschPage>
  )
}
