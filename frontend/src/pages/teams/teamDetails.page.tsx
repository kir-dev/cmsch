import { Button, Divider, Flex, Heading, VStack } from '@chakra-ui/react'
import { Helmet } from 'react-helmet-async'
import { Navigate, useParams } from 'react-router-dom'

import { CustomBreadcrumb } from '../../common-components/CustomBreadcrumb'
import { CmschPage } from '../../common-components/layout/CmschPage'
import { AbsolutePaths } from '../../util/paths'
import { TeamMock } from './mock'
import { BoardStat } from '../../common-components/BoardStat'
import { MemberRow } from './components/MemberRow'

export default function TeamDetailsPage() {
  const { id } = useParams()
  const team = TeamMock.find((t) => t.id === id)
  if (!team) return <Navigate to={AbsolutePaths.TEAM} />
  const breadcrumbItems = [
    {
      title: 'Csapatok',
      to: AbsolutePaths.TEAM
    },
    {
      title: team.name
    }
  ]
  const isAdmin = true
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
      {isAdmin && team.applicants.length > 0 && (
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
